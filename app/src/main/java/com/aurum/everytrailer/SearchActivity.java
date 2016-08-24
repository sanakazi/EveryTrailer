package com.aurum.everytrailer;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.text.Html;
import android.util.Log;
import android.view.KeyEvent;
import android.view.View;
import android.view.inputmethod.EditorInfo;
import android.view.inputmethod.InputMethodManager;
import android.widget.AdapterView;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ListView;
import android.widget.TextView;
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

public class SearchActivity extends AppCompatActivity {

    ListView mSearchList;
    EditText movieSearchEdit;
    ImageButton movieSearchBtn;
    ProgressDialog mProgressDialog;
    String movieSearchStr, status, msz;
    ArrayList<LanguageBean> list;
    LanguageMoviesAdapter adapter;
    ConnectionDetector connectionDetector;
    String privateKey;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_search);

        privateKey = Constants.getPrivateKey();

        Toolbar toolbar = (Toolbar) findViewById(R.id.search_toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayShowTitleEnabled(false);
        getSupportActionBar().setDisplayShowHomeEnabled(true);

        mSearchList = (ListView) findViewById(R.id.search_list);

        movieSearchBtn = (ImageButton) findViewById(R.id.movie_search_btn);
        movieSearchEdit = (EditText) findViewById(R.id.movie_search_edit);

        movieSearchBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                String searchMovieValue = movieSearchEdit.getText().toString().trim();

                if (searchMovieValue.equalsIgnoreCase("")) {
                    Toast.makeText(SearchActivity.this, "Please type a movie name to search", Toast.LENGTH_SHORT).show();
                } else {
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
                        InputMethodManager imm = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
                        imm.hideSoftInputFromWindow(v.getWindowToken(), 0);

                        movieSearchStr = movieSearchEdit.getText().toString();

                        new GetMovieList().execute();
                    } else {
                        Toast.makeText(SearchActivity.this, "No internet connection.", Toast.LENGTH_SHORT).show();
                    }
                }
            }
        });

        movieSearchEdit.setOnEditorActionListener(new TextView.OnEditorActionListener() {
            @Override
            public boolean onEditorAction(TextView v, int actionId, KeyEvent event) {
                if (actionId == EditorInfo.IME_ACTION_SEARCH) {
                    String searchMovieValue = movieSearchEdit.getText().toString().trim();

                    if (searchMovieValue.equalsIgnoreCase("")) {
                        Toast.makeText(SearchActivity.this, "Please type a movie name to search", Toast.LENGTH_SHORT).show();
                    } else {
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
                            InputMethodManager imm = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
                            imm.hideSoftInputFromWindow(v.getWindowToken(), 0);

                            movieSearchStr = movieSearchEdit.getText().toString();

                            new GetMovieList().execute();
                        } else {
                            Toast.makeText(SearchActivity.this, "No internet connection.", Toast.LENGTH_SHORT).show();
                        }
                    }

                    return true;
                }
                return false;
            }
        });

        mSearchList.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                Intent intent = new Intent(SearchActivity.this, Youtube_Activity.class);
                intent.putExtra("Movie Name", list.get(position).getMovieName());
                intent.putExtra("Movie ID", list.get(position).getId());
                intent.putExtra("Trailer Type", list.get(position).getTrailerType());
                startActivity(intent);
                finish();
            }
        });
    }

    private void parseMovieListXML(String MovieListXml) {

        list = new ArrayList<LanguageBean>();

        XMLParser parser = new XMLParser();
        Document doc = parser.getDomElement(MovieListXml);

        NodeList everyTrailerResponse = doc.getElementsByTagName(Constants.KEY_EVERY_TRAILER);

        for (int i = 0; i < everyTrailerResponse.getLength(); i++) {
            Element everyTrailerElement = (Element) everyTrailerResponse.item(i);

            status = parser.getValue(everyTrailerElement, Constants.KEY_STATUS);

            if (status.equalsIgnoreCase("1")) {

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
                msz = parser.getValue(everyTrailerElement, Constants.KEY_MESSAGE);
            }
        }
    }

    class GetMovieList extends AsyncTask<Void, Void, Void> {

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            mProgressDialog = new ProgressDialog(SearchActivity.this);
            mProgressDialog.setMessage("Loading Movie Lists");
            mProgressDialog.setCancelable(false);
            mProgressDialog.setCanceledOnTouchOutside(false);
            mProgressDialog.show();
        }

        @Override

        protected Void doInBackground(Void... params) {
            try {
                URL url = new URL(Constants.URL_MOVIE_SEARCH);

                HttpURLConnection con = (HttpURLConnection) url
                        .openConnection();
                con.setRequestMethod("POST");
                con.setRequestProperty("Content-Type", "application/json");
                con.setRequestProperty("Accept", "application/json");
                con.connect();

                // SET PARAMS
                OutputStream os = con.getOutputStream();
                OutputStreamWriter osw = new OutputStreamWriter(os, "UTF-8");

                String searchRequestXml = "<?xml version='1.0' encoding='utf-8'?><everyTrailler><inputData><movieName>" + movieSearchStr +
                        "</movieName><pageNO>1</pageNO></inputData></everyTrailler>";

                String paramsStr = "{\"pass\":\"" + privateKey + "\",\"searchtext\":\"" + searchRequestXml + "\"}";

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
            if (status.equalsIgnoreCase("1")) {
                adapter = new LanguageMoviesAdapter(SearchActivity.this, list);

                mSearchList.setAdapter(adapter);
            } else {
                Toast.makeText(SearchActivity.this, "" + msz, Toast.LENGTH_SHORT).show();
            }

        }
    }
}
