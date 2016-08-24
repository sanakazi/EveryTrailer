package com.aurum.everytrailer;

import android.app.ProgressDialog;
import android.content.Intent;
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

import com.aurum.everytrailer.adapter.LanguageMoviesAdapter;
import com.aurum.everytrailer.bean.LanguageBean;
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

public class LanguageMoviesActivity extends AppCompatActivity {

    ListView mLanguageListView;
    ProgressDialog mProgressDialog;
    ArrayList<LanguageBean> list;
    String languageId, language;
    LanguageMoviesAdapter adapter;
    ConnectionDetector connectionDetector;
    String privateKey;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_language_movies);

        privateKey = Constants.getPrivateKey();

        Toolbar toolbar = (Toolbar) findViewById(R.id.language_activity);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayShowTitleEnabled(true);
        getSupportActionBar().setDisplayShowHomeEnabled(true);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        languageId = getIntent().getStringExtra("Language ID");
        language = getIntent().getStringExtra("Language");

        getSupportActionBar().setTitle(language);

        mLanguageListView = (ListView) findViewById(R.id.language_list_view);

        mLanguageListView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                Intent intent = new Intent(LanguageMoviesActivity.this, Youtube_Activity.class);
                intent.putExtra("Movie Name", list.get(position).getMovieName());
                intent.putExtra("Movie ID", list.get(position).getId());
                intent.putExtra("Trailer Type", list.get(position).getTrailerType());
                startActivity(intent);
                finish();
            }
        });

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
            new GetMovieList().execute();
        } else {
            Toast.makeText(LanguageMoviesActivity.this, "No internet connection.", Toast.LENGTH_SHORT).show();
        }


    }

    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == android.R.id.home) {
            onBackPressed();
            return true;
        }
        return super.onOptionsItemSelected(item);
    }

    private void parseMovieListXML(String MovieListXml) {

        list = new ArrayList<LanguageBean>();

        XMLParser parser = new XMLParser();
        Document doc = parser.getDomElement(MovieListXml);

        NodeList everyTrailerResponse = doc.getElementsByTagName(Constants.KEY_EVERY_TRAILER);

        for (int i = 0; i < everyTrailerResponse.getLength(); i++) {
            Element everyTrailerElement = (Element) everyTrailerResponse.item(i);

            if (parser.getValue(everyTrailerElement, Constants.KEY_STATUS).equalsIgnoreCase("1")) {

                // LANGUAGE LIST
                NodeList featuredNodeList = everyTrailerElement.getElementsByTagName(Constants.KEY_LANGUAGE_MOVIES);
                for (int j = 0; j < featuredNodeList.getLength(); j++) {
                    Element languageElement = (Element) featuredNodeList.item(j);

                    NodeList languageMovieListNodeList = languageElement.getElementsByTagName(Constants.KEY_MOVIE_LIST);
                    for (int k = 0; k < languageMovieListNodeList.getLength(); k++) {
                        Element languageMovieListElement = (Element) languageMovieListNodeList.item(k);

                        String languagetrailerId = parser.getValue(languageMovieListElement, Constants.KEY_TRAILER_ID);
                        String languageLandingImgPath = parser.getValue(languageMovieListElement, Constants.KEY_LANDING_IMG_PATH);
                        String languageViewCounts = parser.getValue(languageMovieListElement, Constants.KEY_VIEWS_COUNT);
                        String languageLikesPer = parser.getValue(languageMovieListElement, Constants.KEY_LIKES_PER);
                        String languageTrailerType = parser.getValue(languageMovieListElement, Constants.KEY_TRAILER_TYPE);
                        String languageTrailerTitle = parser.getValue(languageMovieListElement, Constants.KEY_TRAILER_TITLE);
                        String languageMovieName = parser.getValue(languageMovieListElement, Constants.KEY_MOVIE_NAME);

                        list.add(new LanguageBean(languagetrailerId, languageViewCounts, languageLikesPer, languageLandingImgPath, languageTrailerTitle, languageMovieName, languageTrailerType));
                    }
                }
            } else {
            }
        }
    }

    class GetMovieList extends AsyncTask<Void, Void, Void> {

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            mProgressDialog = new ProgressDialog(LanguageMoviesActivity.this);
            mProgressDialog.setMessage("Loading Movie Lists");
            mProgressDialog.setCancelable(false);
            mProgressDialog.setCanceledOnTouchOutside(false);
            mProgressDialog.show();
        }

        @Override

        protected Void doInBackground(Void... params) {
            try {
                URL url = new URL(Constants.URL_LANGUAGE_WISE_MOVIES);

                HttpURLConnection con = (HttpURLConnection) url
                        .openConnection();
                con.setRequestMethod("POST");
                con.setRequestProperty("Content-Type", "application/json");
                con.setRequestProperty("Accept", "application/json");
                con.connect();

                // SET PARAMS
                OutputStream os = con.getOutputStream();
                OutputStreamWriter osw = new OutputStreamWriter(os, "UTF-8");

                String languageWiseMovieRequestXml = "<?xml version='1.0' encoding='utf-8'?><everyTrailler><inputData><languageID>" + languageId + "</languageID><pageNO>1</pageNO></inputData></everyTrailler>";

                String paramsStr = "{\"pass\":\"" + privateKey + "\",\"langMovies\":\"" + languageWiseMovieRequestXml + "\"}";

                osw.write(paramsStr);
                osw.flush();
                osw.close();

                String resultPlain = Constants.readStream(con.getInputStream());

                String resultHtml = Html.fromHtml(resultPlain).toString();

                String result = resultHtml.replace("&", "and");

                parseMovieListXML(result);

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

            adapter = new LanguageMoviesAdapter(LanguageMoviesActivity.this, list);

            mLanguageListView.setAdapter(adapter);

        }
    }
}
