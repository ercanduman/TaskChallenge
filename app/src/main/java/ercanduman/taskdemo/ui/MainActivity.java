package ercanduman.taskdemo.ui;

import android.content.Context;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.android.material.snackbar.Snackbar;

import java.lang.ref.WeakReference;

import ercanduman.taskdemo.R;
import ercanduman.taskdemo.util.NetworkConnection;

public class MainActivity extends AppCompatActivity {
    private static final String TAG = "MainActivity";
    private DataLoading dataLoading;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        dataLoading = new DataLoading(this);
        FloatingActionButton fab = findViewById(R.id.fab);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Snackbar.make(view, "Get data from network?", Snackbar.LENGTH_LONG)
                        .setAction("Yes", new View.OnClickListener() {
                            @Override
                            public void onClick(View v) {
                                getDataFromNetwork();
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
        if (dataLoading != null) dataLoading.execute();

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

    static class DataLoading extends AsyncTask<Void, Void, String> {
        private NetworkConnection connection;
        private WeakReference<Context> context;

        DataLoading(Context context) {
            this.context = new WeakReference<>(context);
            connection = new NetworkConnection();
        }

        @Override
        protected void onPreExecute() {
            Log.d(TAG, "DataLoading - Begin!");
        }

        @Override
        protected String doInBackground(Void... voids) {
            Log.d(TAG, "DataLoading - Executing...");
            return connection.getDataFromUrl(context.get());
        }

        @Override
        protected void onPostExecute(String result) {
            Log.d(TAG, "onPostExecute: result: " + result);
            Log.d(TAG, "DataLoading - End!");
        }
    }
}
