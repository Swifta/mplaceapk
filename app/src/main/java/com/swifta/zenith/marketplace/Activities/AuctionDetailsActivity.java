package com.swifta.zenith.marketplace.Activities;

import android.annotation.TargetApi;
import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.os.CountDownTimer;
import android.provider.Settings;
import android.support.design.widget.Snackbar;
import android.support.v4.content.ContextCompat;
import android.support.v4.view.MenuItemCompat;
import android.support.v7.widget.CardView;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.text.Html;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
import com.koushikdutta.async.future.FutureCallback;
import com.koushikdutta.ion.Ion;
import com.swifta.zenith.marketplace.Adapters.SimilarAuctionsAdapter;
import com.swifta.zenith.marketplace.R;
import com.swifta.zenith.marketplace.Utils.Dictionary;
import com.swifta.zenith.marketplace.Utils.JSONParser;
import com.swifta.zenith.marketplace.Utils.NetworkConnection;
import com.swifta.zenith.marketplace.Utils.Timer;
import com.swifta.zenith.marketplace.Utils.Utility;

import org.json.JSONException;
import org.json.JSONObject;

import java.text.ParseException;
import java.util.ArrayList;

public class AuctionDetailsActivity extends BaseToolbarActivity {
    View rootView;
    View floatingButtonView;
    JSONParser auctions;
    TextView auctionsTryAgain;
    TextView noAuctions;
    TextView detailName;
    TextView detailDescription;
    TextView bidPrice;
    TextView terms;
    TextView timeLeft;
    TextView storeName;
    TextView storeCityName;
    TextView storeAddress;
    TextView cartTextView;
    TextView wishlistTextView;
    TextView compareTextView;
    ImageView detailsImage;
    ImageView storeImage;
    TextView retailPrice;
    TextView auctionType;
    TextView shippingInfo;
    TextView shippingAmount;
    TextView noBidhistory;
    TextView startTime;
    TextView endTime;
    TextView totalBid;
    TextView bidderName;
    TextView bidAmount;
    TextView bidTime;
    Button shareButton;
    int imageArrayPosition = 0;
    String auctionsId;
    String auctionsKey;
    String deviceNumber;
    String auctionsName;
    String shareText;
    String storeUrlTitle;
    LinearLayout termsLayout;
    LinearLayout bidLayout;
    android.support.design.widget.FloatingActionButton floatingActionButton;
    RecyclerView mRecyclerView;
    SimilarAuctionsAdapter similarAuctionsAdapter;
    LinearLayoutManager linearLayoutManager;
    ProgressBar auctionsProgress;
    NetworkConnection networkConnection;
    String[] imageArray;
    CardView similarAuctionsCardview;
    ArrayList<JSONParser> similarAuctionsList = new ArrayList<JSONParser>();
    ArrayList<JSONParser> auctionDetails = new ArrayList<JSONParser>();
    ArrayList<JSONParser> storeList = new ArrayList<JSONParser>();

    @TargetApi(Build.VERSION_CODES.LOLLIPOP)
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        rootView = getLayoutInflater().inflate(R.layout.activity_auction_details, mNestedScrollView);

