package ercanduman.taskdemo.services;

import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.os.Build;
import android.util.Log;

import androidx.core.app.NotificationCompat;

import ercanduman.taskdemo.Constants;
import ercanduman.taskdemo.R;

public class UserPresentBroadcastReceiver extends BroadcastReceiver {
    private static final String TAG = "UserPresentBroadcast";

    @Override
    public void onReceive(Context context, Intent intent) {
        if (Constants.BROADCAST_ACTION.equals(intent.getAction())) {
            String passedData = intent.getStringExtra(Constants.EXTRA_INPUT);

            long lastTime = 0;
            if (passedData != null) {
                lastTime = Long.valueOf(passedData);
            }
            long timePassed = System.currentTimeMillis() - lastTime;
            int seconds = (int) ((timePassed / 1000) % 60);
            long minutes = ((timePassed - seconds) / 1000) / 60;
            Log.d(TAG, "onReceive: minutes: " + minutes);
            Log.d(TAG, "onReceive: seconds: " + seconds);
            NotificationCompat.Builder notificationBuilder = new NotificationCompat.Builder(context, Constants.CHANNEL_ID)
                    .setContentTitle("TaskDemo App Broadcast Receiver")
                    .setContentText("Time passed so far: " + minutes + " (minutes) and "
                            + seconds + " (seconds)")
                    .setSmallIcon(R.drawable.ic_launcher_background);

            NotificationManager manager = (NotificationManager) context.getSystemService(Context.NOTIFICATION_SERVICE);
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                notificationBuilder.setChannelId(Constants.CHANNEL_ID);
                NotificationChannel channel = new NotificationChannel(Constants.CHANNEL_ID,
                        context.getString(R.string.app_name), NotificationManager.IMPORTANCE_DEFAULT);
                if (manager != null) manager.createNotificationChannel(channel);
            }
            if (manager != null) {
                manager.notify(1, notificationBuilder.build());
            } else {
                Log.d(TAG, "onReceive: cannot show notification...");
            }
        } else {
            Log.d(TAG, "onReceive: called for different actions.... action: " + intent.getAction());
        }
    }
}
