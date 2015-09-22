package com.swifta.zenith.marketplace.Activities;

import android.content.Intent;
import android.content.pm.ActivityInfo;
import android.media.Image;
import android.support.design.widget.AppBarLayout;
import android.support.design.widget.CollapsingToolbarLayout;
import android.support.design.widget.CoordinatorLayout;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.TabLayout;
import android.support.v4.widget.NestedScrollView;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.widget.ImageView;

import com.swifta.zenith.marketplace.R;

public abstract class BaseToolbarActivity extends AppCompatActivity {

    protected TabLayout mTabLayout;
    protected Toolbar mToolbar;
    protected CoordinatorLayout mCoordinatorLayout;
    protected NestedScrollView mNestedScrollView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        // Sets the orientation permanently to Portrait mode.
        // Child activities will also inherit this.
        setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);
        setContentView(R.layout.activity_base_toolbar);

        mTabLayout = (TabLayout) findViewById(R.id.tab_layout);
        mCoordinatorLayout = (CoordinatorLayout) findViewById(R.id.base_toolbar_coordinator_layout);
        mNestedScrollView = (NestedScrollView) findViewById(R.id.scrollView_content_frame);

        // Sets the Toolbar instead of the ActionBar
        mToolbar = (Toolbar) findViewById(R.id.main_toolbar);
        setSupportActionBar(mToolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        mToolbar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });
    }
}
