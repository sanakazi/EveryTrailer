package com.aurum.everytrailer;

import android.content.SharedPreferences;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.MenuItem;
import android.widget.ListView;

import com.aurum.everytrailer.adapter.ReviewDetailAdapter;
import com.aurum.everytrailer.bean.ReviewBean;

import java.util.ArrayList;

public class ReviewActvity extends AppCompatActivity {

    ReviewDetailAdapter review_adapter;
    ArrayList<ReviewBean> lisreviewset;
    ListView listView;

    SharedPreferences prefs;
    String userId, movieName;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_review_actvity);

        prefs = PreferenceManager.getDefaultSharedPreferences(ReviewActvity.this);
        movieName = getIntent().getStringExtra("Movie_Name");

        Toolbar toolbar = (Toolbar) findViewById(R.id.newsactivity);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayShowTitleEnabled(true);
        getSupportActionBar().setDisplayShowHomeEnabled(true);
        getSupportActionBar().setTitle("Reviews for " + movieName);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        userId = prefs.getString("User_ID", null);


        lisreviewset = Youtube_Activity.lisreviewbean;
        review_adapter = new ReviewDetailAdapter(ReviewActvity.this, lisreviewset, userId);
        listView = (ListView) findViewById(R.id.listviewdeatail);

        listView.setAdapter(review_adapter);
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
}
