package com.swifta.zenith.marketplace.Activities;

import android.annotation.TargetApi;
import android.app.AlertDialog;
import android.content.DialogInterface;
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

import com.getbase.floatingactionbutton.FloatingActionButton;
import com.getbase.floatingactionbutton.FloatingActionsMenu;
import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
import com.koushikdutta.async.future.FutureCallback;
import com.koushikdutta.ion.Ion;
import com.swifta.zenith.marketplace.Adapters.SimilarDealsAdapter;
import com.swifta.zenith.marketplace.R;
import com.swifta.zenith.marketplace.Utils.Dictionary;
import com.swifta.zenith.marketplace.Utils.JSONParser;
import com.swifta.zenith.marketplace.Utils.NetworkConnection;
import com.swifta.zenith.marketplace.Utils.Timer;
import com.swifta.zenith.marketplace.Utils.UnicodeConverter;
import com.swifta.zenith.marketplace.Utils.Utility;

import org.json.JSONException;
import org.json.JSONObject;

import java.text.ParseException;
import java.util.ArrayList;

public class DealsDetailsActivity extends BaseToolbarActivity {
    View rootView;
    View floatingButtonView;
    JSONParser deals;
    TextView dealsTryAgain;
    TextView noDeals;
    TextView detailName;
    TextView detailOff;
    TextView detailDescription;
    TextView detailNewCost;
    TextView terms;
    TextView timeLeft;
    TextView storeName;
    TextView storeCityName;
    TextView storeAddress;
    TextView cartTextView;
    TextView wishlistTextView;
    TextView compareTextView;
    Button shareDeal;
    ImageView detailsImage;
    ImageView storeImage;
    int imageArrayPosition = 0;
    String dealId;
    String dealKey;
    String deviceNumber;
    String dealName;
    String shareText;
    String storeUrlTitle;
    LinearLayout termsLayout;
    FloatingActionsMenu floatingActionsMenu;
    FloatingActionButton wishlistFloatingActionButton;
    FloatingActionButton compareFloatingActionButton;
    FloatingActionButton cartFloatingActionButton;
    RecyclerView mRecyclerView;
    SimilarDealsAdapter similarDealsAdapter;
    LinearLayoutManager linearLayoutManager;
    ProgressBar dealsProgress;
    NetworkConnection networkConnection;
    String[] imageArray;
    CardView similarDealsCardview;
    ArrayList<JSONParser> similarDealsList = new ArrayList<JSONParser>();
    ArrayList<JSONParser> dealsDetails = new ArrayList<JSONParser>();
    ArrayList<JSONParser> storeList = new ArrayList<JSONParser>();

    @TargetApi(Build.VERSION_CODES.LOLLIPOP)
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        rootView = getLayoutInflater().inflate(R.layout.activity_deals_details, mNestedScrollView);

