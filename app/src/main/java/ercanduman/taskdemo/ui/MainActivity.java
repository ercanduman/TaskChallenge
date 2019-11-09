package ercanduman.taskdemo.ui;

import android.content.Context;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.android.material.snackbar.Snackbar;

import java.lang.ref.WeakReference;

import ercanduman.taskdemo.R;
import ercanduman.taskdemo.util.NetworkConnection;
import ercanduman.taskdemo.util.ProcessListener;

public class MainActivity extends AppCompatActivity implements ProcessListener {
    private static final String TAG = "MainActivity";
    private DataLoading dataLoading;

    private TextView mainContent;
    private ProgressBar loadingBar;
    private boolean isDataLoaded;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        mainContent = findViewById(R.id.activity_main_content_view);
        loadingBar = findViewById(R.id.activity_main_progress_bar);

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
        if (id == R.id.action_settings) {
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    @Override
    public void onStarted() {
        if (loadingBar != null) loadingBar.setVisibility(View.VISIBLE);
    }

    @Override
    public void onFinished(String data) {
        isDataLoaded = true;
        mainContent.setText(data);
        if (loadingBar != null) loadingBar.setVisibility(View.GONE);
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
