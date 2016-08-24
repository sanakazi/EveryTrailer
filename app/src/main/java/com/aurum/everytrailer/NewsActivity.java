package com.aurum.everytrailer;

import android.app.ProgressDialog;
import android.content.Intent;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.text.Html;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.Toast;

import com.aurum.everytrailer.adapter.NewsAdapter;
import com.aurum.everytrailer.bean.NewsBean;
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

public class NewsActivity extends AppCompatActivity {

    ListView newsListView;
    ArrayList<NewsBean> list;
    NewsAdapter adapter;
    ProgressDialog mProgressDialog;
    ConnectionDetector connectionDetector;
    String privateKey;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_news);

        privateKey = Constants.getPrivateKey();

        Toolbar toolbar = (Toolbar) findViewById(R.id.newsactivity);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayShowTitleEnabled(true);
        getSupportActionBar().setDisplayShowHomeEnabled(true);
        getSupportActionBar().setTitle("News");
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        newsListView = (ListView) findViewById(R.id.news_list);

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
            Toast.makeText(NewsActivity.this, "No internet connection.", Toast.LENGTH_SHORT).show();
        }

        newsListView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                try {
                    String url = list.get(position).getReadMore();
                    Intent i = new Intent(Intent.ACTION_VIEW);
                    i.setData(Uri.parse(url));
                    startActivity(i);
                } catch (Exception e) {
                    Toast.makeText(NewsActivity.this, "No Activity Found", Toast.LENGTH_SHORT).show();
                }
            }
        });
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

        list = new ArrayList<NewsBean>();

        XMLParser parser = new XMLParser();
        Document doc = parser.getDomElement(MovieListXml);

        NodeList everyTrailerResponse = doc.getElementsByTagName(Constants.KEY_EVERY_TRAILER);

        for (int i = 0; i < everyTrailerResponse.getLength(); i++) {
            Element everyTrailerElement = (Element) everyTrailerResponse.item(i);

            if (parser.getValue(everyTrailerElement, Constants.KEY_STATUS).equalsIgnoreCase("1")) {

                // NEWS LIST
                NodeList newsNodeList = everyTrailerElement.getElementsByTagName(Constants.KEY_NEWS);
                for (int j = 0; j < newsNodeList.getLength(); j++) {
                    Element newsElement = (Element) newsNodeList.item(j);

                    NodeList newsListNodeList = newsElement.getElementsByTagName(Constants.KEY_NEWS_LIST);
                    for (int k = 0; k < newsListNodeList.getLength(); k++) {
                        Element newsListElement = (Element) newsListNodeList.item(k);

                        String newsTitle = parser.getValue(newsListElement, Constants.KEY_NEWS_TITLE);
                        String newsLandingImgPath = parser.getValue(newsListElement, Constants.KEY_LANDING_IMG_PATH);
                        String newsDesc = parser.getValue(newsListElement, Constants.KEY_NEWS_DESC);
                        String newsDate = parser.getValue(newsListElement, Constants.KEY_NEWS_DATE);
                        String readMore = parser.getValue(newsListElement, Constants.KEY_READ_MORE);

                        list.add(new NewsBean(newsLandingImgPath, newsTitle, newsDesc, newsDate, readMore));
                    }
                }
            } else {
            }
        }
    }

    class GetNewsList extends AsyncTask<Void, Void, Void> {

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            mProgressDialog = new ProgressDialog(NewsActivity.this);
            mProgressDialog.setMessage("Loading Movie Lists");
            mProgressDialog.setCancelable(false);
            mProgressDialog.setCanceledOnTouchOutside(false);
            mProgressDialog.show();
        }

        @Override

        protected Void doInBackground(Void... params) {
            try {
                URL url = new URL(Constants.URL_NEWS_LIST);

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

            adapter = new NewsAdapter(NewsActivity.this, list);

            newsListView.setAdapter(adapter);

        }
    }
}
