package ercanduman.taskdemo.util;

import android.content.Context;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.widget.Toast;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;

import ercanduman.taskdemo.Constants;

public class NetworkConnection {
    private boolean isNetworkAvailable(Context context) {
        ConnectivityManager manager = (ConnectivityManager) context.getApplicationContext().getSystemService(Context.CONNECTIVITY_SERVICE);
        if (manager == null) return false;

        NetworkInfo networkInfo = manager.getActiveNetworkInfo();
        NetworkInfo networkInfoWifi = manager.getNetworkInfo(ConnectivityManager.TYPE_WIFI);
        NetworkInfo networkInfoMobile = manager.getNetworkInfo(ConnectivityManager.TYPE_MOBILE);

        return networkInfo != null && networkInfo.isConnectedOrConnecting()
                || networkInfoWifi != null && networkInfoWifi.isConnectedOrConnecting()
                || networkInfoMobile != null && networkInfoMobile.isConnectedOrConnecting();
    }

    public String getDataFromUrl(Context context) {
        if (isNetworkAvailable(context)) {
            HttpURLConnection connection;
            StringBuilder builder;
            BufferedReader reader = null;
            try {
                URL url = new URL(Constants.BASE_URL);
                connection = (HttpURLConnection) url.openConnection();
                builder = new StringBuilder();
                reader = new BufferedReader(new InputStreamReader(connection.getInputStream()));

                String line;
                while ((line = reader.readLine()) != null) {
                    builder.append(line);
                    builder.append("\n");
                }
                return builder.toString();
            } catch (IOException e) {
                e.printStackTrace();
                return e.getMessage();
            } finally {
                try {
                    if (reader != null) reader.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        } else {
            String message = "No Network Available!";
            Toast.makeText(context, message, Toast.LENGTH_SHORT).show();
            return message;
        }
    }
}
