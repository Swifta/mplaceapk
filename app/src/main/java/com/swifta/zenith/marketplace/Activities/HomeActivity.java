package com.swifta.zenith.marketplace.Activities;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.design.widget.Snackbar;
import android.support.v4.view.MenuItemCompat;
import android.support.v7.widget.CardView;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.facebook.appevents.AppEventsLogger;
import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
import com.koushikdutta.async.future.FutureCallback;
import com.koushikdutta.ion.Ion;
import com.swifta.zenith.marketplace.Adapters.AuctionsAdapter;
import com.swifta.zenith.marketplace.Adapters.DealsAdapter;
import com.swifta.zenith.marketplace.Adapters.ProductsAdapter;
import com.swifta.zenith.marketplace.Database.CartDatabase;
import com.swifta.zenith.marketplace.R;
import com.swifta.zenith.marketplace.Utils.JSONParser;
import com.swifta.zenith.marketplace.Utils.NetworkConnection;
import com.swifta.zenith.marketplace.Utils.Utility;

import org.json.JSONException;
import org.json.JSONObject;

import com.facebook.FacebookSdk;

import java.util.ArrayList;


public class HomeActivity extends BaseNavigationDrawerActivity {

    public static int cartCount = 0;
    public static int wishlistCount = 0;
    public static int compareCount = 0;
    private static View cartBadgeLayout;
    private static TextView cartTextView;
    private static TextView wishlistTextView;
    private static TextView compareTextView;
    private View homeView;
    private CardView mCardView;
    private RecyclerView mRecyclerView;
    private RecyclerView dealsRecyclerView;
    private RecyclerView auctionsRecyclerView;
    private LinearLayoutManager linearLayoutManager;
    private LinearLayoutManager dealsLinearLayoutManager;
    private LinearLayoutManager auctionsLinearLayoutManager;
    private ProductsAdapter productsAdapter;
    private DealsAdapter dealsAdapter;
    private AuctionsAdapter auctionsAdapter;
    private MenuItem hMenuItem;
    private TextView welcomeText;
    private TextView noProducts;
    private TextView noDeals;
    private TextView noAuctions;
    private TextView productsTryAgain;
    private TextView dealsTryAgain;
    private TextView auctionsTryAgain;
    private TextView productViewAll;
    private TextView dealsViewAll;
    private TextView auctionsViewAll;
    private ProgressBar productsProgress;
    private ProgressBar dealsProgress;
    private ProgressBar auctionsProgress;
    private NetworkConnection networkConnection;
    private ArrayList<JSONParser> products = new ArrayList<JSONParser>();
    private ArrayList<JSONParser> deals = new ArrayList<JSONParser>();
    private ArrayList<JSONParser> auctions = new ArrayList<JSONParser>();

    /**
     * Updates the value of the cart in the Menu
     */
    public static void displayCartCount() {
        cartTextView.setText(String.valueOf(cartCount));
    }

    /**
     * Updates the value of the wishlist in the Menu
     */
    public static void displayWishlistCount() {
        wishlistTextView.setText(String.valueOf(wishlistCount));
    }

