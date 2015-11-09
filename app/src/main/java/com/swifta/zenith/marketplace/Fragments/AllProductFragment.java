package com.swifta.zenith.marketplace.Fragments;


import android.app.AlertDialog;
import android.app.Fragment;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.support.v4.view.MenuItemCompat;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.SearchView;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ListView;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
import com.koushikdutta.async.future.FutureCallback;
import com.koushikdutta.ion.Ion;
import com.swifta.zenith.marketplace.Activities.AllProductsActivity;
import com.swifta.zenith.marketplace.Activities.CartDetailsActivity;
import com.swifta.zenith.marketplace.Activities.HomeActivity;
import com.swifta.zenith.marketplace.Adapters.AllProductAdapter;
import com.swifta.zenith.marketplace.R;
import com.swifta.zenith.marketplace.Utils.Dictionary;
import com.swifta.zenith.marketplace.Utils.JSONParser;
import com.swifta.zenith.marketplace.Utils.NetworkConnection;
import com.swifta.zenith.marketplace.Utils.Utility;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

/**
 * A simple {@link Fragment} subclass.
 */
public class AllProductFragment extends android.support.v4.app.Fragment implements SearchView.OnQueryTextListener {
    public static final int FRAGMENT_TAG = 1;
    static TextView cartTextView;
    static TextView wishlistTextView;
    static TextView compareTextView;
    View rootView;
    RecyclerView mRecyclerView;
    SwipeRefreshLayout mSwipeRefreshLayout;
    ProgressBar progressBar;
    TextView noProducts;
    NetworkConnection networkConnection;
    ArrayList<JSONParser> products = new ArrayList<JSONParser>();
    AllProductAdapter allProductAdapter;

    public AllProductFragment() {
        // Required empty public constructor
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

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        rootView = inflater.inflate(R.layout.fragment_all_product, container, false);

        // Enables the menu to be displayed in the fragment
        setHasOptionsMenu(true);

        mRecyclerView = (RecyclerView) rootView.findViewById(R.id.all_products_recycler);
        mSwipeRefreshLayout = (SwipeRefreshLayout) rootView.findViewById(R.id.swipe_refresh_layout);
        networkConnection = new NetworkConnection(getActivity());
        progressBar = (ProgressBar) rootView.findViewById(R.id.near_deals_progress);
        noProducts = (TextView) rootView.findViewById(R.id.no_stores);

        mSwipeRefreshLayout.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                products.clear();
                mSwipeRefreshLayout.setRefreshing(true);
                initializeProducts();
            }
        });

        mSwipeRefreshLayout.setColorSchemeResources(R.color.colorPrimaryDark, R.color.colorAccent);
        mSwipeRefreshLayout.setProgressBackgroundColorSchemeResource(android.R.color.darker_gray);

        mRecyclerView.setLayoutManager(new GridLayoutManager(getActivity(), 2));

        initializeProducts();

        allProductAdapter = new AllProductAdapter(getActivity(), products, FRAGMENT_TAG);
        mRecyclerView.setAdapter(allProductAdapter);

        return rootView;
    }

    /**
     * Loads all the products from the server with Ion
     */
    private void initializeProducts() {
        if (networkConnection.isInternetOn()) {
            Ion.with(this)
                    .load(Utility.HOST_VALUE + Utility.ALL_PRODUCTS_PATH_VALUE)
                    .asJsonObject()
                    .setCallback(new FutureCallback<JsonObject>() {
                        @Override
                        public void onCompleted(Exception e, JsonObject result) {
                            mSwipeRefreshLayout.setRefreshing(false);
                            progressBar.setVisibility(View.GONE);
                            if (e == null) {
                                JsonArray resultArray = result.get("productlist").getAsJsonArray();
                                JsonObject resultObject = resultArray.get(0).getAsJsonObject();
                                JsonObject result_inner = resultObject.get("product_list").getAsJsonObject();
                                int httpCode = result_inner.get("httpCode").getAsInt();

                                switch (httpCode) {
                                    case 200:
                                        mRecyclerView.setVisibility(View.VISIBLE);

                                        for (int i = 0; i < resultArray.size(); i++) {
                                            resultObject = resultArray.get(i).getAsJsonObject();
                                            result_inner = resultObject.get("product_list").getAsJsonObject();
                                            try {
                                                JSONObject jsonObject = new JSONObject(result_inner.toString());
                                                products.add(new JSONParser(jsonObject));
                                            } catch (JSONException jsonException) {
                                                jsonException.printStackTrace();
                                            }
                                        }
                                        allProductAdapter.notifyDataSetChanged();
                                        break;
                                    case 401:
                                        noProducts.setText(result_inner.get("Message").getAsString() + "\n" + "Swipe down to refresh");
                                        noProducts.setVisibility(View.VISIBLE);
                                        break;
                                }
                            } else {
                                noProducts.setText(getString(R.string.unable_to_connect));
                                noProducts.setVisibility(View.VISIBLE);
                            }
                        }
                    });
        } else {
            networkConnection.displayAlert();
        }
    }

    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater menuInflater) {
        // Inflate the menu; this adds items to the action bar if it is present.
        menuInflater.inflate(R.menu.menu_all_products, menu);

        MenuItem item = menu.findItem(R.id.search_badge);
        SearchView searchView = (SearchView) MenuItemCompat.getActionView(item);
        searchView.setOnQueryTextListener(this);

        // Sets up the cart count menu item
        View cartBadgeLayout = MenuItemCompat.getActionView(menu.findItem(R.id.cart_badge));
        cartTextView = (TextView) cartBadgeLayout.findViewById(R.id.cart_count_text);
        cartTextView.setText(String.valueOf(HomeActivity.cartCount));

        cartBadgeLayout.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {
                if (HomeActivity.cartCount == 0) {
                    new AlertDialog.Builder(getActivity())
                            .setMessage("You have no products in your cart yet.")
                            .setCancelable(true)
                            .setPositiveButton("OK", new DialogInterface.OnClickListener() {
                                @Override
                                public void onClick(DialogInterface dialogInterface, int i) {
                                    dialogInterface.dismiss();
                                }
                            })
                            .show();
                } else {
                    Intent i = new Intent(getActivity(), CartDetailsActivity.class);
                    i.putExtra("activity_name", AllProductsActivity.class);
                    startActivity(i);
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

    @Override
    public boolean onQueryTextChange(String query) {
        // Here is where we are going to implement our filter logic
        final List<JSONParser> filteredModelList = filter(this.products, query);

        // Can't successfully figure out a way to notify the adapter of changes to the list
        // so calling the adapter on every change was implemented instead
        allProductAdapter = new AllProductAdapter(getActivity(), filteredModelList, FRAGMENT_TAG);
        mRecyclerView.setAdapter(allProductAdapter);

//        allProductAdapter.animateTo(filteredModelList);
//        mRecyclerView.scrollToPosition(0);
        return true;
    }

    /**
     * Searches through a list and creates a new list based on the matching items from the query passed to it
     **/
    private List<JSONParser> filter(List<JSONParser> models, String query) {
        query = query.toLowerCase();

        final List<JSONParser> filteredModelList = new ArrayList<JSONParser>();

        // Search through the List if there is a query
        for (JSONParser jsonParser : models) {
            String text = jsonParser.getProperty(Dictionary.dealTitle).toString().toLowerCase();
            if (text.contains(query)) {
                filteredModelList.add(jsonParser);
            }
        }
        return filteredModelList;
    }

    @Override
    public boolean onQueryTextSubmit(String query) {
        return false;
    }
}
