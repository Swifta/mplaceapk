package com.swifta.zenith.marketplace.Activities;

import android.annotation.TargetApi;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.provider.Settings;
import android.support.design.widget.Snackbar;
import android.support.v4.content.ContextCompat;
import android.support.v4.view.MenuItemCompat;
import android.support.v7.widget.CardView;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
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
import com.swifta.zenith.marketplace.Adapters.SimilarProductsAdapter;
import com.swifta.zenith.marketplace.Database.CartDatabase;
import com.swifta.zenith.marketplace.R;
import com.swifta.zenith.marketplace.Utils.Dictionary;
import com.swifta.zenith.marketplace.Utils.JSONParser;
import com.swifta.zenith.marketplace.Utils.NetworkConnection;
import com.swifta.zenith.marketplace.Utils.ShippingType;
import com.swifta.zenith.marketplace.Utils.UnicodeConverter;
import com.swifta.zenith.marketplace.Utils.Utility;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

public class ProductDetailsActivity extends BaseToolbarActivity {
    static TextView cartTextView;
    static TextView wishlistTextView;
    static TextView compareTextView;
    View rootView;
    View floatingButtonView;
    JSONParser product;
    TextView productsTryAgain;
    TextView noProducts;
    TextView detailName;
    TextView detailOff;
    TextView detailDescription;
    TextView detailNewCost;
    TextView shippingType;
    TextView shippingAmount;
    TextView size;
    TextView storeName;
    TextView storeCityName;
    TextView storeAddress;
    Button shareProduct;
    Button viewStoreDetails;
    ImageView detailsImage;
    ImageView storeImage;
    String dealId;
    String dealKey;
    String deviceNumber;
    String productName;
    String shareText;
    String storeUrlTitle;
    FloatingActionsMenu floatingActionsMenu;
    FloatingActionButton wishlistFloatingActionButton;
    FloatingActionButton compareFloatingActionButton;
    FloatingActionButton cartFloatingActionButton;
    RecyclerView mRecyclerView;
    SimilarProductsAdapter similarProductsAdapter;
    LinearLayoutManager linearLayoutManager;
    ProgressBar productsProgress;
    NetworkConnection networkConnection;
    String[] imageArray;
    CardView similarProductCardview;
    LinearLayout shippingAmountLayout;
    ArrayList<JSONParser> similarProductList = new ArrayList<JSONParser>();
    ArrayList<JSONParser> productDetails = new ArrayList<JSONParser>();
    ArrayList<JSONParser> storeList = new ArrayList<JSONParser>();

    @TargetApi(Build.VERSION_CODES.LOLLIPOP)
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        rootView = getLayoutInflater().inflate(R.layout.activity_product_details, mNestedScrollView);