    /**
     * Updates the value of the compare icon in the Menu
     */
    public static void displayCompareCount() {
        compareTextView.setText(String.valueOf(compareCount));
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        FacebookSdk.sdkInitialize(this);

        // Inflates HomeActivity's layout into the parent's layout
        homeView = getLayoutInflater().inflate(R.layout.activity_home, mNestedScrollView);

        // Selects HomeActivity in the parent DrawerLayout and checks it
        hMenuItem = mNavigationView.getMenu().getItem(position);
        if (hMenuItem.isChecked()) {
            hMenuItem.setChecked(false);
        } else {
            hMenuItem.setChecked(true);
        }

        welcomeText = (TextView) homeView.findViewById(R.id.welcome_text);
        mRecyclerView = (RecyclerView) homeView.findViewById(R.id.shop_all_products_recycler_view);
        dealsRecyclerView = (RecyclerView) homeView.findViewById(R.id.deals_recycler_view);
        auctionsRecyclerView = (RecyclerView) homeView.findViewById(R.id.auctions_recycler_view);
        noProducts = (TextView) homeView.findViewById(R.id.no_stores);
        noDeals = (TextView) homeView.findViewById(R.id.no_deals);
        noAuctions = (TextView) homeView.findViewById(R.id.no_auctions);
        productsTryAgain = (TextView) homeView.findViewById(R.id.products_try_again);
        dealsTryAgain = (TextView) homeView.findViewById(R.id.deals_try_again);
        auctionsTryAgain = (TextView) homeView.findViewById(R.id.auctions_try_again);
        productViewAll = (TextView) homeView.findViewById(R.id.product_view_all);
        dealsViewAll = (TextView) homeView.findViewById(R.id.deals_view_all);
        auctionsViewAll = (TextView) homeView.findViewById(R.id.auctions_view_all);
        productsProgress = (ProgressBar) homeView.findViewById(R.id.product_progress);
        dealsProgress = (ProgressBar) homeView.findViewById(R.id.deals_progress);
        auctionsProgress = (ProgressBar) homeView.findViewById(R.id.auctions_progress);
        mCardView = (CardView) homeView.findViewById(R.id.home_intro_card);
        networkConnection = new NetworkConnection(this);
        linearLayoutManager = new LinearLayoutManager(this);
        dealsLinearLayoutManager = new LinearLayoutManager(this);
        auctionsLinearLayoutManager = new LinearLayoutManager(this);


        // in the Navigation View if the user is logged in.
        if (BaseNavigationDrawerActivity.SIGNED_IN) {
            // Hides the welcome card and shows the full menu
            mCardView.setVisibility(View.GONE);
            // Also sets the welcome text to the username
            SharedPreferences preferences = getSharedPreferences("Preferences", Context.MODE_PRIVATE);
            String username = preferences.getString("username", "");
            welcomeText.setText(username);
            drawer_name.setText(username);
        } else {
            // Clears the database
            CartDatabase.deleteAll(CartDatabase.class);
            // Shows the welcome card and shows the full menu
            mCardView.setVisibility(View.VISIBLE);
        }

        linearLayoutManager.setOrientation(LinearLayoutManager.HORIZONTAL);
        dealsLinearLayoutManager.setOrientation(LinearLayoutManager.HORIZONTAL);
        auctionsLinearLayoutManager.setOrientation(LinearLayoutManager.HORIZONTAL);

        mRecyclerView.setLayoutManager(linearLayoutManager);
        dealsRecyclerView.setLayoutManager(dealsLinearLayoutManager);
        auctionsRecyclerView.setLayoutManager(auctionsLinearLayoutManager);

        initializeHotProducts();
        initializeHotDeals();
        initializeHotAuctions();

        productsAdapter = new ProductsAdapter(this, products);
        mRecyclerView.setAdapter(productsAdapter);

        dealsAdapter = new DealsAdapter(HomeActivity.this, deals);
        dealsRecyclerView.setAdapter(dealsAdapter);

        auctionsAdapter = new AuctionsAdapter(HomeActivity.this, auctions);
        auctionsRecyclerView.setAdapter(auctionsAdapter);
    }

    /**
     * Handles the clicks for the necessary views
     */
    public void onClick(View view) {
        switch (view.getId()) {
            case (R.id.register_button):
                createNewActivity(RegisterActivity.class);
                break;
            case (R.id.sign_in_button):
                createNewActivity(SignInActivity.class);
                break;
            case (R.id.start_shopping_button):
                createNewActivity(CategoryActivity.class);
                break;
            case (R.id.product_view_all):
                createNewActivity(AllProductsActivity.class);
                break;
            case (R.id.products_try_again):
                initializeHotProducts();
                break;
            case (R.id.home_explore):
                createNewActivity(StoresActivity.class);
                break;
            case (R.id.deals_view_all):
                createNewActivity(AllDealsActivity.class);
                break;
            case (R.id.deals_try_again):
                initializeHotDeals();
                break;
            case (R.id.auctions_view_all):
                createNewActivity(AllAuctionActivity.class);
                break;
            case (R.id.auctions_try_again):
                initializeHotAuctions();
                break;
        }
    }

    /**
     * Creates a new activity
     */
    private void createNewActivity(Class activity) {
        Intent i = new Intent(HomeActivity.this, activity);
        startActivity(i);
        finish();
    }

