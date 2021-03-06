package com.swifta.zenith.marketplace.Fragments;


import android.app.Fragment;
import android.os.Bundle;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.widget.LinearLayoutManager;
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
import com.swifta.zenith.marketplace.Adapters.CategoryAdapter;
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
public class DealsCategoryFragment extends android.support.v4.app.Fragment {

    View v;
    SwipeRefreshLayout mSwipeRefreshLayout;
    RecyclerView mRecyclerView;
    ProgressBar progressBar;
    TextView noDeals;
    NetworkConnection networkConnection;
    ArrayList<JSONParser> categories = new ArrayList<JSONParser>();
    CategoryAdapter categoryAdapter;

    public DealsCategoryFragment() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        v = inflater.inflate(R.layout.fragment_deals_category, container, false);

        mRecyclerView = (RecyclerView) v.findViewById(R.id.category_recycler_view);
        mSwipeRefreshLayout = (SwipeRefreshLayout) v.findViewById(R.id.swipe_refresh_layout);
        networkConnection = new NetworkConnection(getActivity());
        progressBar = (ProgressBar) v.findViewById(R.id.store_progress);
        noDeals = (TextView) v.findViewById(R.id.no_deals);

        mSwipeRefreshLayout.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                categories.clear();
                mSwipeRefreshLayout.setRefreshing(true);
                initializeCategories();
            }
        });

        mSwipeRefreshLayout.setColorSchemeResources(R.color.colorPrimaryDark, R.color.colorAccent);
        mSwipeRefreshLayout.setProgressBackgroundColorSchemeResource(android.R.color.darker_gray);

        mRecyclerView.setLayoutManager(new LinearLayoutManager(getActivity()));

        initializeCategories();

        categoryAdapter = new CategoryAdapter(getActivity(), categories, "DealsCategoryFragment");
        mRecyclerView.setAdapter(categoryAdapter);

        return v;
    }

    /**
     * Loads all the categories from the server with Ion
     */
    private void initializeCategories() {
        if (networkConnection.isInternetOn()) {
            Ion.with(this)
                    .load(Utility.HOST_VALUE + Utility.DEALS_CATEGORY_PATH_VALUE + "16.html")
                    .asJsonObject()
                    .setCallback(new FutureCallback<JsonObject>() {
                        @Override
                        public void onCompleted(Exception e, JsonObject result) {
                            mSwipeRefreshLayout.setRefreshing(false);
                            progressBar.setVisibility(View.GONE);
                            if (e == null) {
                                JsonArray resultArray = result.get("categorylist").getAsJsonArray();
                                JsonObject resultObject = resultArray.get(0).getAsJsonObject();
                                JsonObject result_inner = resultObject.get("category_list").getAsJsonObject();
                                int httpCode = result_inner.get("httpCode").getAsInt();

                                switch (httpCode) {
                                    case 200:
                                        mRecyclerView.setVisibility(View.VISIBLE);

                                        for (int i = 0; i < resultArray.size(); i++) {
                                            resultObject = resultArray.get(i).getAsJsonObject();
                                            result_inner = resultObject.get("category_list").getAsJsonObject();

                                            try {
                                                JSONObject jsonObject = new JSONObject(result_inner.toString());
                                                categories.add(new JSONParser(jsonObject));
                                            } catch (JSONException exception) {

                                            }
                                        }
                                        categoryAdapter.notifyDataSetChanged();
                                        break;
                                    case 401:
                                        noDeals.setText(result_inner.get("Message").getAsString() + "\n" + "Swipe down to refresh");
                                        noDeals.setVisibility(View.VISIBLE);
                                        break;
                                }
                            } else {
                                noDeals.setText(getString(R.string.unable_to_connect) + "\n" + "Swipe down to refresh");
                                noDeals.setVisibility(View.VISIBLE);
                            }
                        }
                    });
        } else {
            networkConnection.displayAlert();
        }
    }

}
