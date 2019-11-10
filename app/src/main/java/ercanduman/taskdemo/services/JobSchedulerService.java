package ercanduman.taskdemo.services;

import android.app.AlarmManager;
import android.app.PendingIntent;
import android.app.job.JobParameters;
import android.app.job.JobService;
import android.content.Intent;
import android.content.IntentFilter;
import android.util.Log;

import ercanduman.taskdemo.Constants;
import ercanduman.taskdemo.util.Preferences;

public class JobSchedulerService extends JobService {
    private static final String TAG = "JobSchedulerService";
    private boolean isJobCancelled;
    private UserPresentBroadcastReceiver broadcastReceiver = new UserPresentBroadcastReceiver();

    @Override
    public void onCreate() {
        super.onCreate();
        Log.d(TAG, "onCreate: called");
        IntentFilter broadcastIntent = new IntentFilter(Constants.BROADCAST_ACTION);
        registerReceiver(broadcastReceiver, broadcastIntent);
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        Log.d(TAG, "onDestroy: called.");
        unregisterReceiver(broadcastReceiver);
    }

    @Override
    public boolean onStartJob(JobParameters params) {
        Log.d(TAG, "onStartJob: called");
        if (!isJobCancelled) {
            Log.d(TAG, "onStartJob: called for NON canceled job...");
            Intent broadcastIntent = new Intent();
            broadcastIntent.setAction(Constants.BROADCAST_ACTION);
            broadcastIntent.putExtra(Constants.EXTRA_INPUT, Preferences.getSavedTime());
//            sendBroadcast(broadcastIntent);

            PendingIntent sendBroadcastIntent = PendingIntent.getBroadcast(this, 0, broadcastIntent, 0);
            //Schedule alarm
            AlarmManager alarmManager = (AlarmManager) this.getSystemService(ALARM_SERVICE);
            if (alarmManager != null) {
                alarmManager.setRepeating(AlarmManager.ELAPSED_REALTIME_WAKEUP,
                        5000,
                        60000,
                        sendBroadcastIntent);
            }
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
