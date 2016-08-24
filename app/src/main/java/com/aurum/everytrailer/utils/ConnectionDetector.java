package com.aurum.everytrailer.utils;

import android.os.AsyncTask;
import android.util.Log;

import java.net.HttpURLConnection;
import java.net.URL;

/**
 * Created by VarunBarve on 11/30/2015.
 */
public class ConnectionDetector extends AsyncTask<Void, Boolean, Boolean> {

    public static String statusStr, passStr;
    String result;

    @Override
    protected Boolean doInBackground(Void... params) {

        try {
            URL url = new URL("http://192.168.168.5:8096/landingScreen.svc/passKeyGenerate");

            HttpURLConnection conn = (HttpURLConnection) url.openConnection();
            conn.setDoInput(true);
            conn.setDoOutput(true);
            conn.setRequestMethod("POST");
            conn.setRequestProperty("Accept", "application/json");
            conn.setRequestProperty("Content-type", "application/json");

            int responseCode = conn.getResponseCode();

            if (responseCode == 200) {
                return true;
            } else {
                return false;
            }

        } catch (Exception e) {
            Log.e("STRING ENTITY ERROR: ", "STRING ENTITY ERROR: " + e);
            return false;
        }
    }
}