        // Sets the status bar color
        Window window = getWindow();
        window.addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS);
        window.clearFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS);
        if (android.os.Build.VERSION.SDK_INT >= 21) {
            window.setStatusBarColor(ContextCompat.getColor(this, R.color.colorPrimaryDarker));
        }

        floatingButtonView = getLayoutInflater().inflate(R.layout.floating_action_button, mCoordinatorLayout);

        noDeals = (TextView) rootView.findViewById(R.id.no_stores);
        dealsTryAgain = (TextView) rootView.findViewById(R.id.deals_try_again);
        detailName = (TextView) rootView.findViewById(R.id.detail_name);
        detailOff = (TextView) rootView.findViewById(R.id.detail_off);
        detailDescription = (TextView) rootView.findViewById(R.id.detail_description);
        terms = (TextView) rootView.findViewById(R.id.terms);
        timeLeft = (TextView) rootView.findViewById(R.id.time_left);
        storeName = (TextView) rootView.findViewById(R.id.store_name);
        storeCityName = (TextView) rootView.findViewById(R.id.store_city);
        storeAddress = (TextView) rootView.findViewById(R.id.store_address);
        storeImage = (ImageView) rootView.findViewById(R.id.store_image);
        detailNewCost = (TextView) rootView.findViewById(R.id.detail_price);
        shareDeal = (Button) rootView.findViewById(R.id.share_deal);
        detailsImage = (ImageView) rootView.findViewById(R.id.details_image);
        dealsProgress = (ProgressBar) rootView.findViewById(R.id.deals_progress);
        termsLayout = (LinearLayout) rootView.findViewById(R.id.terms_layout);
        mRecyclerView = (RecyclerView) rootView.findViewById(R.id.similar_deals_recycler_view);
        similarDealsCardview = (CardView) rootView.findViewById(R.id.similar_deals_cardview);
        floatingActionsMenu = (FloatingActionsMenu) floatingButtonView.findViewById(R.id.floating_action_button);
        wishlistFloatingActionButton = (FloatingActionButton) floatingButtonView.findViewById(R.id.fab_wishlist);
        compareFloatingActionButton = (FloatingActionButton) floatingButtonView.findViewById(R.id.fab_compare);
        cartFloatingActionButton = (FloatingActionButton) floatingButtonView.findViewById(R.id.fab_cart);

        similarDealsCardview.setVisibility(View.GONE);

        // Gets the data from the previous activity
        Bundle bundle = getIntent().getExtras();

        if (bundle != null) {
            String data = bundle.getString("deals");
            try {
                deals = new JSONParser(new JSONObject(data));

                dealId = deals.getProperty("deal_id").toString();
                dealKey = deals.getProperty("deal_key").toString();

            } catch (JSONException e) {
                e.printStackTrace();
            }
        }

        setFloatingActionButtonProperties(compareFloatingActionButton, R.drawable.ic_compare, "Add to compare list");
        setFloatingActionButtonProperties(cartFloatingActionButton, R.drawable.ic_add_shopping_cart_white, "Add to cart");
        setFloatingActionButtonProperties(wishlistFloatingActionButton, R.drawable.ic_wishlist, "Add to wishlist");

        wishlistFloatingActionButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (!BaseNavigationDrawerActivity.SIGNED_IN) {
                    new AlertDialog.Builder(DealsDetailsActivity.this)
                            .setMessage(R.string.please_sign_in)
                            .setCancelable(true)
                            .setPositiveButton("Sign In", new DialogInterface.OnClickListener() {
                                @Override
                                public void onClick(DialogInterface dialogInterface, int i) {
                                    Intent intent = new Intent(DealsDetailsActivity.this, SignInActivity.class);
                                    startActivity(intent);
                                    finish();
                                }
                            })
                            .setNeutralButton("Cancel", new DialogInterface.OnClickListener() {
                                @Override
                                public void onClick(DialogInterface dialog, int which) {
                                    dialog.dismiss();
                                }
                            })
                            .show();
                } else {
                    HomeActivity.wishlistCount += 1;
                    wishlistTextView.setText(String.valueOf(HomeActivity.wishlistCount));

                    if (deals.getProperty(Dictionary.dealTitle).toString() == "Not found") {
                        Snackbar.make(rootView, dealName
                                + getString(R.string.added_to_wishlist), Snackbar.LENGTH_SHORT).show();
                    } else {
                        Snackbar.make(rootView, dealName
                                + getString(R.string.added_to_wishlist), Snackbar.LENGTH_SHORT).show();
                    }

                }

            }
        });

        compareFloatingActionButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (!BaseNavigationDrawerActivity.SIGNED_IN) {
                    new AlertDialog.Builder(DealsDetailsActivity.this)
                            .setMessage(R.string.please_sign_in)
                            .setCancelable(true)
                            .setPositiveButton("Sign In", new DialogInterface.OnClickListener() {
                                @Override
                                public void onClick(DialogInterface dialogInterface, int i) {
                                    Intent intent = new Intent(DealsDetailsActivity.this, SignInActivity.class);
                                    startActivity(intent);
                                    finish();
                                }
                            })
                            .setNeutralButton("Cancel", new DialogInterface.OnClickListener() {
                                @Override
                                public void onClick(DialogInterface dialog, int which) {
                                    dialog.dismiss();
                                }
                            })
                            .show();
                } else {
                    HomeActivity.compareCount += 1;
                    compareTextView.setText(String.valueOf(HomeActivity.compareCount));

                    if (deals.getProperty(Dictionary.dealTitle).toString().equals("Not found")) {
                        Snackbar.make(rootView, dealName
                                + getString(R.string.added_to_compare), Snackbar.LENGTH_SHORT).show();
                    } else {
                        Snackbar.make(rootView, dealName
                                + getString(R.string.added_to_compare), Snackbar.LENGTH_SHORT).show();
                    }
                }
            }
        });

        cartFloatingActionButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                if (!BaseNavigationDrawerActivity.SIGNED_IN) {
                    new AlertDialog.Builder(DealsDetailsActivity.this)
                            .setMessage(R.string.please_sign_in)
                            .setCancelable(true)
                            .setPositiveButton("Sign In", new DialogInterface.OnClickListener() {
                                @Override
                                public void onClick(DialogInterface dialogInterface, int i) {
                                    Intent intent = new Intent(DealsDetailsActivity.this, SignInActivity.class);
                                    startActivity(intent);
                                    finish();
                                }
                            })
                            .setNeutralButton("Cancel", new DialogInterface.OnClickListener() {
                                @Override
                                public void onClick(DialogInterface dialog, int which) {
                                    dialog.dismiss();
                                }
                            })
                            .show();
                } else {
                    HomeActivity.cartCount += 1;
                    cartTextView.setText(String.valueOf(HomeActivity.cartCount));

                    if (deals.getProperty(Dictionary.dealTitle).toString().equals("Not found")) {
                        Snackbar.make(rootView, dealName
                                + getString(R.string.added_to_cart), Snackbar.LENGTH_SHORT).show();
                    } else {
                        Snackbar.make(rootView, dealName
                                + getString(R.string.added_to_cart), Snackbar.LENGTH_SHORT).show();
                    }
                }
            }
        });

        // Loads the image into the imageview with Ion
        Ion.with(detailsImage)
                .placeholder(R.drawable.home_background)
                .load(deals.getProperty(Dictionary.imageUrl).toString());

        networkConnection = new NetworkConnection(this);

        linearLayoutManager = new LinearLayoutManager(this);
        linearLayoutManager.setOrientation(LinearLayoutManager.HORIZONTAL);
        mRecyclerView.setLayoutManager(linearLayoutManager);

        // Gets the device number of the specific Android device
        deviceNumber = Settings.Secure.getString(this.getContentResolver(), Settings.Secure.ANDROID_ID);

        // Get the deals details from the server
        initializeDealsDetails();
    }

    /**
     * Gets the deals details from the server based on the Deal Id and Deal Key
     */
    void initializeDealsDetails() {
        if (networkConnection.isInternetOn()) {
            Ion.with(this)
                    .load(Utility.HOST_VALUE + Utility.DEALS_DETAILS + dealId + "/" + dealKey + "/" + deviceNumber + ".html")
                    .asJsonObject()
                    .setCallback(new FutureCallback<JsonObject>() {
                        @Override
                        public void onCompleted(Exception e, JsonObject result) {

                            if (e == null) {
                                JsonArray resultArray = result.get("dealdetail").getAsJsonArray();
                                JsonObject resultObject = resultArray.get(0).getAsJsonObject();
                                JsonObject result_inner = resultObject.get("deal_details").getAsJsonObject();
                                int httpCode = result_inner.get("httpCode").getAsInt();

                                switch (httpCode) {
                                    case 200:
                                        shareDeal.setClickable(true);

                                        for (int i = 0; i < resultArray.size(); i++) {
                                            resultObject = resultArray.get(i).getAsJsonObject();
                                            result_inner = resultObject.get("deal_details").getAsJsonObject();

                                            try {
                                                JSONObject jsonObject = new JSONObject(result_inner.toString());
                                                dealsDetails.add(new JSONParser(jsonObject));

                                            } catch (JSONException exception) {

                                            }
                                        }
                                        imageArray = dealsDetails.get(0).getProperty(Dictionary.imageUrl).toString().split(",");
                                        dealName = dealsDetails.get(0).getProperty(Dictionary.dealTitle).toString();

                                        getSupportActionBar().setTitle(dealsDetails.get(0).getProperty(Dictionary.category).toString());
                                        detailName.setText(dealsDetails.get(0).getProperty(Dictionary.dealTitle).toString());
                                        detailOff.setText(dealsDetails.get(0).getProperty(Dictionary.dealDiscount).toString() + "%");
                                        // Displays HTML in the Textview
                                        detailDescription.setText(UnicodeConverter.getConversionResult(dealsDetails.get(0).getProperty(Dictionary.description).toString()));

                                        String termsText = dealsDetails.get(0).getProperty(Dictionary.termsConditions).toString();
                                        if (termsText.equals("") || termsText.equals(null)) {
                                            termsLayout.setVisibility(View.GONE);
                                        } else {
                                            terms.setText(Html.fromHtml(termsText));
                                        }

                                        detailNewCost.setText(dealsDetails.get(0).getProperty(Dictionary.currencySymbol).toString()
                                                + dealsDetails.get(0).getProperty("deal_value").toString());

                                        // Gets the date in strings and converts it to a usable format
                                        String endDate = dealsDetails.get(0).getProperty("enddate").toString();

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

                                        storeName.setText(dealsDetails.get(0).getProperty("store_name").toString());
                                        storeCityName.setText(dealsDetails.get(0).getProperty("store_city_name").toString());
                                        storeAddress.setText(dealsDetails.get(0).getProperty("store_address").toString());

                                        // Forms the {shop_url_title} by replacing the empty string items with a dash (-)
                                        storeUrlTitle = dealsDetails.get(0).getProperty("store_name").toString().replaceAll(" ", "-");

                                        shareText = Utility.HOST_VALUE + "/" + storeUrlTitle
                                                + "/deals/" + dealsDetails.get(0).getProperty("deal_key").toString()
                                                + "/"
                                                + dealsDetails.get(0).getProperty("deal_url_title").toString()
                                                + ".html";

                                        Ion.with(storeImage)
                                                .placeholder(R.drawable.home_background)
                                                .load(dealsDetails.get(0).getProperty("store_image_url").toString());

                                        initializeSimilarDeals();

                                        break;
                                    case 401:
                                        Snackbar.make(rootView, result_inner.get("Message").getAsString(), Snackbar.LENGTH_LONG);
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
                    item.put("store_image", dealsDetails.get(0).getProperty(Dictionary.productStoreImage).toString());
                    item.put("store_name", dealsDetails.get(0).getProperty(Dictionary.storeName).toString());
                    item.put("address1", dealsDetails.get(0).getProperty(Dictionary.storeAddress).toString());
                    item.put("city_name", dealsDetails.get(0).getProperty(Dictionary.storeCityName).toString());
                    item.put("phone_number", dealsDetails.get(0).getProperty(Dictionary.storePhoneNumber).toString());
                    item.put("website", dealsDetails.get(0).getProperty(Dictionary.storeWebsite).toString());
                    item.put("latitude", dealsDetails.get(0).getProperty(Dictionary.storeLatitude).toString());
                    item.put("longitude", dealsDetails.get(0).getProperty(Dictionary.storeLongitude).toString());
                } catch (JSONException ex) {
                    ex.printStackTrace();
                }

                Intent i = new Intent(DealsDetailsActivity.this, StoreDetailsActivity.class);
                i.putExtra("store_details", item.toString());
                startActivity(i);
                finish();
                break;
            case (R.id.share_deal):
                Intent intent = new Intent(Intent.ACTION_SEND);
                intent.setType("text/plain");
                intent.putExtra(Intent.EXTRA_TEXT, shareText);
                Log.d("ShareTextDeals", shareText);
                startActivity(Intent.createChooser(intent, "Share with"));
                break;
        }
    }

    /**
     * Gets the similar deals from the server based on the Deal Id and Deal Key
     */
    public void initializeSimilarDeals() {

        String url = dealsDetails.get(0).getProperty(Dictionary.dealId).toString() + "/"
                + dealsDetails.get(0).getProperty(Dictionary.categoryId).toString() + "/" + 13 + ".html";

        if (networkConnection.isInternetOn()) {
            Ion.with(this)
                    .load(Utility.HOST_VALUE + Utility.SIMILAR_DEALS_BY_DEAL_PATH_VALUE + url)
                    .asJsonObject()
                    .setCallback(new FutureCallback<JsonObject>() {
                        @Override
                        public void onCompleted(Exception e, JsonObject result) {

                            if (e == null) {
                                JsonArray resultArray = result.get("similardeallistdeals").getAsJsonArray();
                                JsonObject resultObject = resultArray.get(0).getAsJsonObject();
                                JsonObject result_inner = resultObject.get("similardealslist").getAsJsonObject();

                                try {
                                    String productId = result_inner.get("deal_id").getAsString();

                                    similarDealsCardview.setVisibility(View.VISIBLE);
                                    mRecyclerView.setVisibility(View.VISIBLE);

                                    for (int i = 0; i < resultArray.size(); i++) {
                                        resultObject = resultArray.get(i).getAsJsonObject();
                                        result_inner = resultObject.get("similardealslist").getAsJsonObject();

                                        try {
                                            JSONObject jsonObject = new JSONObject(result_inner.toString());
                                            similarDealsList.add(new JSONParser(jsonObject));
                                        } catch (JSONException exception) {
                                        }
                                    }

                                    similarDealsAdapter = new SimilarDealsAdapter(DealsDetailsActivity.this,
                                            similarDealsList);
                                    similarDealsAdapter.notifyDataSetChanged();
                                    mRecyclerView.setAdapter(similarDealsAdapter);
                                } catch (NullPointerException ex) {
                                    ex.printStackTrace();
                                }
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
        getMenuInflater().inflate(R.menu.menu_deals_details, menu);

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

    /**
     * Sets the properties of the FloatingActionButtons from the library used
     */
    private void setFloatingActionButtonProperties(FloatingActionButton fab, int drawable, String title) {
        fab.setSize(FloatingActionButton.SIZE_MINI);
        fab.setColorNormalResId(R.color.colorAccent);
        fab.setColorPressedResId(R.color.light_gray);
        fab.setIcon(drawable);
        fab.setTitle(title);
    }
}
