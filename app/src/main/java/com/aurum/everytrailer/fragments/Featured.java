package com.aurum.everytrailer.fragments;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AbsListView;
import android.widget.AdapterView;
import android.widget.ListView;

import com.aurum.everytrailer.MainActivity;
import com.aurum.everytrailer.R;
import com.aurum.everytrailer.Youtube_Activity;
import com.aurum.everytrailer.adapter.FeaturedListAdapter;

/**
 * Created by NiravShrimali on 11/4/2015.
 */
public class Featured extends Fragment {

    ListView mFeaturedList;
    FeaturedListAdapter adapter;

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.featured_layout, container, false);

        mFeaturedList = (ListView) rootView.findViewById(R.id.featured_list);

        adapter = new FeaturedListAdapter(Featured.this.getActivity(), MainActivity.featuredList);

        mFeaturedList.setAdapter(adapter);

        mFeaturedList.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                                                 @Override
                                                 public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                                                     Intent intent = new Intent(Featured.this.getActivity(), Youtube_Activity.class);
                                                     intent.putExtra("Movie Name", MainActivity.featuredList.get(position).getMovieName());
                                                     intent.putExtra("Movie ID", MainActivity.featuredList.get(position).getId());
                                                     intent.putExtra("Trailer Type", MainActivity.featuredList.get(position).getTrailerType());
                                                     startActivity(intent);
                                                 }
                                             }
        );

        mFeaturedList.setOnScrollListener(new AbsListView.OnScrollListener() {
            @Override
            public void onScrollStateChanged(AbsListView view, int scrollState) {

            }

            @Override
            public void onScroll(AbsListView view, int firstVisibleItem, int visibleItemCount, int totalItemCount) {

            }
        });

        return rootView;
    }
}