    /**
     * Loads all the products from the server with Ion
     */
    private void initializeHotProducts() {
        productsProgress.setVisibility(View.VISIBLE);
        noProducts.setVisibility(View.GONE);
        productsTryAgain.setVisibility(View.GONE);
        mRecyclerView.setVisibility(View.GONE);
        productViewAll.setVisibility(View.GONE);

        if (networkConnection.isInternetOn()) {
            Ion.with(this)
                    .load(Utility.HOST_VALUE + Utility.HOT_PRODUCTS_PATH_VALUE)
                    .asJsonObject()
                    .setCallback(new FutureCallback<JsonObject>() {
                        @Override
                        public void onCompleted(Exception e, JsonObject result) {
                            productsProgress.setVisibility(View.GONE);

                            if (e == null) {
                                JsonArray resultArray = result.get("productlist").getAsJsonArray();
                                JsonObject resultObject = resultArray.get(0).getAsJsonObject();
                                JsonObject result_inner = resultObject.get("product_list").getAsJsonObject();

                                int httpCode = result_inner.get("httpCode").getAsInt();

                                switch (httpCode) {
                                    case 200:
                                        mRecyclerView.setVisibility(View.VISIBLE);
                                        productViewAll.setVisibility(View.VISIBLE);

                                        for (int i = 0; i < resultArray.size(); i++) {
                                            resultObject = resultArray.get(i).getAsJsonObject();
                                            result_inner = resultObject.get("product_list").getAsJsonObject();
                                            try {
                                                JSONObject productData = new JSONObject(result_inner.toString());
                                                products.add(new JSONParser(productData));
                                            } catch (JSONException ex) {
                                                ex.printStackTrace();
                                            }
                                        }
                                        break;
                                    case 401:
                                        mRecyclerView.setVisibility(View.GONE);
                                        noProducts.setText(result_inner.get("Message").getAsString());
                                        noProducts.setVisibility(View.VISIBLE);
                                        break;
                                }
                            } else {
                                noProducts.setText(getString(R.string.unable_to_connect));
                                noProducts.setVisibility(View.VISIBLE);
                                productsTryAgain.setVisibility(View.VISIBLE);
                            }
                            productsAdapter.notifyDataSetChanged();
                        }
                    });
        } else {
            networkConnection.displayAlert();
        }
    }

    /**
     * Loads all the deals from the server with Ion
     */
    private void initializeHotDeals() {
        dealsProgress.setVisibility(View.VISIBLE);
        noDeals.setVisibility(View.GONE);
        dealsTryAgain.setVisibility(View.GONE);
        dealsRecyclerView.setVisibility(View.GONE);
        dealsViewAll.setVisibility(View.GONE);

        if (networkConnection.isInternetOn()) {
            Ion.with(this)
                    .load(Utility.HOST_VALUE + Utility.HOT_DEALS_PATH_VALUE)
                    .asJsonObject()
                    .setCallback(new FutureCallback<JsonObject>() {
                                     @Override
                                     public void onCompleted(Exception e, JsonObject result) {
                                         dealsProgress.setVisibility(View.GONE);
                                         if (e == null) {

                                             JsonArray resultArray = result.get("deallist").getAsJsonArray();
                                             JsonObject resultObject = resultArray.get(0).getAsJsonObject();
                                             JsonObject result_inner = resultObject.get("dealslist").getAsJsonObject();

                                             int httpCode = result_inner.get("httpCode").getAsInt();

                                             switch (httpCode) {
                                                 case 200:
                                                     dealsRecyclerView.setVisibility(View.VISIBLE);
                                                     dealsViewAll.setVisibility(View.VISIBLE);

                                                     for (int i = 0; i < resultArray.size(); i++) {
                                                         resultObject = resultArray.get(i).getAsJsonObject();
                                                         result_inner = resultObject.get("dealslist").getAsJsonObject();
                                                         try {
                                                             JSONObject data = new JSONObject(result_inner.toString());
                                                             deals.add(new JSONParser(data));
                                                         } catch (JSONException ex) {

                                                         }
                                                     }
                                                     break;
                                                 case 401:
                                                     noDeals.setText(result_inner.get("Message").getAsString());
                                                     noDeals.setVisibility(View.VISIBLE);
                                                     break;
                                             }
                                         } else {
                                             noDeals.setText(getString(R.string.unable_to_connect));
                                             noDeals.setVisibility(View.VISIBLE);
                                             dealsTryAgain.setVisibility(View.VISIBLE);
                                         }
                                         dealsAdapter.notifyDataSetChanged();
                                     }
                                 }
                    );
        } else {
            networkConnection.displayAlert();
        }
    }