        // Sets the status bar color
        Window window = getWindow();
        window.addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS);
        window.clearFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS);
        if (android.os.Build.VERSION.SDK_INT >= 21) {
            window.setStatusBarColor(ContextCompat.getColor(this, R.color.colorPrimaryDarker));
        }

        floatingButtonView = getLayoutInflater().inflate(R.layout.floating_action_button, mCoordinatorLayout);

        noProducts = (TextView) rootView.findViewById(R.id.no_stores);
        productsTryAgain = (TextView) rootView.findViewById(R.id.products_try_again);
        detailName = (TextView) rootView.findViewById(R.id.detail_name);
        detailOff = (TextView) rootView.findViewById(R.id.detail_off);
        detailDescription = (TextView) rootView.findViewById(R.id.detail_description);
        shippingType = (TextView) rootView.findViewById(R.id.shipping_type);
        shippingAmount = (TextView) rootView.findViewById(R.id.shipping_amount);
        //size = (TextView) rootView.findViewById(R.id.size);
        storeName = (TextView) rootView.findViewById(R.id.store_name);
        storeCityName = (TextView) rootView.findViewById(R.id.store_city);
        storeAddress = (TextView) rootView.findViewById(R.id.store_address);
        storeImage = (ImageView) rootView.findViewById(R.id.store_image);
        detailNewCost = (TextView) rootView.findViewById(R.id.detail_price);
        shareProduct = (Button) rootView.findViewById(R.id.share_product);
        viewStoreDetails = (Button) rootView.findViewById(R.id.view_detail);
        detailsImage = (ImageView) rootView.findViewById(R.id.details_image);
        productsProgress = (ProgressBar) rootView.findViewById(R.id.product_progress);
        mRecyclerView = (RecyclerView) rootView.findViewById(R.id.similar_products_recycler_view);
        similarProductCardview = (CardView) rootView.findViewById(R.id.similar_product_cardview);
        shippingAmountLayout = (LinearLayout) rootView.findViewById(R.id.shipping_amount_layout);
        floatingActionsMenu = (FloatingActionsMenu) floatingButtonView.findViewById(R.id.floating_action_button);
        wishlistFloatingActionButton = (FloatingActionButton) floatingButtonView.findViewById(R.id.fab_wishlist);
        compareFloatingActionButton = (FloatingActionButton) floatingButtonView.findViewById(R.id.fab_compare);
        cartFloatingActionButton = (FloatingActionButton) floatingButtonView.findViewById(R.id.fab_cart);

        similarProductCardview.setVisibility(View.GONE);

        // Gets the data from the previous activity
        Bundle bundle = getIntent().getExtras();
        if (bundle != null) {
            String data = bundle.getString("product");
            try {
                product = new JSONParser(new JSONObject(data));

                // Checks if the bundle is from Similar products
                if (product.getProperty("product_id").toString().equals("Not found")) {
                    dealId = product.getProperty("deal_id").toString();
                    dealKey = product.getProperty("deal_key").toString();
                } else {
                    dealId = product.getProperty("product_id").toString();
                    dealKey = product.getProperty("product_key").toString();
                }

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
                    new AlertDialog.Builder(ProductDetailsActivity.this)
                            .setMessage(R.string.please_sign_in)
                            .setCancelable(true)
                            .setPositiveButton("Sign In", new DialogInterface.OnClickListener() {
                                @Override
                                public void onClick(DialogInterface dialogInterface, int i) {
                                    Intent intent = new Intent(ProductDetailsActivity.this, SignInActivity.class);
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

                    if (product.getProperty(Dictionary.dealTitle).toString().equals("Not found")) {
                        Snackbar.make(rootView, productName
                                + getString(R.string.added_to_wishlist), Snackbar.LENGTH_SHORT).show();
                    } else {
                        Snackbar.make(rootView, productName
                                + getString(R.string.added_to_wishlist), Snackbar.LENGTH_SHORT).show();
                    }
                }
            }
        });

        compareFloatingActionButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (!BaseNavigationDrawerActivity.SIGNED_IN) {
                    new AlertDialog.Builder(ProductDetailsActivity.this)
                            .setMessage(R.string.please_sign_in)
                            .setCancelable(true)
                            .setPositiveButton("Sign In", new DialogInterface.OnClickListener() {
                                @Override
                                public void onClick(DialogInterface dialogInterface, int i) {
                                    Intent intent = new Intent(ProductDetailsActivity.this, SignInActivity.class);
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

                    if (product.getProperty(Dictionary.dealTitle).toString().equals("Not found")) {
                        Snackbar.make(rootView, productName
                                + getString(R.string.added_to_compare), Snackbar.LENGTH_SHORT).show();
                    } else {
                        Snackbar.make(rootView, productName
                                + getString(R.string.added_to_compare), Snackbar.LENGTH_SHORT).show();
                    }
                }
            }
        });

        cartFloatingActionButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                if (!BaseNavigationDrawerActivity.SIGNED_IN) {
                    new AlertDialog.Builder(ProductDetailsActivity.this)
                            .setMessage(R.string.please_sign_in)
                            .setCancelable(true)
                            .setPositiveButton("Sign In", new DialogInterface.OnClickListener() {
                                @Override
                                public void onClick(DialogInterface dialogInterface, int i) {
                                    Intent intent = new Intent(ProductDetailsActivity.this, SignInActivity.class);
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

                    // Get the deal key of this product
                    String dealKey = product.getProperty(Dictionary.dealKey).toString();
                    long count = CartDatabase.count(CartDatabase.class, null, null);

                    if (count == 0) {
                        // If the cart is empty, add the product
                        HomeActivity.cartCount += 1;
                        HomeActivity.displayCartCount();
                        cartTextView.setText(String.valueOf(HomeActivity.cartCount));

                        CartDatabase mCartDatabase = new CartDatabase(productDetails.get(0).toString());
                        mCartDatabase.save();

                        Snackbar.make(rootView, productName
                                + getString(R.string.added_to_cart), Snackbar.LENGTH_SHORT).show();
                    } else {
                        if (containsDuplicateProduct(dealKey)) {
                            Snackbar.make(rootView, productName
                                    + " is already in your cart.", Snackbar.LENGTH_SHORT).show();
                        } else {
                            // Save the product to cart if there is no duplicate
                            HomeActivity.cartCount += 1;
                            HomeActivity.displayCartCount();
                            cartTextView.setText(String.valueOf(HomeActivity.cartCount));

                            CartDatabase mCartDatabase = new CartDatabase(productDetails.get(0).toString());
                            mCartDatabase.save();

                            Snackbar.make(rootView, productName
                                    + getString(R.string.added_to_cart), Snackbar.LENGTH_SHORT).show();

//                                if (product.getProperty(Dictionary.dealTitle).toString().equals("Not found")) {
//                                    Snackbar.make(rootView, productName
//                                            + getString(R.string.added_to_cart), Snackbar.LENGTH_SHORT).show();
//                                } else {
//                                    Snackbar.make(rootView, productName
//                                            + getString(R.string.added_to_cart), Snackbar.LENGTH_SHORT).show();
//                                }
                        }
                    }
                }
            }
        });

        // Loads the image into the imageview with Ion
        Ion.with(detailsImage)
                .placeholder(R.drawable.home_background)
                .load(product.getProperty(Dictionary.imageUrl).toString());

        networkConnection = new NetworkConnection(this);

        linearLayoutManager = new LinearLayoutManager(this);
        linearLayoutManager.setOrientation(LinearLayoutManager.HORIZONTAL);
        mRecyclerView.setLayoutManager(linearLayoutManager);

        // Gets the device number of the specific Android device
        deviceNumber = Settings.Secure.getString(this.getContentResolver(), Settings.Secure.ANDROID_ID);

        // Get the product details from the server
        initializeProductDetails();
    }

    /**
     * Gets the product details from the server based on the Deal Id and Deal Key
     */
    void initializeProductDetails() {
        if (networkConnection.isInternetOn()) {
            Ion.with(this)
                    .load(Utility.HOST_VALUE + Utility.PRODUCTS_DETAILS + dealId + "/" + dealKey + "/" + deviceNumber + ".html")
                    .asJsonObject()
                    .setCallback(new FutureCallback<JsonObject>() {
                        @Override
                        public void onCompleted(Exception e, JsonObject result) {

                            if (e == null) {
                                JsonArray resultArray = result.get("productdetail").getAsJsonArray();
                                JsonObject resultObject = resultArray.get(0).getAsJsonObject();
                                JsonObject result_inner = resultObject.get("product_details").getAsJsonObject();
                                int httpCode = result_inner.get("httpCode").getAsInt();

                                switch (httpCode) {
                                    case 200:
                                        // Disables the Share and View Details Buttons until data is loaded into the activity
                                        shareProduct.setClickable(true);
                                        viewStoreDetails.setClickable(true);

                                        for (int i = 0; i < resultArray.size(); i++) {
                                            resultObject = resultArray.get(i).getAsJsonObject();
                                            result_inner = resultObject.get("product_details").getAsJsonObject();

                                            try {
                                                JSONObject jsonObject = new JSONObject(result_inner.toString());
                                                productDetails.add(new JSONParser(jsonObject));

                                            } catch (JSONException exception) {

                                            }
                                        }

                                        imageArray = productDetails.get(0).getProperty(Dictionary.imageUrl).toString().split(",");
                                        productName = productDetails.get(0).getProperty(Dictionary.dealTitle).toString();

                                        getSupportActionBar().setTitle(productDetails.get(0).getProperty(Dictionary.category).toString());
                                        detailName.setText(productDetails.get(0).getProperty(Dictionary.dealTitle).toString());
                                        detailOff.setText(productDetails.get(0).getProperty(Dictionary.dealDiscount).toString() + "%");
                                        // Displays HTML in the Textview
                                        detailDescription.setText(UnicodeConverter.getConversionResult(productDetails.get(0).getProperty(Dictionary.description).toString()));

                                        // Gets the Shipping code and sets the corresponding meaning
                                        String shippingTypeValue = productDetails.get(0).getProperty(Dictionary.shippingMethod).toString();
                                        shippingType.setText(ShippingType.getValue(shippingTypeValue, getApplicationContext()));

                                        // Hides the shipping amount if shipping is free
                                        if (ShippingType.getValue(shippingTypeValue, getApplicationContext()).equals(getResources().getString(R.string.free_ship))) {
                                            shippingAmountLayout.setVisibility(View.GONE);
                                        } else {
                                            shippingAmount.setText(UnicodeConverter.getConversionResult(productDetails.get(0).getProperty(Dictionary.currencySymbol).toString())
                                                    + productDetails.get(0).getProperty(Dictionary.shippingAmount).toString());
                                        }

                                        detailNewCost.setText(UnicodeConverter.getConversionResult(productDetails.get(0).getProperty(Dictionary.currencySymbol).toString())
                                                + productDetails.get(0).getProperty(Dictionary.dealValue).toString());
                                        //category.setText(productDetails.get(0).getProperty(Dictionary.category).toString());
                                        //size.setText(productDetails.get(0).getProperty(Dictionary.sizeCount).toString());
                                        storeName.setText(productDetails.get(0).getProperty("store_name").toString());
                                        storeCityName.setText(productDetails.get(0).getProperty("store_city_name").toString());
                                        storeAddress.setText(productDetails.get(0).getProperty("store_address").toString());

                                        // Forms the {shop_url_title} by replacing the empty string items with a dash (-)
                                        storeUrlTitle = productDetails.get(0).getProperty("store_name").toString().replaceAll(" ", "-");

                                        shareText = Utility.HOST_VALUE + "/" + storeUrlTitle
                                                + "/product/" + productDetails.get(0).getProperty("deal_key").toString()
                                                + "/"
                                                + productDetails.get(0).getProperty("deal_url_title").toString()
                                                + ".html";

                                        Ion.with(storeImage)
                                                .placeholder(R.drawable.home_background)
                                                .load(productDetails.get(0).getProperty("store_image_url").toString());

                                        initializeSimilarProducts();

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

    public void onClick(View view) {
        switch (view.getId()) {
            case (R.id.view_detail):
                JSONObject item = new JSONObject();
                try {
                    item.put("store_image", productDetails.get(0).getProperty(Dictionary.productStoreImage).toString());
                    item.put("store_name", productDetails.get(0).getProperty(Dictionary.storeName).toString());
                    item.put("address1", productDetails.get(0).getProperty(Dictionary.storeAddress).toString());
                    item.put("city_name", productDetails.get(0).getProperty(Dictionary.storeCityName).toString());
                    item.put("phone_number", productDetails.get(0).getProperty(Dictionary.storePhoneNumber).toString());
                    item.put("website", productDetails.get(0).getProperty(Dictionary.storeWebsite).toString());
                    item.put("latitude", productDetails.get(0).getProperty(Dictionary.storeLatitude).toString());
                    item.put("longitude", productDetails.get(0).getProperty(Dictionary.storeLongitude).toString());
                } catch (JSONException ex) {

                }

                Intent i = new Intent(ProductDetailsActivity.this, StoreDetailsActivity.class);
                i.putExtra("store_details", item.toString());
                startActivity(i);
                finish();
                break;
            case (R.id.share_product):
                Intent intent = new Intent(Intent.ACTION_SEND);
                intent.setType("text/plain");
                intent.putExtra(Intent.EXTRA_TEXT, shareText);
                startActivity(Intent.createChooser(intent, "Share with"));
                break;
        }
    }

    /**
     * Gets the similar products from the server based on the Deal Id and Deal Key
     */
    public void initializeSimilarProducts() {

        String url = productDetails.get(0).getProperty(Dictionary.dealId).toString() + "/"
                + productDetails.get(0).getProperty(Dictionary.categoryId).toString();

        if (networkConnection.isInternetOn()) {
            Ion.with(this)
                    .load(Utility.HOST_VALUE + Utility.SIMILAR_PRODUCT_BY_PRODUCTS_PATH_VALUE + url)
                    .asJsonObject()
                    .setCallback(new FutureCallback<JsonObject>() {
                        @Override
                        public void onCompleted(Exception e, JsonObject result) {
                            if (e == null) {
                                JsonArray resultArray = result.get("similarproductlistproducts").getAsJsonArray();
                                JsonObject resultObject = resultArray.get(0).getAsJsonObject();
                                JsonObject result_inner = resultObject.get("similarproductlist").getAsJsonObject();

                                try {
                                    String productId = result_inner.get("product_id").getAsString();

                                    similarProductCardview.setVisibility(View.VISIBLE);
                                    mRecyclerView.setVisibility(View.VISIBLE);

                                    for (int i = 0; i < resultArray.size(); i++) {
                                        resultObject = resultArray.get(i).getAsJsonObject();
                                        result_inner = resultObject.get("similarproductlist").getAsJsonObject();

                                        try {
                                            JSONObject jsonObject = new JSONObject(result_inner.toString());
                                            similarProductList.add(new JSONParser(jsonObject));
                                        } catch (JSONException exception) {
                                            exception.printStackTrace();
                                        }
                                    }

                                    similarProductsAdapter = new SimilarProductsAdapter(ProductDetailsActivity.this,
                                            similarProductList);
                                    similarProductsAdapter.notifyDataSetChanged();
                                    mRecyclerView.setAdapter(similarProductsAdapter);

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
        getMenuInflater().inflate(R.menu.menu_product_details, menu);

        // Sets up the cart count menu item
        View cartBadgeLayout = MenuItemCompat.getActionView(menu.findItem(R.id.cart_badge));
        cartTextView = (TextView) cartBadgeLayout.findViewById(R.id.cart_count_text);
        cartTextView.setText(String.valueOf(HomeActivity.cartCount));

        cartBadgeLayout.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {
                Intent i = new Intent(ProductDetailsActivity.this, CartDetailsActivity.class);
                i.putExtra("activity_name", ProductDetailsActivity.class);
                startActivity(i);
                finish();
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
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent ac;//tivity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        return true;
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

    /**
     * Compare it to the items already present in the Cart to check if it's a duplicate item
     **/
    private boolean containsDuplicateProduct(String dealKey) {
        boolean duplicateProduct = false;
        String cartDealKey;
        // Get the list of all items in the Cart Database
        List<CartDatabase> allProducts = CartDatabase.listAll(CartDatabase.class);
        List<JSONParser> cartList = new ArrayList<JSONParser>();

        // Search through the List
        for (int i = 0; i < allProducts.size(); i++) {
            try {
                String data = allProducts.get(i).getData();
                JSONObject parser = new JSONObject(data);

                cartList.add(new JSONParser(parser));

                // Finds the deal_key of all the products in the cart and compares it to the new one
                cartDealKey = String.valueOf(cartList.get(i).getProperty(Dictionary.dealKey).toString());

                if (dealKey.equals(cartDealKey)) {
                    duplicateProduct = true;
                    break;
                }
            } catch (JSONException ex) {
                ex.printStackTrace();
            }
        }
        return duplicateProduct;
    }
}
