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
import com.aurum.everytrailer.adapter.ComingSoonListAdapter;

/**
 * Created by NiravShrimali on 11/4/2015.
 */
public class ComingSoon extends Fragment {

    ListView mComingSoonList;
    ComingSoonListAdapter adapter;

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.commingsoon_layout, container, false);

        mComingSoonList = (ListView) rootView.findViewById(R.id.coming_soon_list);

        adapter = new ComingSoonListAdapter(ComingSoon.this.getActivity(), MainActivity.comingSoonList);

        mComingSoonList.setAdapter(adapter);

        mComingSoonList.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                Intent intent = new Intent(ComingSoon.this.getActivity(), Youtube_Activity.class);
                intent.putExtra("Movie Name", MainActivity.comingSoonList.get(position).getMovieName());
                intent.putExtra("Movie ID", MainActivity.comingSoonList.get(position).getId());
                intent.putExtra("Trailer Type", MainActivity.comingSoonList.get(position).getTrailerType());
                startActivity(intent);
            }
        });

        return rootView;
    }

}
