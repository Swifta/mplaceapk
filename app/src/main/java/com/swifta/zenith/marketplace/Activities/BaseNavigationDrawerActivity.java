package com.swifta.zenith.marketplace.Activities;

import android.annotation.TargetApi;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.ActivityInfo;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Build;
import android.support.customtabs.CustomTabsCallback;
import android.support.customtabs.CustomTabsClient;
import android.support.customtabs.CustomTabsIntent;
import android.support.customtabs.CustomTabsServiceConnection;
import android.support.customtabs.CustomTabsSession;
import android.support.design.widget.CoordinatorLayout;
import android.support.design.widget.NavigationView;
import android.support.design.widget.TabLayout;
import android.support.v4.content.ContextCompat;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v4.widget.NestedScrollView;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.TextView;

import com.swifta.zenith.marketplace.R;
import com.swifta.zenith.marketplace.Utils.Utility;

import java.util.ArrayList;
import java.util.List;

public abstract class BaseNavigationDrawerActivity extends AppCompatActivity {

    protected Toolbar mToolbar;
    protected DrawerLayout mDrawerLayout;
    protected CoordinatorLayout mCoordinatorLayout;
    protected NavigationView mNavigationView;
    protected ActionBarDrawerToggle mActionBarDrawerToggle;
    protected NestedScrollView mNestedScrollView;
    protected Menu menu;
    protected TabLayout mTabLayout;
    protected TextView drawer_name;
    protected TextView drawer_sign_in;
    protected TextView drawer_sign_out;
    private static final String TAG = "CustomTabsClientExample";
    private CustomTabsSession mCustomTabsSession;
    private CustomTabsClient mClient;
    private CustomTabsServiceConnection mConnection;
    final List<MenuItem> items = new ArrayList<>();

    protected static int position;
    public static boolean SIGNED_IN = false;

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
        drawer_name = (TextView) findViewById(R.id.drawer_name);
        drawer_sign_in = (TextView) findViewById(R.id.drawer_sign_in);
        drawer_sign_out = (TextView) findViewById(R.id.drawer_sign_out);
        menu = mNavigationView.getMenu();

        // Sets the Toolbar instead of the ActionBar
        mToolbar = (Toolbar) findViewById(R.id.main_toolbar);
        setSupportActionBar(mToolbar);

        // If the user is signed in, hide the welcome card and show the full menu
        // in the Navigation View. This applies to children activities too.
        if (SIGNED_IN) {
            mNavigationView.getMenu().removeItem(R.id.navigation_subheader_2_1);
            drawer_name.setVisibility(View.VISIBLE);
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
                SharedPreferences sharedPreferences = getSharedPreferences("Preferences", Context.MODE_PRIVATE);
                SharedPreferences.Editor editor = sharedPreferences.edit();
                editor.clear();

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
        if (id == R.id.action_settings) {
            return true;
        } else if (id == R.id.home) {
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

    private void goToNavDrawerItem(MenuItem menuItem) {
        int id = menuItem.getItemId();
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
                //createNewIntent(BlogActivity.class);
                CustomTabsIntent.Builder builder = new CustomTabsIntent.Builder(getSession());
                builder.setToolbarColor(ContextCompat.getColor(this, R.color.colorPrimary)).setShowTitle(true);
                builder.setStartAnimations(this, android.R.anim.slide_out_right, android.R.anim.slide_in_left);
                builder.setExitAnimations(this, android.R.anim.slide_in_left, android.R.anim.slide_out_right);
                CustomTabsIntent customTabsIntent = builder.build();
                customTabsIntent.launchUrl(this, Uri.parse(Utility.BLOG_VALUE));
                break;
        }
        mDrawerLayout.closeDrawers();
    }

    @Override
    protected void onDestroy() {
        unbindCustomTabsService();

        super.onDestroy();
    }

    // Used to create a custom Chrome tab for the blog
    private CustomTabsSession getSession() {
        if (mClient == null) {
            mCustomTabsSession = null;
        } else if (mCustomTabsSession == null) {
            mCustomTabsSession = mClient.newSession(new CustomTabsCallback() {
                @Override
                public void onNavigationEvent(int navigationEvent, Bundle extras) {
                    Log.w(TAG, "onNavigationEvent: Code = " + navigationEvent);
                }
            });
        }
        return mCustomTabsSession;
    }

    // Used to create a custom Chrome tab for the blog
    private void unbindCustomTabsService() {
        if (mConnection == null) return;
        unbindService(mConnection);
        mClient = null;
        mCustomTabsSession = null;
    }

}

