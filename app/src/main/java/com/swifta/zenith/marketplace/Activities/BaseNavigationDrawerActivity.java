package com.swifta.zenith.marketplace.Activities;

import android.annotation.TargetApi;
import android.content.Intent;
import android.content.pm.ActivityInfo;
import android.os.Build;
import android.os.Bundle;
import android.support.design.widget.CoordinatorLayout;
import android.support.design.widget.NavigationView;
import android.support.design.widget.TabLayout;
import android.support.v4.content.ContextCompat;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v4.widget.NestedScrollView;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.TextView;

import com.swifta.zenith.marketplace.R;
import com.swifta.zenith.marketplace.Utils.ChromeTabsCreator;
import com.swifta.zenith.marketplace.Utils.Session;

import java.util.ArrayList;
import java.util.List;

public abstract class BaseNavigationDrawerActivity extends AppCompatActivity {

    public static boolean SIGNED_IN = false;
    protected static int position;
    final List<MenuItem> items = new ArrayList<>();
    protected Toolbar mToolbar;
    protected DrawerLayout mDrawerLayout;
    protected CoordinatorLayout mCoordinatorLayout;
    protected NavigationView mNavigationView;
    protected ActionBarDrawerToggle mActionBarDrawerToggle;
    protected NestedScrollView mNestedScrollView;
    protected Menu menu;
    private View headerView;
    protected TabLayout mTabLayout;
    protected TextView drawer_name;
    protected TextView drawer_sign_in;
    protected TextView drawer_sign_out;
    private ChromeTabsCreator mChromeTabsCreator;

    @TargetApi(Build.VERSION_CODES.LOLLIPOP)
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        // Sets the orientation permanently to Portrait mode.
        // Child activities will also inherit this.
        setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);
        setContentView(R.layout.activity_base);

        // Sets the status bar color
        Window window = getWindow();
        window.addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS);
        window.clearFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS);
        if (android.os.Build.VERSION.SDK_INT >= 21) {
            window.setStatusBarColor(ContextCompat.getColor(this, R.color.colorPrimaryDarker));
        }

        mCoordinatorLayout = (CoordinatorLayout) findViewById(R.id.main_coordinator_layout);
        mNestedScrollView = (NestedScrollView) findViewById(R.id.scrollView_content_frame);
        mTabLayout = (TabLayout) findViewById(R.id.tab_layout);
        mDrawerLayout = (DrawerLayout) findViewById(R.id.main_drawer_layout);
        mNavigationView = (NavigationView) findViewById(R.id.navigation_view);
        // Latest way of getting Navigation Header views
        headerView = mNavigationView.inflateHeaderView(R.layout.navigation_header);
        drawer_name = (TextView) headerView.findViewById(R.id.drawer_name);
        drawer_sign_in = (TextView) headerView.findViewById(R.id.drawer_sign_in);
        drawer_sign_out = (TextView) headerView.findViewById(R.id.drawer_sign_out);

        menu = mNavigationView.getMenu();
        mChromeTabsCreator = new ChromeTabsCreator();

        // Sets the Toolbar instead of the ActionBar
        mToolbar = (Toolbar) findViewById(R.id.main_toolbar);
        setSupportActionBar(mToolbar);

        // If the user is signed in, hide the welcome card and show the full menu
        // in the Navigation View. This applies to children activities too.
        if (SIGNED_IN) {
            mNavigationView.getMenu().removeItem(R.id.navigation_subheader_2_1);
            drawer_name.setVisibility(View.VISIBLE);
            drawer_name.setText("Welcome, " + Session.getEmail(this));

            drawer_sign_in.setVisibility(View.GONE);
            drawer_sign_out.setVisibility(View.VISIBLE);
        } else {
            mNavigationView.getMenu().removeItem(R.id.navigation_subheader_2);
            drawer_name.setVisibility(View.GONE);
            drawer_sign_in.setVisibility(View.VISIBLE);
            drawer_sign_out.setVisibility(View.GONE);
        }

        drawer_sign_in.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent i = new Intent(BaseNavigationDrawerActivity.this, SignInActivity.class);
                startActivity(i);
                finish();
            }
        });

        drawer_sign_out.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                // Clear all the data from the SharedPreferences
                Session.clear(BaseNavigationDrawerActivity.this);

                SIGNED_IN = false;

                Intent i = new Intent(BaseNavigationDrawerActivity.this, HomeActivity.class);
                startActivity(i);
                finish();
            }
        });

        for (int i = 0; i < menu.size(); i++) {
            items.add(menu.getItem(i));
        }

        // Allows Navigation Items to be checked on click in the Navigation view
        mNavigationView.setNavigationItemSelectedListener(
                new NavigationView.OnNavigationItemSelectedListener() {
                    @Override
                    public boolean onNavigationItemSelected(MenuItem menuItem) {
                        goToNavDrawerItem(menuItem);
                        return true;
                    }
                }
        );

        mActionBarDrawerToggle = new ActionBarDrawerToggle(
                this, mDrawerLayout, mToolbar, R.string.open_drawer, R.string.close_drawer) {
            @Override
            public void onDrawerOpened(View drawerView) {
                super.onDrawerOpened(drawerView);
            }

            @Override
            public void onDrawerClosed(View drawerView) {
                super.onDrawerClosed(drawerView);
            }
        };

        mDrawerLayout.setDrawerListener(mActionBarDrawerToggle);

        // Calling Sync State is necessary if not hamburger icon won't show up
        mActionBarDrawerToggle.syncState();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_base, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.home) {
            mDrawerLayout.openDrawer(GravityCompat.START);
            return true;
        }
        return super.onOptionsItemSelected(item);
    }

    // Helper class for creating intents
    private void createNewIntent(Class newActivity) {
        Intent intent = new Intent(this, newActivity);

        // To stop animations on start of activity
        intent.setFlags(Intent.FLAG_ACTIVITY_NO_ANIMATION);
        startActivity(intent);
        // To stop animations on activity finish
        overridePendingTransition(0, 0);
        finish();
    }

    /**
     * // Allows Navigation Items to be checked on click in the Navigation view
     **/
    private void goToNavDrawerItem(MenuItem menuItem) {
        int id;

        id = menuItem.getItemId();
        BaseNavigationDrawerActivity.position = items.indexOf(menuItem);

        switch (id) {
            case R.id.main_navigation_item_11:
                createNewIntent(HomeActivity.class);
                break;
            case R.id.main_navigation_item_12:
                createNewIntent(ProductCategoryActivity.class);
                break;
            case R.id.main_navigation_item_13:
                createNewIntent(DealsActivity.class);
                break;
            case R.id.main_navigation_item_14:
                createNewIntent(AuctionsActivity.class);
                break;
            case R.id.main_navigation_item_15:
                createNewIntent(StoreCreditActivity.class);
                break;
            case R.id.main_navigation_item_16:
                createNewIntent(SoldOutActivity.class);
                break;
            case R.id.main_navigation_item_17:
                createNewIntent(StoresActivity.class);
                break;
            case R.id.main_navigation_item_18:
                createNewIntent(NearActivity.class);
                break;
            case R.id.main_navigation_item_19:
                mChromeTabsCreator.createDefaultChromeTab(this, BaseNavigationDrawerActivity.this);
                break;
            case R.id.main_navigation_item_22:
                createNewIntent(ProfileActivity.class);
                break;
            case R.id.main_navigation_item_28:
                createNewIntent(ShippingActivity.class);
                break;
        }
        mDrawerLayout.closeDrawers();
    }

    @Override
    protected void onDestroy() {
        mChromeTabsCreator.unbindCustomTabsService();

        super.onDestroy();
    }
}

