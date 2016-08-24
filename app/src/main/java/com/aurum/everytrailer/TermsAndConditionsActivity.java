package com.aurum.everytrailer;

import android.app.ProgressDialog;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.text.Html;
import android.util.Log;
import android.view.MenuItem;
import android.webkit.WebView;
import android.widget.Toast;

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

public class TermsAndConditionsActivity extends AppCompatActivity {

    WebView terms_and_conditions_web_view;
    String termsAndConditionsWebViewStr;
    ConnectionDetector connectionDetector;
    ProgressDialog mProgressDialog;
    String privateKey;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_terms_and_conditions);

        privateKey = Constants.getPrivateKey();

        terms_and_conditions_web_view = (WebView) findViewById(R.id.terms_and_conditions_web_view);

        Toolbar toolbar = (Toolbar) findViewById(R.id.terms_and_conditions_toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayShowTitleEnabled(true);
        getSupportActionBar().setDisplayShowHomeEnabled(true);
        getSupportActionBar().setTitle("Terms And Conditions");
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

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
            new GetNewsList().execute();
        } else {
            Toast.makeText(TermsAndConditionsActivity.this, "No internet connection.", Toast.LENGTH_SHORT).show();
        }


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

    class GetNewsList extends AsyncTask<Void, Void, Void> {

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            mProgressDialog = new ProgressDialog(TermsAndConditionsActivity.this);
            mProgressDialog.setMessage("Loading...");
            mProgressDialog.setCancelable(false);
            mProgressDialog.setCanceledOnTouchOutside(false);
            mProgressDialog.show();
        }

        @Override

        protected Void doInBackground(Void... params) {
            try {
                URL url = new URL(Constants.URL_TERMS_AND_CONDITIONS);

                HttpURLConnection con = (HttpURLConnection) url
                        .openConnection();
                con.setRequestMethod("POST");
                con.setRequestProperty("Content-Type", "application/json");
                con.setRequestProperty("Accept", "application/json");
                con.connect();

                // SET PARAMS
                OutputStream os = con.getOutputStream();
                OutputStreamWriter osw = new OutputStreamWriter(os, "UTF-8");

                String requestXml = "<?xml version='1.0' encoding='utf-8' ?> <everyTrailler>   <merchandDetail>     <merchandID>       mobileapp     </merchandID>     <password>    " + privateKey + "    </password>   </merchandDetail>   <inputData>     <userID>1</userID>   </inputData> </everyTrailler>";

                String paramsStr = "{\"xmlString\":\"" + requestXml + "\"}";

                osw.write(paramsStr);
                osw.flush();
                osw.close();

                String resultPlain = Constants.readStream(con.getInputStream());

                String resultHtml = Html.fromHtml(resultPlain).toString();

                termsAndConditionsWebViewStr = resultHtml.replace("&", "and");

                Log.e(Constants.TAG, "TERMS AND CONDITIONS: " + termsAndConditionsWebViewStr);

            } catch (Exception e) {
                Log.e(Constants.TAG,
                        "JSON OBJECT ERROR: " + e);
            }
            return null;
        }

        @Override
        protected void onPostExecute(Void aVoid) {
            super.onPostExecute(aVoid);

            String tAndC = null, privacyPolicy = null;

            XMLParser parser = new XMLParser();
            Document doc = parser.getDomElement(termsAndConditionsWebViewStr);

            NodeList everyTrailerResponse = doc.getElementsByTagName(Constants.KEY_EVERY_TRAILER);

            for (int i = 0; i < everyTrailerResponse.getLength(); i++) {
                Element everyTrailerElement = (Element) everyTrailerResponse.item(i);

                String status = parser.getValue(everyTrailerElement, Constants.KEY_STATUS);

                if (status.equalsIgnoreCase("1")) {

                    tAndC = parser.getValue(everyTrailerElement, Constants.TERM_AND_CONDITION);
                    privacyPolicy = parser.getValue(everyTrailerElement, Constants.PRIVACY_POLICY);

                } else {
                    String msg = parser.getValue(everyTrailerElement, Constants.KEY_MESSAGE);
                }
            }

            String completeTAndC = tAndC + "\n\n" + privacyPolicy;

            terms_and_conditions_web_view.loadData(completeTAndC, "text/html", "utf-8");

            mProgressDialog.dismiss();

        }
    }

}
