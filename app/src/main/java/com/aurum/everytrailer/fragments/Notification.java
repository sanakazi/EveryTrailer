package com.aurum.everytrailer.fragments;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.aurum.everytrailer.R;

/**
 * Created by NiravShrimali on 11/4/2015.
 */
public class Notification extends Fragment {

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View viewroot = inflater.inflate(R.layout.notificaton_layout, container, false);


        return viewroot;
    }
}
