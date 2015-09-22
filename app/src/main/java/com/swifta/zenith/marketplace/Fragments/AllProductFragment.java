package com.swifta.zenith.marketplace.Fragments;


import android.os.Bundle;
import android.app.Fragment;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
import com.koushikdutta.async.future.FutureCallback;
import com.koushikdutta.ion.Ion;
import com.swifta.zenith.marketplace.Adapters.AllProductAdapter;
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
public class AllProductFragment extends android.support.v4.app.Fragment {
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


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        rootView = inflater.inflate(R.layout.fragment_all_product, container, false);

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

        allProductAdapter = new AllProductAdapter(getActivity(), products);
        mRecyclerView.setAdapter(allProductAdapter);

        return rootView;
    }

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

}
