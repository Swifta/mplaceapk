package com.swifta.zenith.marketplace.Activities;

import android.content.Intent;
import android.support.design.widget.TabLayout;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;

import com.swifta.zenith.marketplace.Fragments.AuctionsCategoryFragment;
import com.swifta.zenith.marketplace.Fragments.DealsCategoryFragment;
import com.swifta.zenith.marketplace.Fragments.ProductCategoryFragment;
import com.swifta.zenith.marketplace.R;

public class CategoryActivity extends BaseNavigationDrawerActivity {
    View rootView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        rootView = getLayoutInflater().inflate(R.layout.activity_category, mNestedScrollView);

        // Selects the specific item in the Navigation View and checks it
        MenuItem hMenuItem = mNavigationView.getMenu().getItem(position);
        if (hMenuItem.isChecked()) {
            hMenuItem.setChecked(false);
        } else {
            hMenuItem.setChecked(true);
        }

        getSupportFragmentManager().beginTransaction()
                .replace(R.id.frameLayout, new ProductCategoryFragment())
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
                            .replace(R.id.frameLayout, new ProductCategoryFragment())
                            .commit();
                } else if(tab.getPosition() == 1) {
                    getSupportFragmentManager().beginTransaction()
                            .replace(R.id.frameLayout, new DealsCategoryFragment())
                            .commit();
                } else if(tab.getPosition() == 2) {
                    getSupportFragmentManager().beginTransaction()
                            .replace(R.id.frameLayout, new AuctionsCategoryFragment())
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

    @Override
    public void onBackPressed() {
        super.onBackPressed();

        Intent i = new Intent(CategoryActivity.this, HomeActivity.class);
        startActivity(i);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_category, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            return true;
        }
        return super.onOptionsItemSelected(item);
    }

}
