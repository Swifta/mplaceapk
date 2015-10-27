package com.swifta.zenith.marketplace.Activities;

import android.content.Intent;
import android.os.Bundle;
import android.view.MenuItem;

import com.swifta.zenith.marketplace.Fragments.DealsCategoryFragment;
import com.swifta.zenith.marketplace.R;

public class DealsActivity extends BaseNavigationDrawerActivity {

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

        getLayoutInflater().inflate(R.layout.activity_deals, mNestedScrollView);
        getSupportFragmentManager().beginTransaction()
                .replace(R.id.frameLayout, new DealsCategoryFragment())
                .commit();
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();

        Intent i = new Intent(DealsActivity.this, HomeActivity.class);
        startActivity(i);
    }
}
