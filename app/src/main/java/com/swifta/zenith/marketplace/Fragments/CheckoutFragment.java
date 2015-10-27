package com.swifta.zenith.marketplace.Fragments;


import android.app.Fragment;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.Toast;

import com.swifta.zenith.marketplace.R;

/**
 * A simple {@link Fragment} subclass.
 */
public class CheckoutFragment extends android.support.v4.app.Fragment {
    View rootView;
    Button interswitchOption;
    Button cashOnDeliveryOption;
    Button payLaterOption;

    public CheckoutFragment() {
        // Required empty public constructor
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        rootView = inflater.inflate(R.layout.fragment_checkout, container, false);

        interswitchOption = (Button) rootView.findViewById(R.id.interswitch);
        cashOnDeliveryOption = (Button) rootView.findViewById(R.id.cash_on_delivery);
        payLaterOption = (Button) rootView.findViewById(R.id.pay_later);

        interswitchOption.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {
                Toast.makeText(getActivity(), "interswitchOption", Toast.LENGTH_SHORT).show();
            }
        });

        cashOnDeliveryOption.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {
                CashOnDeliveryFragment cashOnDeliveryFragment = new CashOnDeliveryFragment();
                getActivity().getSupportFragmentManager().beginTransaction()
                        .replace(R.id.frame_content, cashOnDeliveryFragment)
                        .addToBackStack(null)
                        .commit();
            }
        });

        payLaterOption.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {
                Toast.makeText(getActivity(), "payLaterOption", Toast.LENGTH_SHORT).show();
            }
        });

        return rootView;
    }

}

