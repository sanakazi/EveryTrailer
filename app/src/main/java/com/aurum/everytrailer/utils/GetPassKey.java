package com.aurum.everytrailer.utils;

import android.os.AsyncTask;
import android.text.Html;
import android.util.Log;

import org.json.JSONObject;
import org.json.XML;

import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.URL;

/**
 * Created by varunbarve on 02/03/2016.
 */
public class GetPassKey extends AsyncTask<Void, String[], String[]> {

    String result;

    @Override
    protected String[] doInBackground(Void... params) {

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

                InputStream in = conn.getErrorStream();
                if (in == null) {
                    in = conn.getInputStream();
                }

                String resultPlain = Constants.readStream(in);

                result = Html.fromHtml(resultPlain).toString();

                Log.e("PASS KEY CALL: ", "PASS KEY CALL: " + result);


                JSONObject rootJsonObject = XML.toJSONObject(result);

                Log.e("JSON RESPONSE: ", "JSON RESPONSE: " + result);

                JSONObject everyTrailerJsonObject = rootJsonObject.getJSONObject("everyTrailler");
                JSONObject keyJsonObject = everyTrailerJsonObject.getJSONObject("key");

                String statusStr = keyJsonObject.getString("status");
                String passStr = keyJsonObject.getString("pass");

                return new String[]{statusStr, passStr};
            } else {
                return new String[]{null};
            }

        } catch (Exception e) {
            Log.e("STRING ENTITY ERROR: ", "STRING ENTITY ERROR: " + e);
            return new String[]{null};
        }
    }
}
