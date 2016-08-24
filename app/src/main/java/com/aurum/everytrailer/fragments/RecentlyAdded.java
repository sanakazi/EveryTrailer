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

import com.aurum.everytrailer.MainActivity;
import com.aurum.everytrailer.R;
import com.aurum.everytrailer.Youtube_Activity;
import com.aurum.everytrailer.adapter.RecentListAdapter;

/**
 * Created by NiravShrimali on 11/4/2015.
 */
public class RecentlyAdded extends Fragment {

    ListView mRecentList;
    RecentListAdapter adapter;

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.recentlyadded_layout, container, false);

        mRecentList = (ListView) rootView.findViewById(R.id.recent_list);

        adapter = new RecentListAdapter(RecentlyAdded.this.getActivity(), MainActivity.recentList);

        mRecentList.setAdapter(adapter);

        mRecentList.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                Intent intent = new Intent(RecentlyAdded.this.getActivity(), Youtube_Activity.class);
                intent.putExtra("Movie Name", MainActivity.recentList.get(position).getMovieName());
                intent.putExtra("Movie ID", MainActivity.recentList.get(position).getId());
                intent.putExtra("Trailer Type", MainActivity.recentList.get(position).getTrailerType());
                startActivity(intent);
            }
        });

        return rootView;
    }
}
