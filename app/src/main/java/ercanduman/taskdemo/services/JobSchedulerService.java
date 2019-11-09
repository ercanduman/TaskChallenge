package ercanduman.taskdemo.services;

import android.app.job.JobParameters;
import android.app.job.JobService;
import android.content.Intent;
import android.content.IntentFilter;
import android.util.Log;

import ercanduman.taskdemo.Constants;

public class JobSchedulerService extends JobService {
    private static final String TAG = "JobSchedulerService";
    private boolean isJobCancelled;
    private UserPresentBroadcastReceiver broadcastReceiver = new UserPresentBroadcastReceiver();

    @Override
    public void onCreate() {
        super.onCreate();
        IntentFilter broadcastIntent = new IntentFilter(Constants.BROADCAST_ACTION);
        registerReceiver(broadcastReceiver, broadcastIntent);
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        unregisterReceiver(broadcastReceiver);
    }

    @Override
    public boolean onStartJob(JobParameters params) {
        Log.d(TAG, "onStartJob: called");
        if (!isJobCancelled) {
            Log.d(TAG, "onStartJob: called for NON canceled job...");
            Intent broadcastIntent = new Intent();
            broadcastIntent.setAction(Constants.BROADCAST_ACTION);
            broadcastIntent.putExtra(Constants.EXTRA_INPUT, String.valueOf(System.currentTimeMillis()));
            sendBroadcast(broadcastIntent);
        } else {
            Log.d(TAG, "onStartJob: called for canceled job...");
        }
        return true;
    }

    @Override
    public boolean onStopJob(JobParameters params) {
        Log.d(TAG, "onStopJob: called");
        isJobCancelled = true;
        return true;
    }
}
