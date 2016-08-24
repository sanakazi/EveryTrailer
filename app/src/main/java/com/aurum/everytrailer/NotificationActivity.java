package com.aurum.everytrailer;

import android.app.ProgressDialog;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.text.Html;
import android.util.Log;
import android.view.MenuItem;
import android.widget.ListView;
import android.widget.Toast;

import com.aurum.everytrailer.adapter.NotificationAdapter;
import com.aurum.everytrailer.bean.NotificationBean;
import com.aurum.everytrailer.utils.ConnectionDetector;
import com.aurum.everytrailer.utils.Constants;
import com.aurum.everytrailer.utils.XMLParser;

import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.NodeList;

import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;

public class NotificationActivity extends AppCompatActivity {

    ArrayList<NotificationBean> list;
    ListView notificationList;
    NotificationAdapter adapter;
    ProgressDialog mProgressDialog;
    ConnectionDetector connectionDetector;
    String privateKey;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_notification);

        privateKey = Constants.getPrivateKey();

        Toolbar toolbar = (Toolbar) findViewById(R.id.notificationactivity);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayShowTitleEnabled(true);
        getSupportActionBar().setDisplayShowHomeEnabled(true);
        getSupportActionBar().setTitle("Notification");
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        notificationList = (ListView) findViewById(R.id.notification_list);

        list = new ArrayList<NotificationBean>();

        // CONNECTION DETECTOR
        AsyncTask<Void, Boolean, Boolean> connectionDetectorTask = new ConnectionDetector()
                .execute();

        boolean result = false;
        try {
            result = connectionDetectorTask.get();
        } catch (Exception e) {
            e.printStackTrace();
        }

        if (result) {
            new GetNotificationList().execute();
        } else {
            Toast.makeText(NotificationActivity.this, "No internet connection.", Toast.LENGTH_SHORT).show();
        }
    }

    public int getStatusBarHeight() {
        int result = 0;
        int resourceId = getResources().getIdentifier("status_bar_height", "dimen", "android");
        if (resourceId > 0) {
            result = getResources().getDimensionPixelSize(resourceId);
        }
        return result;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == android.R.id.home) {
            onBackPressed();
            return true;
        }
        return super.onOptionsItemSelected(item);
    }

    private void parseNewsListXML(String MovieListXml) {

        list = new ArrayList<NotificationBean>();

        XMLParser parser = new XMLParser();
        Document doc = parser.getDomElement(MovieListXml);

        NodeList everyTrailerResponse = doc.getElementsByTagName("notification");

        for (int i = 0; i < everyTrailerResponse.getLength(); i++) {
            Element everyTrailerElement = (Element) everyTrailerResponse.item(i);

            if (parser.getValue(everyTrailerElement, Constants.KEY_STATUS).equalsIgnoreCase("1")) {

                // NEWS LIST
                NodeList notificationListNodeList = everyTrailerElement.getElementsByTagName("notificationList");
                for (int j = 0; j < notificationListNodeList.getLength(); j++) {
                    Element notificationListElement = (Element) notificationListNodeList.item(j);

                    String notificationListTitle = parser.getValue(notificationListElement, "title");
                    String notificationListDate = parser.getValue(notificationListElement, "dates");
                    String notificationListTime = parser.getValue(notificationListElement, "time");

                    list.add(new NotificationBean(notificationListTitle, notificationListDate, notificationListTime));
                }
            } else {
            }
        }
    }

    class GetNotificationList extends AsyncTask<Void, Void, Void> {

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            mProgressDialog = new ProgressDialog(NotificationActivity.this);
            mProgressDialog.setMessage("Loading Movie Lists");
            mProgressDialog.setCancelable(false);
            mProgressDialog.setCanceledOnTouchOutside(false);
            mProgressDialog.show();
        }

        @Override

        protected Void doInBackground(Void... params) {
            try {
                URL url = new URL(Constants.URL_NOTIFICATIONS);

                HttpURLConnection con = (HttpURLConnection) url
                        .openConnection();
                con.setRequestMethod("POST");
                con.setRequestProperty("Content-Type", "application/json");
                con.setRequestProperty("Accept", "application/json");
                con.connect();

                // SET PARAMS
                OutputStream os = con.getOutputStream();
                OutputStreamWriter osw = new OutputStreamWriter(os, "UTF-8");

                String paramsStr = "{\"pass\":\"" + privateKey + "\",\"pageIndex\":\"" + 1 + "\"}";

                osw.write(paramsStr);
                osw.flush();
                osw.close();

                String resultPlain = Constants.readStream(con.getInputStream());

                String resultHtml = Html.fromHtml(resultPlain).toString();

                String result = resultHtml.replace("&", "and");

                parseNewsListXML(result);

            } catch (Exception e) {
                Log.e(Constants.TAG,
                        "JSON OBJECT ERROR: " + e);
            }
            return null;
        }

        @Override
        protected void onPostExecute(Void aVoid) {
            super.onPostExecute(aVoid);
            mProgressDialog.dismiss();

            adapter = new NotificationAdapter(NotificationActivity.this, list);

            notificationList.setAdapter(adapter);

        }
    }
}
