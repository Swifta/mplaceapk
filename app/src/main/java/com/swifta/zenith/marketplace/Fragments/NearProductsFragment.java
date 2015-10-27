package com.swifta.zenith.marketplace.Fragments;

import android.app.AlertDialog;
import android.app.Fragment;
import android.content.DialogInterface;
import android.content.Intent;
import android.location.Location;
import android.os.Bundle;
import android.provider.Settings;
import android.support.design.widget.Snackbar;
import android.support.v4.view.MenuItemCompat;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.GooglePlayServicesUtil;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationServices;
import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
import com.koushikdutta.async.future.FutureCallback;
import com.koushikdutta.ion.Ion;
import com.swifta.zenith.marketplace.Activities.CartDetailsActivity;
import com.swifta.zenith.marketplace.Activities.HomeActivity;
import com.swifta.zenith.marketplace.Adapters.AllProductAdapter;
import com.swifta.zenith.marketplace.R;
import com.swifta.zenith.marketplace.Utils.JSONParser;
import com.swifta.zenith.marketplace.Utils.NetworkConnection;
import com.swifta.zenith.marketplace.Utils.Session;
import com.swifta.zenith.marketplace.Utils.Utility;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;

/**
 * A simple {@link Fragment} subclass.
 */
public class NearProductsFragment extends android.support.v4.app.Fragment implements GoogleApiClient.ConnectionCallbacks, GoogleApiClient.OnConnectionFailedListener {
    public static final int FRAGMENT_TAG = 3;
    private final static int PLAY_SERVICES_RESOLUTION_REQUEST = 10000;
    static TextView cartTextView;
    static TextView wishlistTextView;
    static TextView compareTextView;
    View rootView;
    RecyclerView mRecyclerView;
    SwipeRefreshLayout mSwipeRefreshLayout;
    ProgressBar progressBar;
    TextView noProducts;
    NetworkConnection networkConnection;
    GoogleApiClient mGoogleApiClient;
    double mLongitude;
    double mLatitude;
    Location mLocation;
    LocationRequest mLocationRequest;
    ArrayList<JSONParser> productList = new ArrayList<JSONParser>();
    AllProductAdapter nearProductsAdapter;

    public NearProductsFragment() {
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
        rootView = inflater.inflate(R.layout.fragment_near_products, container, false);

        // Enables the menu to be displayed in the fragment
        setHasOptionsMenu(true);

        mRecyclerView = (RecyclerView) rootView.findViewById(R.id.near_products_recycler);
        mSwipeRefreshLayout = (SwipeRefreshLayout) rootView.findViewById(R.id.swipe_refresh_layout);
        networkConnection = new NetworkConnection(getActivity());
        progressBar = (ProgressBar) rootView.findViewById(R.id.near_deals_progress);
        noProducts = (TextView) rootView.findViewById(R.id.no_stores);

        // Checks the availability of Google Play services
        if (checkPlayServices()) {
            buildGoogleAPIClient();
        }

        mSwipeRefreshLayout.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                productList.clear();
                mSwipeRefreshLayout.setRefreshing(true);
                initializeProducts();
            }
        });

        mSwipeRefreshLayout.setColorSchemeResources(R.color.colorPrimaryDark, R.color.colorAccent);
        mSwipeRefreshLayout.setProgressBackgroundColorSchemeResource(android.R.color.darker_gray);

        mRecyclerView.setLayoutManager(new GridLayoutManager(getActivity(), 2));

        // Retrieve data from the server
        initializeProducts();

        nearProductsAdapter = new AllProductAdapter(getActivity(), productList, FRAGMENT_TAG);
        mRecyclerView.setAdapter(nearProductsAdapter);

        return rootView;
    }

    /**
     * Loads all the near products from the server with Ion
     */
    private void initializeProducts() {
        if (networkConnection.isInternetOn()) {

            if (!mGoogleApiClient.isConnected()) {
                mGoogleApiClient.connect();
            }

            if (Session.getLatitude(getActivity()) != null && Session.getLongitude(getActivity()) != null) {

                String location = Session.getLatitude(getActivity()) + "/" + Session.getLongitude(getActivity()) + ".html";

                Ion.with(this)
                        .load(Utility.HOST_VALUE + Utility.NEAR_PRODUCTS_PATH_VALUE + location)
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
                                                    productList.add(new JSONParser(jsonObject));
                                                } catch (JSONException jsonException) {
                                                    jsonException.printStackTrace();
                                                }
                                            }
                                            nearProductsAdapter.notifyDataSetChanged();
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
            }
        } else {
            networkConnection.displayAlert();
        }

    }

    private boolean checkPlayServices() {
        int resultCode = GooglePlayServicesUtil.isGooglePlayServicesAvailable(getActivity());
        if (resultCode != ConnectionResult.SUCCESS) {
            if (GooglePlayServicesUtil.isUserRecoverableError(resultCode)) {
                GooglePlayServicesUtil.getErrorDialog(resultCode, getActivity(), PLAY_SERVICES_RESOLUTION_REQUEST).show();
            } else {
                Snackbar.make(rootView, "This device is not supported", Snackbar.LENGTH_LONG).show();
            }
            return false;
        }
        return true;
    }

    protected synchronized void buildGoogleAPIClient() {
        mGoogleApiClient = new GoogleApiClient.Builder(getActivity())
                .addConnectionCallbacks(this)
                .addOnConnectionFailedListener(this)
                .addApi(LocationServices.API)
                .build();
    }

    @Override
    public void onStart() {
        super.onStart();
        if (mGoogleApiClient != null) {
            mGoogleApiClient.connect();
        }
    }

    @Override
    public void onResume() {
        super.onResume();

        checkPlayServices();
    }

    @Override
    public void onConnected(Bundle bundle) {
        getCurrentLocation();
    }

    @Override
    public void onConnectionSuspended(int i) {
        mGoogleApiClient.connect();
    }

    @Override
    public void onConnectionFailed(ConnectionResult connectionResult) {

    }

    /**
     * Gets the current location of the device and enables the GPS if it's turned off
     **/
    private void getCurrentLocation() {
        mLocation = LocationServices.FusedLocationApi
                .getLastLocation(mGoogleApiClient);

        if (mLocation != null) {
            mLatitude = mLocation.getLatitude();
            mLongitude = mLocation.getLongitude();

            Session.saveLongitude(String.valueOf(mLongitude), getActivity());
            Session.saveLatitude(String.valueOf(mLatitude), getActivity());
        } else {
            AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
            builder.setMessage("Your GPS is disabled. Do you want to enbale it?")
                    .setCancelable(false)
                    .setPositiveButton("Yes", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialogInterface, int i) {
                            startActivity(new Intent(Settings.ACTION_LOCATION_SOURCE_SETTINGS));
                            getCurrentLocation();
                        }
                    })
                    .setNegativeButton("No", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialogInterface, int i) {
                            dialogInterface.dismiss();
                        }
                    });
            AlertDialog alert = builder.create();
            alert.show();
        }
    }

    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater menuInflater) {
        // Inflate the menu; this adds items to the action bar if it is present.
        menuInflater.inflate(R.menu.menu_all_products, menu);

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
}

