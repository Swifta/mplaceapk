package com.swifta.zenith.marketplace.Fragments;


import android.os.Bundle;
import android.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

/**
 * A simple {@link Fragment} subclass.
 */
public class OnboardingPane extends android.support.v4.app.Fragment {

    final static String LAYOUT_ID = "layoutid";

    public OnboardingPane() {
        // Required empty public constructor
    }

    public static OnboardingPane newInstance(int layoutId) {
        OnboardingPane pane = new OnboardingPane();
        Bundle args = new Bundle();
        args.putInt(LAYOUT_ID, layoutId);
        pane.setArguments(args);
        return pane;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        ViewGroup rootView = (ViewGroup) inflater.inflate(getArguments().getInt(LAYOUT_ID, -1), container, false);
        return rootView;
    }
}


