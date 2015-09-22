package com.swifta.zenith.marketplace.Activities;

import android.annotation.TargetApi;
import android.os.Build;
import android.support.design.widget.TabLayout;
import android.os.Bundle;
import android.support.v4.content.ContextCompat;
import android.support.v4.view.MenuItemCompat;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.TextView;

import com.swifta.zenith.marketplace.Fragments.AuctionsCategoryListFragment;
import com.swifta.zenith.marketplace.Fragments.DealsCategoryListFragment;
import com.swifta.zenith.marketplace.Fragments.ProductCategoryListFragment;
import com.swifta.zenith.marketplace.R;

import java.util.ArrayList;

public class SubCategoryActivity extends BaseToolbarActivity {
    View rootView;
    Bundle bundle;
    static TextView cartTextView;
    static TextView wishlistTextView;
    static TextView compareTextView;
    String categoryName;
    String fragmentName;
    ArrayList<String> subCategoryId = new ArrayList<String>();
    ArrayList<String> subCategoryName = new ArrayList<String>();
    ArrayList<String> countProduct = new ArrayList<String>();


    @TargetApi(Build.VERSION_CODES.LOLLIPOP)
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        // Ignore: setContentView(R.layout.activity_sub_category);


        mNestedScrollView.setVisibility(View.GONE);
        rootView = getLayoutInflater().inflate(R.layout.activity_sub_category, mCoordinatorLayout);

        // Sets the status bar color
        Window window = getWindow();
        window.addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS);
        window.clearFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS);
        if (android.os.Build.VERSION.SDK_INT >= 21) {
            window.setStatusBarColor(ContextCompat.getColor(this, R.color.colorPrimaryDarker));
        }

        // Populates the subcategories using bundles with
        // details from the category adapter
        bundle = getIntent().getExtras();
        // Confirms that bundle is not empty before usage
        if (bundle != null) {

            subCategoryId = bundle.getStringArrayList("sub-category-id");
            subCategoryName = bundle.getStringArrayList("sub-category-name");
            countProduct = bundle.getStringArrayList("count-product");
            categoryName = bundle.getString("category-name");
            fragmentName = bundle.getString("fragment-name");

            getSupportActionBar().setTitle(categoryName);
            mTabLayout.setVisibility(View.VISIBLE);
            mTabLayout.setTabMode(TabLayout.MODE_SCROLLABLE);

            // Creates new tabs dynamically
            for (int i = 0; i < subCategoryName.size(); i++) {
                String name = subCategoryName.get(i) + (" (" +
                        countProduct.get(i) + ")");
                mTabLayout.addTab(mTabLayout.newTab().setText(name));
            }

            // Sets the first tab as the default tab and opens a new fragment
            createProductCategoryListFragment(subCategoryId.get(mTabLayout.getSelectedTabPosition()));
        }

        mTabLayout.setOnTabSelectedListener(new TabLayout.OnTabSelectedListener() {
            @Override
            public void onTabSelected(TabLayout.Tab tab) {

                createProductCategoryListFragment(subCategoryId.get(tab.getPosition()));
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
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_all_products, menu);

        // Sets up the cart count menu item
        View cartBadgeLayout = MenuItemCompat.getActionView(menu.findItem(R.id.cart_badge));
        cartTextView = (TextView) cartBadgeLayout.findViewById(R.id.cart_count_text);
        cartTextView.setText(String.valueOf(HomeActivity.cartCount));

        // Sets up the wishlist count menu item
        View wishlistBadgeLayout = MenuItemCompat.getActionView(menu.findItem(R.id.wishlist_badge));
        wishlistTextView = (TextView) wishlistBadgeLayout.findViewById(R.id.wishlist_count_text);
        wishlistTextView.setText(String.valueOf(HomeActivity.wishlistCount));

        // Sets up the compare count menu item
        View compareBadgeLayout = MenuItemCompat.getActionView(menu.findItem(R.id.compare_badge));
        compareTextView = (TextView) compareBadgeLayout.findViewById(R.id.compare_count_text);
        compareTextView.setText(String.valueOf(HomeActivity.compareCount));

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

    /**
     * Updates the value of the cart in the Menu
     */
    public static void displayCartCount() {
        cartTextView.setText(String.valueOf(HomeActivity.cartCount));
    }

    /**
     * Updates the value of the wishlist in the Menu
     */
    public static void displayWishlistCount() {
        wishlistTextView.setText(String.valueOf(HomeActivity.wishlistCount));
    }

    /**
     * Updates the value of the compare icon in the Menu
     */
    public static void displayCompareCount() {
        compareTextView.setText(String.valueOf(HomeActivity.compareCount));
    }

    /**
     * Sends the subcategory_id to another fragment to enable fetching
     * of the product category list in the new fragment
     */
    private void createProductCategoryListFragment(String subCategoryId) {
        // Creates new bundle
        Bundle bundle = new Bundle();
        bundle.putString("subcategory_id", subCategoryId);


        // Creates new fragment and inserts bundle based on the home fragment
        switch (fragmentName) {
            case ("ProductCategoryFragment"):
                ProductCategoryListFragment productCategoryListFragment = new ProductCategoryListFragment();
                productCategoryListFragment.setArguments(bundle);

                getSupportFragmentManager().beginTransaction()
                        .replace(R.id.frameLayout, productCategoryListFragment)
                        .commit();
                break;
            case ("DealsCategoryFragment"):
                DealsCategoryListFragment dealsCategoryListFragment = new DealsCategoryListFragment();
                dealsCategoryListFragment.setArguments(bundle);

                getSupportFragmentManager().beginTransaction()
                        .replace(R.id.frameLayout, dealsCategoryListFragment)
                        .commit();
                break;
            case ("AuctionsCategoryFragment"):
                AuctionsCategoryListFragment auctionsCategoryListFragment = new AuctionsCategoryListFragment();
                auctionsCategoryListFragment.setArguments(bundle);

                getSupportFragmentManager().beginTransaction()
                        .replace(R.id.frameLayout, auctionsCategoryListFragment)
                        .commit();
                break;
        }

    }
}
