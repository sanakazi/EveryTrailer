package com.aurum.everytrailer.fragments;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ListView;

import com.aurum.everytrailer.LanguageMoviesActivity;
import com.aurum.everytrailer.MainActivity;
import com.aurum.everytrailer.R;
import com.aurum.everytrailer.adapter.AllMoviesAdapter;

/**
 * Created by NiravShrimali on 11/4/2015.
 */
public class AllMovies extends Fragment {

    ListView mAllMoviesListView;
    AllMoviesAdapter adapter;

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.allfilms_layout, container, false);

        mAllMoviesListView = (ListView) rootView.findViewById(R.id.allmovies_listview);

        adapter = new AllMoviesAdapter(AllMovies.this.getActivity(), MainActivity.allMoviesList);

        mAllMoviesListView.setAdapter(adapter);

        mAllMoviesListView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                Intent intent = new Intent(AllMovies.this.getActivity(), LanguageMoviesActivity.class);
                intent.putExtra("Language ID", MainActivity.allMoviesList.get(position).getLanguageId());
                intent.putExtra("Language", MainActivity.allMoviesList.get(position).getLanguage());
                startActivity(intent);
            }
        });

        return rootView;
    }
}
