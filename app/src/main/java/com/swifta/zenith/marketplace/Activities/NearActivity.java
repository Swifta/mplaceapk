package com.swifta.zenith.marketplace.Activities;

import android.os.Bundle;
import android.support.design.widget.TabLayout;
import android.view.MenuItem;
import android.view.View;

import com.swifta.zenith.marketplace.Fragments.NearAuctionsFragment;
import com.swifta.zenith.marketplace.Fragments.NearDealsFragment;
import com.swifta.zenith.marketplace.Fragments.NearProductsFragment;
import com.swifta.zenith.marketplace.R;

public class NearActivity extends BaseNavigationDrawerActivity {
    View rootView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        //Ignore: setContentView(R.layout.activity_home);

        // Selects the specific item in the Navigation View and checks it
        MenuItem hMenuItem = mNavigationView.getMenu().getItem(position);
        if (hMenuItem.isChecked()) {
            hMenuItem.setChecked(false);
        } else {
            hMenuItem.setChecked(true);
        }

        rootView = getLayoutInflater().inflate(R.layout.activity_near, mNestedScrollView);

        getSupportFragmentManager().beginTransaction()
                .replace(R.id.frameLayout, new NearProductsFragment())
                .commit();

        mTabLayout.setVisibility(View.VISIBLE);
        mTabLayout.addTab(mTabLayout.newTab().setText(R.string.title_activity_products));
        mTabLayout.addTab(mTabLayout.newTab().setText(R.string.title_activity_deals));
        mTabLayout.addTab(mTabLayout.newTab().setText(R.string.title_activity_auctions));

        mTabLayout.setOnTabSelectedListener(new TabLayout.OnTabSelectedListener() {
            @Override
            public void onTabSelected(TabLayout.Tab tab) {
                if (tab.getPosition() == 0) {
                    getSupportFragmentManager().beginTransaction()
                            .replace(R.id.frameLayout, new NearProductsFragment())
                            .commit();
                } else if (tab.getPosition() == 1) {
                    getSupportFragmentManager().beginTransaction()
                            .replace(R.id.frameLayout, new NearDealsFragment())
                            .commit();
                } else if (tab.getPosition() == 2) {
                    getSupportFragmentManager().beginTransaction()
                            .replace(R.id.frameLayout, new NearAuctionsFragment())
                            .commit();
                }
            }

            @Override
            public void onTabUnselected(TabLayout.Tab tab) {

            }

            @Override
            public void onTabReselected(TabLayout.Tab tab) {

            }
        });
    }
}
