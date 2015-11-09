package com.swifta.zenith.marketplace.Activities;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;

import com.swifta.zenith.marketplace.Fragments.AllProductFragment;
import com.swifta.zenith.marketplace.R;

public class AllProductsActivity extends BaseToolbarActivity {
    View rootView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        // Ignore: setContentView(R.layout.activity_all_products);

        // Hides NestedScrollView because it's bad practise to display RecyclerView in a ScrollView
        mNestedScrollView.setVisibility(View.GONE);
        rootView = getLayoutInflater().inflate(R.layout.activity_all_products, mCoordinatorLayout);

        mToolbar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent i = new Intent(AllProductsActivity.this, HomeActivity.class);
                startActivity(i);
                finish();
            }
        });

        // Creates the launcher fragment
        getSupportFragmentManager().beginTransaction()
                .replace(R.id.frameLayout, new AllProductFragment())
                .commit();
    }
}
