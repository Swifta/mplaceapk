package com.swifta.zenith.marketplace.Fragments;


import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.location.Location;
import android.os.Bundle;
import android.app.Fragment;
import android.provider.Settings;
import android.support.design.widget.Snackbar;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
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
import com.swifta.zenith.marketplace.Adapters.ProductsAdapter;
import com.swifta.zenith.marketplace.R;
import com.swifta.zenith.marketplace.Utils.JSONParser;
import com.swifta.zenith.marketplace.Utils.NetworkConnection;
import com.swifta.zenith.marketplace.Utils.Utility;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;

/**
 * A simple {@link Fragment} subclass.
 */
public class NearProductsFragment extends android.support.v4.app.Fragment implements GoogleApiClient.ConnectionCallbacks, GoogleApiClient.OnConnectionFailedListener {
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
    ArrayList<JSONParser> products = new ArrayList<JSONParser>();
    ProductsAdapter productsAdapter;
    private final static int PLAY_SERVICES_RESOLUTION_REQUEST = 10000;
    private final static int UPDATE_INTERVAL = 1000;
    private final static int FASTEST_INTERVAL = 5000;
    private final static int DISPLACEMENT = 10;


    public NearProductsFragment() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        rootView = inflater.inflate(R.layout.fragment_near_products, container, false);

        mRecyclerView = (RecyclerView) rootView.findViewById(R.id.all_products_recycler);
        mSwipeRefreshLayout = (SwipeRefreshLayout) rootView.findViewById(R.id.swipe_refresh_layout);
        networkConnection = new NetworkConnection(getActivity());
        progressBar = (ProgressBar) rootView.findViewById(R.id.near_deals_progress);
        noProducts = (TextView) rootView.findViewById(R.id.no_stores);

        // Checks the availability of Google Play services
        if (checkPlayServices()) {
            buildGoogleAPIClient();

            // createLocationRequest();
        }

        if (mGoogleApiClient != null) {
            mGoogleApiClient.connect();
        }

        // Gets Location from Google API
        //getCurrentLocation();

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

        // Retrieve data from the server
        initializeProducts();

        productsAdapter = new ProductsAdapter(getActivity(), products);
        mRecyclerView.setAdapter(productsAdapter);

        return rootView;
    }

    private void initializeProducts() {
        if (networkConnection.isInternetOn()) {

            if (!mGoogleApiClient.isConnected()) {
                mGoogleApiClient.connect();
            }

            if (mLatitude != 0 && mLongitude != 0) {

                String location = String.valueOf(mLatitude) + "/" + String.valueOf(mLongitude) + ".html";

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
                                                    products.add(new JSONParser(jsonObject));
                                                } catch (JSONException jsonException) {

                                                }
                                            }
                                            productsAdapter.notifyDataSetChanged();
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

    protected void createLocationRequest() {
        mLocationRequest = new LocationRequest();
        mLocationRequest.setInterval(UPDATE_INTERVAL);
        mLocationRequest.setFastestInterval(FASTEST_INTERVAL);
        mLocationRequest.setPriority(LocationRequest.PRIORITY_HIGH_ACCURACY);
        mLocationRequest.setSmallestDisplacement(DISPLACEMENT);
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

    private void getCurrentLocation() {
        mLocation = LocationServices.FusedLocationApi
                .getLastLocation(mGoogleApiClient);
        if (mLocation != null) {
            mLatitude = mLocation.getLatitude();
            mLongitude = mLocation.getLongitude();
        } else {
            AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
            builder.setMessage("Your GPS is disabled. Do you want to enbale it?")
                    .setCancelable(false)
                    .setPositiveButton("Yes", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialogInterface, int i) {
                            startActivity(new Intent(Settings.ACTION_LOCATION_SOURCE_SETTINGS));
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
}

