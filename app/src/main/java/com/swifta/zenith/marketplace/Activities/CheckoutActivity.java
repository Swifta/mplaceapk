package com.swifta.zenith.marketplace.Activities;

import android.os.Bundle;
import android.view.View;

import com.swifta.zenith.marketplace.Fragments.CheckoutFragment;
import com.swifta.zenith.marketplace.R;

public class CheckoutActivity extends BaseToolbarActivity {
    View rootview;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        rootview = getLayoutInflater().inflate(R.layout.activity_checkout, mNestedScrollView);

        // Displays the default fragment for this Activity
        CheckoutFragment checkoutFragment = new CheckoutFragment();
        getSupportFragmentManager().beginTransaction()
                .replace(R.id.frame_content, checkoutFragment)
                .addToBackStack(null)
                .commit();
    }
}
