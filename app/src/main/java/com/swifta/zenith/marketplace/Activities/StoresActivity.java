package com.swifta.zenith.marketplace.Activities;

import android.os.Bundle;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.MenuItem;
import android.view.View;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
import com.koushikdutta.async.future.FutureCallback;
import com.koushikdutta.ion.Ion;
import com.swifta.zenith.marketplace.Adapters.StoreAdapter;
import com.swifta.zenith.marketplace.R;
import com.swifta.zenith.marketplace.Utils.JSONParser;
import com.swifta.zenith.marketplace.Utils.NetworkConnection;
import com.swifta.zenith.marketplace.Utils.Utility;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;

public class StoresActivity extends BaseNavigationDrawerActivity {
    View rootView;
    RecyclerView mRecyclerView;
    SwipeRefreshLayout mSwipeRefreshLayout;
    ProgressBar progressBar;
    TextView noProducts;
    NetworkConnection networkConnection;
    ArrayList<JSONParser> store = new ArrayList<JSONParser>();
    StoreAdapter storeAdapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        //setContentView(R.layout.activity_stores);

        // Selects the specific item in the Navigation View and checks it
        MenuItem hMenuItem = mNavigationView.getMenu().getItem(position);
        if (hMenuItem.isChecked()) {
            hMenuItem.setChecked(false);
        } else {
            hMenuItem.setChecked(true);
        }

        // Hides NestedScrollView because it's bad practise to display RecyclerView in a ScrollView
        mNestedScrollView.setVisibility(View.GONE);
        rootView = getLayoutInflater().inflate(R.layout.activity_stores, mCoordinatorLayout);

        mRecyclerView = (RecyclerView) rootView.findViewById(R.id.category_recycler_view);
        mSwipeRefreshLayout = (SwipeRefreshLayout) rootView.findViewById(R.id.swipe_refresh_layout);
        networkConnection = new NetworkConnection(StoresActivity.this);
        progressBar = (ProgressBar) rootView.findViewById(R.id.store_progress);
        noProducts = (TextView) rootView.findViewById(R.id.no_stores);

        mSwipeRefreshLayout.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                store.clear();
                mSwipeRefreshLayout.setRefreshing(true);
                initializeProducts();
            }
        });

        mSwipeRefreshLayout.setColorSchemeResources(R.color.colorPrimaryDark, R.color.colorAccent);
        mSwipeRefreshLayout.setProgressBackgroundColorSchemeResource(android.R.color.darker_gray);

        mRecyclerView.setLayoutManager(new GridLayoutManager(this, 2));

        initializeProducts();

        storeAdapter = new StoreAdapter(this, store);
        mRecyclerView.setAdapter(storeAdapter);
    }

    private void initializeProducts() {
        if (networkConnection.isInternetOn()) {
            Ion.with(this)
                    .load(Utility.HOST_VALUE + Utility.STORE_LIST_PATH_VALUE)
                    .asJsonObject()
                    .setCallback(new FutureCallback<JsonObject>() {
                        @Override
                        public void onCompleted(Exception e, JsonObject result) {
                            mSwipeRefreshLayout.setRefreshing(false);
                            progressBar.setVisibility(View.GONE);
                            if (e == null) {
                                mRecyclerView.setVisibility(View.VISIBLE);

                                JsonArray resultArray = result.get("storelist").getAsJsonArray();
                                for (int i = 0; i < resultArray.size(); i++) {
                                    JsonObject resultObject = resultArray.get(i).getAsJsonObject();
                                    JsonObject result_inner = resultObject.get("store_list").getAsJsonObject();

                                    try {
                                        JSONObject jsonObject = new JSONObject(result_inner.toString());
                                        store.add(new JSONParser(jsonObject));
                                    } catch (JSONException exception) {

                                    }
                                }
                                storeAdapter.notifyDataSetChanged();

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
}
