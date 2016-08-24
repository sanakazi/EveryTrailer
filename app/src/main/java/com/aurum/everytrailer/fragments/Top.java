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
import com.aurum.everytrailer.adapter.TopViewsListAdapter;

/**
 * Created by NiravShrimali on 10/30/2015.
 */
public class Top extends Fragment {

    ListView mTopList;
    TopViewsListAdapter adapter;

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.topten_layout, container, false);

        mTopList = (ListView) rootView.findViewById(R.id.top_list);

        adapter = new TopViewsListAdapter(Top.this.getActivity(), MainActivity.topViewsList);

        mTopList.setAdapter(adapter);

        mTopList.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                Intent intent = new Intent(Top.this.getActivity(), Youtube_Activity.class);
                intent.putExtra("Movie Name", MainActivity.topViewsList.get(position).getMovieName());
                intent.putExtra("Movie ID", MainActivity.topViewsList.get(position).getId());
                intent.putExtra("Trailer Type", MainActivity.topViewsList.get(position).getTrailerType());
                startActivity(intent);
            }
        });

        return rootView;
    }
}
