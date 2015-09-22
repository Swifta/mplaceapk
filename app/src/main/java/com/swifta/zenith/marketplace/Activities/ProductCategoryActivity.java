package com.swifta.zenith.marketplace.Activities;

import android.content.Intent;
import android.os.Bundle;
import android.view.MenuItem;
import android.view.View;

import com.swifta.zenith.marketplace.Fragments.ProductCategoryFragment;
import com.swifta.zenith.marketplace.R;

public class ProductCategoryActivity extends BaseNavigationDrawerActivity {
    View rootView;
    MenuItem hMenuItem;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        // Ignore: setContentView(R.layout.activity_product_category);
        rootView = getLayoutInflater().inflate(R.layout.activity_product_category, mNestedScrollView);

        // Selects the specific item in the Navigation View and checks it
        hMenuItem = mNavigationView.getMenu().getItem(position);
        if (hMenuItem.isChecked()) {
            hMenuItem.setChecked(false);
        } else {
            hMenuItem.setChecked(true);
        }

        getSupportFragmentManager().beginTransaction()
                .replace(R.id.frameLayout, new ProductCategoryFragment())
                .commit();
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();

        Intent i = new Intent(ProductCategoryActivity.this, HomeActivity.class);
        startActivity(i);
    }
}