        // Sets the status bar color
        Window window = getWindow();
        window.addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS);
        window.clearFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS);
        if (android.os.Build.VERSION.SDK_INT >= 21) {
            window.setStatusBarColor(ContextCompat.getColor(this, R.color.colorPrimaryDarker));
        }

        floatingButtonView = getLayoutInflater().inflate(R.layout.auction_floating_action_button, mCoordinatorLayout);

        noAuctions = (TextView) rootView.findViewById(R.id.no_stores);
        auctionsTryAgain = (TextView) rootView.findViewById(R.id.auctions_try_again);
        detailName = (TextView) rootView.findViewById(R.id.detail_name);
        detailDescription = (TextView) rootView.findViewById(R.id.detail_description);
        terms = (TextView) rootView.findViewById(R.id.terms);
        timeLeft = (TextView) rootView.findViewById(R.id.time_left);
        storeName = (TextView) rootView.findViewById(R.id.store_name);
        storeCityName = (TextView) rootView.findViewById(R.id.store_city);
        storeAddress = (TextView) rootView.findViewById(R.id.store_address);
        storeImage = (ImageView) rootView.findViewById(R.id.store_image);
        bidPrice = (TextView) rootView.findViewById(R.id.bid_price_value);
        retailPrice = (TextView) rootView.findViewById(R.id.retail_price_value);
        auctionType = (TextView) rootView.findViewById(R.id.auction_type_value);
        shippingInfo = (TextView) rootView.findViewById(R.id.shipping_info);
        shippingAmount = (TextView) rootView.findViewById(R.id.shipping_amount);
        noBidhistory = (TextView) rootView.findViewById(R.id.no_bid_history);
        startTime = (TextView) rootView.findViewById(R.id.start_time_value);
        endTime = (TextView) rootView.findViewById(R.id.end_time_value);
        totalBid = (TextView) rootView.findViewById(R.id.total_bid);
        bidderName = (TextView) rootView.findViewById(R.id.bidder_name);
        bidAmount = (TextView) rootView.findViewById(R.id.bid_amount);
        bidTime = (TextView) rootView.findViewById(R.id.bid_time);
        shareButton = (Button) rootView.findViewById(R.id.share_auction);
        detailsImage = (ImageView) rootView.findViewById(R.id.details_image);
        auctionsProgress = (ProgressBar) rootView.findViewById(R.id.auctions_progress);
        mRecyclerView = (RecyclerView) rootView.findViewById(R.id.similar_auction_recycler_view);
        similarAuctionsCardview = (CardView) rootView.findViewById(R.id.similar_auction_cardview);
        bidLayout = (LinearLayout) rootView.findViewById(R.id.bid_history_linear_layout);
        termsLayout = (LinearLayout) rootView.findViewById(R.id.termsLayout);
        floatingActionButton = (android.support.design.widget.FloatingActionButton) floatingButtonView.findViewById(R.id.floating_action_button);

        similarAuctionsCardview.setVisibility(View.GONE);

        // Gets the data from the previous activity
        Bundle bundle = getIntent().getExtras();

        if (bundle != null) {
            String data = bundle.getString("auctions");
            try {
                auctions = new JSONParser(new JSONObject(data));

                // Checks if the bundle is from Similar products
                if (auctions.getProperty("deals_id").toString().equals("Not found")) {
                    auctionsId = auctions.getProperty("deal_id").toString();
                    auctionsKey = auctions.getProperty("deal_key").toString();
                } else {
                    auctionsId = auctions.getProperty("deal_id").toString();
                    auctionsKey = auctions.getProperty("deal_key").toString();
                }

            } catch (JSONException e) {
                e.printStackTrace();
            }
        }

        // Loads the image into the imageview with Ion
        Ion.with(detailsImage)
                .placeholder(R.drawable.home_background)
                .load(auctions.getProperty(Dictionary.imageUrl).toString());

        networkConnection = new NetworkConnection(this);

        linearLayoutManager = new LinearLayoutManager(this);
        linearLayoutManager.setOrientation(LinearLayoutManager.HORIZONTAL);
        mRecyclerView.setLayoutManager(linearLayoutManager);

        // Gets the device number of the specific Android device
        deviceNumber = Settings.Secure.getString(this.getContentResolver(), Settings.Secure.ANDROID_ID);

        // Get the aution details from the server
        initializeAuctionDetails();

        floatingActionButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Snackbar.make(rootView, shareText, Snackbar.LENGTH_SHORT);
            }
        });

    }

    /**
     * Gets the auctions details from the server based on the Deal Id and Deal Key
     */
    void initializeAuctionDetails() {
        if (networkConnection.isInternetOn()) {
            Ion.with(this)
                    .load(Utility.HOST_VALUE + Utility.AUCTIONS_DETAILS + auctionsId + "/" + auctionsKey + "/" + deviceNumber + ".html")
                    .asJsonObject()
                    .setCallback(new FutureCallback<JsonObject>() {
                        @Override
                        public void onCompleted(Exception e, JsonObject result) {

                            if (e == null) {
                                JsonArray resultArray = result.get("auctiondetail").getAsJsonArray();
                                JsonObject resultObject = resultArray.get(0).getAsJsonObject();
                                JsonObject result_inner = resultObject.get("auction_details").getAsJsonObject();

                                int httpCode = result_inner.get("httpCode").getAsInt();

                                switch (httpCode) {
                                    case 200:

                                        for (int i = 0; i < resultArray.size(); i++) {
                                            resultObject = resultArray.get(i).getAsJsonObject();
                                            result_inner = resultObject.get("auction_details").getAsJsonObject();

                                            try {
                                                JSONObject jsonObject = new JSONObject(result_inner.toString());

                                                auctionDetails.add(new JSONParser(jsonObject));

                                            } catch (JSONException exception) {
                                                exception.printStackTrace();
                                            }
                                        }

                                        imageArray = auctionDetails.get(0).getProperty(Dictionary.imageUrl).toString().split(",");
                                        auctionsName = auctionDetails.get(0).getProperty(Dictionary.dealTitle).toString();

                                        getSupportActionBar().setTitle(auctionDetails.get(0).getProperty(Dictionary.category).toString());
                                        detailName.setText(auctionDetails.get(0).getProperty(Dictionary.dealTitle).toString());
                                        // Displays HTML in the Textview
                                        detailDescription.setText(Html.fromHtml(auctionDetails.get(0).getProperty(Dictionary.description).toString()));
                                        String termsText = auctionDetails.get(0).getProperty(Dictionary.termsConditions).toString();
                                        if (termsText.equals("") || termsText.equals(null)) {
                                            termsLayout.setVisibility(View.GONE);
                                        } else {
                                            terms.setText(Html.fromHtml(termsText));
                                        }

                                        try {
                                            startTime.setText(Timer.changeDateFormat(auctionDetails.get(0).getProperty(Dictionary.startdate).toString()));
                                            endTime.setText(Timer.changeDateFormat(auctionDetails.get(0).getProperty(Dictionary.enddate).toString()));
                                        } catch (ParseException ex) {
                                            ex.printStackTrace();
                                        }

                                        retailPrice.setText(auctionDetails.get(0).getProperty(Dictionary.currencySymbol).toString()
                                                + auctionDetails.get(0).getProperty(Dictionary.retailPrice).toString());
                                        bidPrice.setText(auctionDetails.get(0).getProperty(Dictionary.currencySymbol).toString()
                                                + auctionDetails.get(0).getProperty(Dictionary.bidValue).toString());

                                        // Checks if the there is a bid on the auction by checking if the bid history returns
                                        // a JsonObject or a JsonArray
                                        if (result_inner.get("bidhistory") instanceof JsonObject) {
                                            JsonObject bidHistoryObject = result_inner.get("bidhistory").getAsJsonObject();
                                            JsonObject response = bidHistoryObject.get("response").getAsJsonObject();

                                            noBidhistory.setText(response.get("Message").getAsString());
                                        } else {
                                            noBidhistory.setVisibility(View.GONE);
                                            bidLayout.setVisibility(View.VISIBLE);

                                            JsonArray bidHistoryArray = result_inner.get("bidhistory").getAsJsonArray();
                                            JsonObject response = bidHistoryArray.get(0).getAsJsonObject();
                                            JsonObject bidResponse = response.get("bid_history").getAsJsonObject();
                                            String bidCount = bidResponse.get("bid_count").getAsString();

                                            if (bidCount.equals("1")) {
                                                totalBid.setText(bidCount + "bid");
                                            } else {
                                                totalBid.setText(bidCount + "bids");
                                            }

                                            bidderName.setText(bidResponse.get("name").getAsString());
                                            bidAmount.setText(bidResponse.get("bid_amount").getAsString());
                                            bidTime.setText(bidResponse.get("transaction_time").getAsString());
                                        }

                                        auctionType.setText(auctionDetails.get(0).getProperty(Dictionary.auctionType).toString());
                                        shippingInfo.setText(auctionDetails.get(0).getProperty(Dictionary.shippingInformation).toString());
                                        shippingAmount.setText(auctionDetails.get(0).getProperty(Dictionary.currencySymbol).toString()
                                                + auctionDetails.get(0).getProperty(Dictionary.shippingAmount).toString());

                                        // Gets the date in strings and converts it to a usable format
                                        String endDate = auctionDetails.get(0).getProperty("enddate").toString();

                                        // Sets the timer to the difference between the current date and the end date;
                                        try {
                                            new CountDownTimer(Timer.getDateDifference(endDate), 1000) {
                                                @Override
                                                public void onTick(long millisUntilFinished) {
                                                    // Displays the time left in days, hours and seconds
                                                    timeLeft.setText(Timer.calculateTime(millisUntilFinished / 1000));
                                                }

                                                @Override
                                                public void onFinish() {
                                                    timeLeft.setText("00:00:00");
                                                }
                                            }.start();
                                        } catch (ParseException e1) {
                                            e1.printStackTrace();
                                        }

                                        storeName.setText(auctionDetails.get(0).getProperty("store_name").toString());
                                        storeCityName.setText(auctionDetails.get(0).getProperty("store_city_name").toString());
                                        storeAddress.setText(auctionDetails.get(0).getProperty("store_address").toString());

                                        // Forms the {shop_url_title} by replacing the empty string items with a dash (-)
                                        storeUrlTitle = auctionDetails.get(0).getProperty("store_name").toString().replaceAll(" ", "-");

                                        shareText = Utility.HOST_VALUE + "/" + storeUrlTitle
                                                + "/auction/" + auctionDetails.get(0).getProperty("deal_key").toString()
                                                + "/"
                                                + auctionDetails.get(0).getProperty("deal_url_title").toString()
                                                + ".html";

                                        Ion.with(storeImage)
                                                .placeholder(R.drawable.home_background)
                                                .load(auctionDetails.get(0).getProperty("store_image_url").toString());

                                        shareButton.setClickable(true);

                                        initializeSimilarAuctions();

                                        break;
                                    case 401:
                                        Snackbar.make(rootView, result_inner.get("Message").getAsString(), Snackbar.LENGTH_LONG);
                                        break;
                                }
                            } else {
                                Snackbar.make(rootView, getString(R.string.unable_to_connect), Snackbar.LENGTH_LONG);
                            }
                        }
                    });
        } else {
            networkConnection.displayAlert();
        }
    }

    /**
     * Send the store details to the StoreActivity
     */
    public void onClick(View view) {
        switch (view.getId()) {
            case (R.id.view_detail):
                JSONObject item = new JSONObject();
                try {
                    item.put("store_image", auctionDetails.get(0).getProperty(Dictionary.productStoreImage).toString());
                    item.put("store_name", auctionDetails.get(0).getProperty(Dictionary.storeName).toString());
                    item.put("address1", auctionDetails.get(0).getProperty(Dictionary.storeAddress).toString());
                    item.put("city_name", auctionDetails.get(0).getProperty(Dictionary.storeCityName).toString());
                    item.put("phone_number", auctionDetails.get(0).getProperty(Dictionary.storePhoneNumber).toString());
                    item.put("website", auctionDetails.get(0).getProperty(Dictionary.storeWebsite).toString());
                    item.put("latitude", auctionDetails.get(0).getProperty(Dictionary.storeLatitude).toString());
                    item.put("longitude", auctionDetails.get(0).getProperty(Dictionary.storeLongitude).toString());
                } catch (JSONException ex) {
                    ex.printStackTrace();
                }

                Intent i = new Intent(AuctionDetailsActivity.this, StoreDetailsActivity.class);
                i.putExtra("store_details", item.toString());
                startActivity(i);
                finish();
                break;
            case (R.id.share_auction):
                Intent intent = new Intent(Intent.ACTION_SEND);
                intent.setType("text/plain");
                intent.putExtra(Intent.EXTRA_TEXT, shareText);
                Log.d("ShareTextAuction", shareText);
                startActivity(Intent.createChooser(intent, "Share with"));
                break;
        }
    }

    /**
     * Gets the similar auctions from the server based on the Deal Id and the Category Id
     */
    public void initializeSimilarAuctions() {

        String url = "/" + auctionDetails.get(0).getProperty(Dictionary.dealId).toString() + "/"
                + auctionDetails.get(0).getProperty(Dictionary.categoryId).toString() + "/" + 16;

        if (networkConnection.isInternetOn()) {
            Ion.with(this)
                    .load(Utility.HOST_VALUE + Utility.SIMILAR_AUCTIONS_PATH_VALUE + url)
                    .asJsonObject()
                    .setCallback(new FutureCallback<JsonObject>() {
                        @Override
                        public void onCompleted(Exception e, JsonObject result) {

                            if (e == null) {
                                JsonArray resultArray = result.get("similarauctionlist").getAsJsonArray();
                                JsonObject resultObject = resultArray.get(0).getAsJsonObject();
                                JsonObject result_inner = resultObject.get("similar_auction_list").getAsJsonObject();

                                int httpCode = result_inner.get("httpCode").getAsInt();

                                switch (httpCode) {
                                    case 200:

                                        similarAuctionsCardview.setVisibility(View.VISIBLE);
                                        mRecyclerView.setVisibility(View.VISIBLE);

                                        for (int i = 0; i < resultArray.size(); i++) {
                                            resultObject = resultArray.get(i).getAsJsonObject();

                                            result_inner = resultObject.get("similar_auction_list").getAsJsonObject();
                                            try {
                                                JSONObject jsonObject = new JSONObject(result_inner.toString());
                                                similarAuctionsList.add(new JSONParser(jsonObject));
                                            } catch (JSONException exception) {
                                            }
                                        }

                                        similarAuctionsAdapter = new SimilarAuctionsAdapter(AuctionDetailsActivity.this,
                                                similarAuctionsList);
                                        similarAuctionsAdapter.notifyDataSetChanged();
                                        mRecyclerView.setAdapter(similarAuctionsAdapter);
                                        break;
                                    case 401:
                                        Snackbar.make(rootView, result_inner.get("Message").getAsString(), Snackbar.LENGTH_LONG);
                                        break;
                                }
                            } else {
                                Snackbar.make(rootView, getString(R.string.unable_to_connect), Snackbar.LENGTH_LONG);
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
        getMenuInflater().inflate(R.menu.menu_auction_details, menu);

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
}

