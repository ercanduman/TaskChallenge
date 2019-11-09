package ercanduman.taskdemo.ui;

import android.app.job.JobInfo;
import android.app.job.JobScheduler;
import android.content.ComponentName;
import android.content.Context;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.ProgressBar;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.android.material.snackbar.Snackbar;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.lang.ref.WeakReference;
import java.util.ArrayList;
import java.util.List;

import ercanduman.taskdemo.Constants;
import ercanduman.taskdemo.R;
import ercanduman.taskdemo.data.Album;
import ercanduman.taskdemo.services.JobSchedulerService;
import ercanduman.taskdemo.util.NetworkConnection;
import ercanduman.taskdemo.util.ProcessListener;

public class MainActivity extends AppCompatActivity implements ProcessListener {
    private static final String TAG = "MainActivity";
    private DataLoading dataLoading;

    private ProgressBar loadingBar;
    private boolean isDataLoaded;
    private ListView listView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        loadingBar = findViewById(R.id.activity_main_progress_bar);
        listView = findViewById(R.id.activity_main_list_view);

        dataLoading = new DataLoading(this, this);

        FloatingActionButton fab = findViewById(R.id.fab);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Snackbar.make(view, "Get data from network?", Snackbar.LENGTH_LONG)
                        .setAction("Yes", new View.OnClickListener() {
                            @Override
                            public void onClick(View v) {
                                if (NetworkConnection.isNetworkAvailable(MainActivity.this)) {
                                    getDataFromNetwork();
                                } else {
                                    Toast.makeText(MainActivity.this, "No Network Available!", Toast.LENGTH_SHORT).show();
                                }
                            }
                        }).show();
            }
        });


    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        if (dataLoading != null) dataLoading.cancel(true);
    }

    private void getDataFromNetwork() {
        if (dataLoading != null && !isDataLoaded) dataLoading.execute();
        else Toast.makeText(this, "Data is already loaded!", Toast.LENGTH_SHORT).show();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_start_job_scheduler) {
            startJobScheduler();
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    private void startJobScheduler() {
        Log.d(TAG, "startJobScheduler: called");
        ComponentName componentName = new ComponentName(this, JobSchedulerService.class);
        JobInfo jobInfo = new JobInfo.Builder(Constants.BROADCAST_JOB_ID, componentName)
                .setMinimumLatency(3 * 1000) // Wait at least 30s
                .setOverrideDeadline(1000)
                .build();

        JobScheduler scheduler = (JobScheduler) getSystemService(JOB_SCHEDULER_SERVICE);
        if (scheduler != null) {
            int resultCode = scheduler.schedule(jobInfo);
            if (resultCode == JobScheduler.RESULT_SUCCESS) {
                Toast.makeText(this, "Job Scheduled!", Toast.LENGTH_SHORT).show();
            } else if (resultCode == JobScheduler.RESULT_FAILURE) {
                Toast.makeText(this, "Job Scheduling Failed!", Toast.LENGTH_SHORT).show();
            } else {
                Toast.makeText(this, "Unknown result code! resultCode: " + resultCode, Toast.LENGTH_SHORT).show();
            }
        }
    }

    @Override
    public void onStarted() {
        if (loadingBar != null) loadingBar.setVisibility(View.VISIBLE);
    }

    @Override
    public void onFinished(String data) {
        isDataLoaded = true;
        handleJsonData(data);
        if (loadingBar != null) loadingBar.setVisibility(View.GONE);
    }

    private void handleJsonData(String data) {
        List<Album> albumList = new ArrayList<>();
        try {
            JSONArray jsonArray = new JSONArray(data);
            for (int i = 0; i < jsonArray.length(); i++) {
                JSONObject albumObject = (JSONObject) jsonArray.get(i);

                Album album = new Album();
                album.setUserId(albumObject.getString("userId"));
                album.setTitle(albumObject.getString("title"));
                album.setId(albumObject.getString("id"));
                albumList.add(album);
            }
            handleAlbumListAndShowOnList(albumList);
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }

    private void handleAlbumListAndShowOnList(List<Album> albumList) {
        ArrayAdapter<Album> adapter = new ArrayAdapter<>(this,
                android.R.layout.simple_list_item_1, albumList);
        listView.setAdapter(adapter);
    }

    static class DataLoading extends AsyncTask<Void, Void, String> {
        private NetworkConnection connection;
        private WeakReference<Context> context;
        private ProcessListener listener;

        DataLoading(Context context, ProcessListener listener) {
            this.context = new WeakReference<>(context);
            connection = new NetworkConnection();
            this.listener = listener;
        }

        @Override
        protected void onPreExecute() {
            Log.d(TAG, "DataLoading - Begin!");
            listener.onStarted();
        }

        @Override
        protected String doInBackground(Void... voids) {
            Log.d(TAG, "DataLoading - Executing...");
            return connection.getDataFromUrl(context.get());
        }

        @Override
        protected void onPostExecute(String result) {
            Log.d(TAG, "onPostExecute: result: " + result);
            listener.onFinished(result);
            Log.d(TAG, "DataLoading - End!");
        }
    }
}
