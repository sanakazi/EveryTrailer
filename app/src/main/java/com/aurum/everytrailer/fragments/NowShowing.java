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
import com.aurum.everytrailer.adapter.NowShowingListAdapter;

/**
 * Created by NiravShrimali on 11/4/2015.
 */
public class NowShowing extends Fragment {

    ListView mNowShowingList;
    NowShowingListAdapter adapter;

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.nowshowing_layout, container, false);

        mNowShowingList = (ListView) rootView.findViewById(R.id.now_showing_list);

        adapter = new NowShowingListAdapter(NowShowing.this.getActivity(), MainActivity.nowShowingList);

        mNowShowingList.setAdapter(adapter);

        mNowShowingList.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                Intent intent = new Intent(NowShowing.this.getActivity(), Youtube_Activity.class);
                intent.putExtra("Movie Name", MainActivity.nowShowingList.get(position).getMovieName());
                intent.putExtra("Movie ID", MainActivity.nowShowingList.get(position).getId());
                intent.putExtra("Trailer Type", MainActivity.nowShowingList.get(position).getTrailerType());
                startActivity(intent);
            }
        });

        return rootView;
    }
}