    /**
     * Loads all the auctions from the server with Ion
     */
    private void initializeHotAuctions() {
        auctionsProgress.setVisibility(View.VISIBLE);
        noAuctions.setVisibility(View.GONE);
        auctionsTryAgain.setVisibility(View.GONE);
        auctionsRecyclerView.setVisibility(View.GONE);
        auctionsViewAll.setVisibility(View.GONE);

        if (networkConnection.isInternetOn()) {
            Ion.with(this)
                    .load(Utility.HOST_VALUE + Utility.HOT_AUCTIONS_PATH_VALUE)
                    .asJsonObject()
                    .setCallback(new FutureCallback<JsonObject>() {
                        @Override
                        public void onCompleted(Exception e, JsonObject result) {
                            auctionsProgress.setVisibility(View.GONE);
                            if (e == null) {
                                JsonArray resultArray = result.get("auctionlist").getAsJsonArray();
                                JsonObject resultObject = resultArray.get(0).getAsJsonObject();
                                JsonObject result_inner = resultObject.get("auction_list").getAsJsonObject();

                                int httpCode = result_inner.get("httpCode").getAsInt();

                                switch (httpCode) {
                                    case 200:
                                        auctionsRecyclerView.setVisibility(View.VISIBLE);
                                        auctionsViewAll.setVisibility(View.VISIBLE);

                                        for (int i = 0; i < resultArray.size(); i++) {
                                            resultObject = resultArray.get(i).getAsJsonObject();
                                            result_inner = resultObject.get("auction_list").getAsJsonObject();

                                            try {
                                                JSONObject jsonObject = new JSONObject(result_inner.toString());
                                                auctions.add(new JSONParser(jsonObject));
                                            } catch (JSONException exception) {
                                                exception.printStackTrace();
                                            }
                                            auctionsAdapter.notifyDataSetChanged();
                                            break;
                                        }
                                        break;
                                    case 401:
                                        noAuctions.setText(result_inner.get("Message").getAsString());
                                        noAuctions.setVisibility(View.VISIBLE);
                                        break;
                                }
                            } else {
                                noAuctions.setText(getString(R.string.unable_to_connect));
                                noAuctions.setVisibility(View.VISIBLE);
                                auctionsTryAgain.setVisibility(View.VISIBLE);
                            }
                        }
                    });
        } else {
            networkConnection.displayAlert();
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {

        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_home, menu);

        // Sets up the cart count menu item
        cartBadgeLayout = MenuItemCompat.getActionView(menu.findItem(R.id.cart_badge));
        cartTextView = (TextView) cartBadgeLayout.findViewById(R.id.cart_count_text);
        cartTextView.setText(String.valueOf(HomeActivity.cartCount));

        cartBadgeLayout.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {
                if (HomeActivity.cartCount == 0) {
                    Snackbar.make(homeView, "You have no products in your cart yet.", Snackbar.LENGTH_SHORT).show();
                } else {
                    Intent i = new Intent(HomeActivity.this, CartDetailsActivity.class);
                    i.putExtra("activity_name", HomeActivity.class);
                    startActivity(i);
                    finish();
                }
            }
        });

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
    public void onRestart() {
        super.onRestart();
        try {
            cartTextView.setText(String.valueOf(cartCount));
            wishlistTextView.setText(String.valueOf(wishlistCount));
            compareTextView.setText(String.valueOf(compareCount));
        } catch (NullPointerException ex) {
            ex.printStackTrace();
        }
    }

    @Override
    protected void onResume() {
        super.onResume();

        AppEventsLogger.activateApp(this);
    }

    @Override
    protected void onPause() {
        super.onPause();

        AppEventsLogger.deactivateApp(this);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.cart_badge) {
            Snackbar.make(homeView, "I have been added to cart already, duuh", Snackbar.LENGTH_SHORT).show();
            return true;
        }
        return super.onOptionsItemSelected(item);
    }
}
