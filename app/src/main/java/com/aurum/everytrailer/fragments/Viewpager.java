package com.aurum.everytrailer.fragments;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.design.widget.TabLayout;
import android.support.v4.app.Fragment;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;

import com.aurum.everytrailer.R;
import com.aurum.everytrailer.adapter.ViewPagerAdapter;

/**
 * Created by NiravShrimali on 11/4/2015.
 */
public class Viewpager extends Fragment {
    private Toolbar toolbar;
    private TabLayout tabLayout;
    private ViewPager viewPager;

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View viewroot = inflater.inflate(R.layout.viewpager_layout, container, false);

        toolbar = (Toolbar) viewroot.findViewById(R.id.toolbar);

        ((AppCompatActivity) getActivity()).setSupportActionBar(toolbar);
        ((AppCompatActivity) getActivity()).getSupportActionBar().setHomeButtonEnabled(true);
        ((AppCompatActivity) getActivity()).getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        viewPager = (ViewPager) viewroot.findViewById(R.id.viewpager);
        setupViewPager(viewPager);

        tabLayout = (TabLayout) viewroot.findViewById(R.id.tabs);
        tabLayout.setupWithViewPager(viewPager);
        return viewroot;
    }

    private void setupViewPager(ViewPager viewPager) {
        ViewPagerAdapter adapter = new ViewPagerAdapter(getFragmentManager());
        adapter.addFrag(new Featured(), "Featured");
        adapter.addFrag(new Top(), "Top 10");
        adapter.addFrag(new RecentlyAdded(), "Recently Added");
        adapter.addFrag(new NowShowing(), "Now Showing");
        adapter.addFrag(new ComingSoon(), "Coming Soon");
        adapter.addFrag(new AllMovies(), "All Movies");

        viewPager.setAdapter(adapter);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home:
                this.getActivity().onBackPressed();
        }
        return super.onOptionsItemSelected(item);
    }

}
