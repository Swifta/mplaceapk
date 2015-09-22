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
import com.swifta.zenith.marketplace.Adapters.AllAuctionAdapter;
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
public class AuctionsCategoryListFragment extends android.support.v4.app.Fragment {
    View rootView;
    RecyclerView mRecyclerView;
    SwipeRefreshLayout mSwipeRefreshLayout;
    ProgressBar progressBar;
    TextView noAuctions;
    NetworkConnection networkConnection;
    public ArrayList<JSONParser> auctionsCategoryList = new ArrayList<JSONParser>();
    AllAuctionAdapter allAuctionAdapter;
    String categoryId;
    Bundle bundle;

    public AuctionsCategoryListFragment() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        rootView = inflater.inflate(R.layout.fragment_auctions_category_list, container, false);

        mRecyclerView = (RecyclerView) rootView.findViewById(R.id.auctions_category_list_recycler);
        mSwipeRefreshLayout = (SwipeRefreshLayout) rootView.findViewById(R.id.swipe_refresh_layout);
        networkConnection = new NetworkConnection(getActivity());
        progressBar = (ProgressBar) rootView.findViewById(R.id.auctions_category_list_progress);
        noAuctions = (TextView) rootView.findViewById(R.id.no_auctions);

        // Gets the data from the previous fragment
        bundle = getArguments();
        if (bundle != null) {
            categoryId = bundle.getString("subcategory_id");
        }

        mSwipeRefreshLayout.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                auctionsCategoryList.clear();
                mSwipeRefreshLayout.setRefreshing(true);
                initializeAuctionsCategoryList();
            }
        });

        mSwipeRefreshLayout.setColorSchemeResources(R.color.colorPrimaryDark, R.color.colorAccent);
        mSwipeRefreshLayout.setProgressBackgroundColorSchemeResource(android.R.color.darker_gray);

        mRecyclerView.setLayoutManager(new GridLayoutManager(getActivity(), 2));

        initializeAuctionsCategoryList();

        allAuctionAdapter = new AllAuctionAdapter(
                getActivity(), auctionsCategoryList);
        mRecyclerView.setAdapter(allAuctionAdapter);

        return rootView;
    }

    /**
     * Gets the data from the server using the Ion Library
     */
    private void initializeAuctionsCategoryList() {
        if (networkConnection.isInternetOn()) {
            Ion.with(this)
                    .load(Utility.HOST_VALUE + Utility.AUCTIONS_CATEGORY_LIST_PATH_VALUE
                            + "2/" + categoryId + "/none.html")
                    .asJsonObject()
                    .setCallback(new FutureCallback<JsonObject>() {
                                     @Override
                                     public void onCompleted(Exception e, JsonObject result) {
                                         mSwipeRefreshLayout.setRefreshing(false);
                                         progressBar.setVisibility(View.GONE);
                                         if (e == null) {
                                             JsonArray resultArray = result.get("auctionlist").getAsJsonArray();
                                             JsonObject resultObject = resultArray.get(0).getAsJsonObject();
                                             JsonObject result_inner = resultObject.get("auction_list").getAsJsonObject();
                                             int httpCode = result_inner.get("httpCode").getAsInt();

                                             switch (httpCode) {
                                                 case 200:
                                                     mRecyclerView.setVisibility(View.VISIBLE);

                                                     for (int i = 0; i < resultArray.size(); i++) {
                                                         resultObject = resultArray.get(i).getAsJsonObject();
                                                         result_inner = resultObject.get("auction_list").getAsJsonObject();
                                                         try {
                                                             JSONObject jsonObject = new JSONObject(result_inner.toString());
                                                             auctionsCategoryList.add(new JSONParser(jsonObject));
                                                         } catch (JSONException jsonException) {
                                                             jsonException.printStackTrace();
                                                         }
                                                     }

                                                     allAuctionAdapter.notifyDataSetChanged();
                                                     break;
                                                 case 401:
                                                     noAuctions.setText(result_inner.get("Message").getAsString() + "\n" + "Swipe down to refresh");
                                                     noAuctions.setVisibility(View.VISIBLE);
                                                     break;
                                             }
                                         } else {
                                             noAuctions.setText(getString(R.string.unable_to_connect));
                                             noAuctions.setVisibility(View.VISIBLE);
                                         }
                                     }
                                 }

                    );
        } else {
            networkConnection.displayAlert();
        }
    }


}
