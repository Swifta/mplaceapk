package com.swifta.zenith.marketplace.Fragments;


import android.app.Fragment;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.swifta.zenith.marketplace.R;

/**
 * A simple {@link Fragment} subclass.
 */
public class CashOnDeliveryFragment extends android.support.v4.app.Fragment {
    View rootView;

    public CashOnDeliveryFragment() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        rootView = inflater.inflate(R.layout.fragment_authorize_net, container, false);

        return rootView;
    }


}
