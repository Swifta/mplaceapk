package com.swifta.zenith.marketplace.Activities;

import android.content.Intent;
import android.support.design.widget.Snackbar;
import android.support.v4.view.MenuItemCompat;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.TextView;

import com.swifta.zenith.marketplace.Fragments.AllDealsFragment;
import com.swifta.zenith.marketplace.Fragments.AllProductFragment;
import com.swifta.zenith.marketplace.R;

public class AllDealsActivity extends BaseToolbarActivity {
    View rootView;
    static TextView cartTextView;
    static TextView wishlistTextView;
    static TextView compareTextView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        // Ignore: setContentView(R.layout.activity_all_deals);

        mNestedScrollView.setVisibility(View.GONE);
        rootView = getLayoutInflater().inflate(R.layout.activity_all_deals, mCoordinatorLayout);

        mToolbar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent i = new Intent(AllDealsActivity.this, HomeActivity.class);
                startActivity(i);
                finish();
            }
        });

        // Creates the launcher fragment
        getSupportFragmentManager().beginTransaction()
                .replace(R.id.frameLayout, new AllDealsFragment())
                .commit();
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
        if (id == R.id.cart_badge) {
            Snackbar.make(rootView, "I have been added to cart already, duuh", Snackbar.LENGTH_SHORT).show();
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

}
