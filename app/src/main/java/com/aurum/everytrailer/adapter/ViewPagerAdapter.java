package com.aurum.everytrailer.adapter;

import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by NiravShrimali on 11/4/2015.
 */
public class ViewPagerAdapter extends FragmentPagerAdapter {
    private final List<Fragment> aFragmentList = new ArrayList<>();
    private final List<String> aFragmentTitleList = new ArrayList<>();

    public ViewPagerAdapter(FragmentManager fm) {
        super(fm);
    }


    @Override
    public Fragment getItem(int position) {
        return aFragmentList.get(position);
    }

    @Override
    public int getCount() {
        return aFragmentList.size();
    }

    public void addFrag(Fragment fragment, String title) {
        aFragmentList.add(fragment);
        aFragmentTitleList.add(title);
    }

    @Override
    public CharSequence getPageTitle(int position) {
        return aFragmentTitleList.get(position);
    }
}
