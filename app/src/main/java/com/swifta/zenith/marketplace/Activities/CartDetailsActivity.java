package com.swifta.zenith.marketplace.Activities;

import android.content.Intent;
import android.os.Bundle;
import android.support.v4.content.ContextCompat;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.LinearLayout;

import com.swifta.zenith.marketplace.Adapters.CartAdapter;
import com.swifta.zenith.marketplace.Database.CartDatabase;
import com.swifta.zenith.marketplace.Database.PriceDatabase;
import com.swifta.zenith.marketplace.R;
import com.swifta.zenith.marketplace.Utils.Dictionary;
import com.swifta.zenith.marketplace.Utils.JSONParser;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

public class CartDetailsActivity extends BaseToolbarActivity {
    View rootView;
    Button cartContinue;
    Button cartCheckout;
    CartAdapter cartAdapter;
    RecyclerView mRecyclerView;
    LinearLayout noItemsLayout;
    LinearLayout cartProcessLayout;
    private List<JSONParser> cartList = new ArrayList<JSONParser>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        rootView = getLayoutInflater().inflate(R.layout.activity_cart_details, mNestedScrollView);

        // Sets the status bar color
        Window window = getWindow();
        window.addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS);
        window.clearFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS);
        if (android.os.Build.VERSION.SDK_INT >= 21) {
            window.setStatusBarColor(ContextCompat.getColor(this, R.color.colorPrimaryDarker));
        }

        mToolbar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // Go back to the list of products if the activity is the product activity
                Class activityName = (Class<?>) getIntent().getExtras().get("activity_name");

                if (activityName == ProductDetailsActivity.class) {
                    Intent i = new Intent(CartDetailsActivity.this, AllProductsActivity.class);
                    startActivity(i);
                    finish();
                } else if (activityName == HomeActivity.class) {
                    Intent i = new Intent(CartDetailsActivity.this, (Class<?>) getIntent().getExtras().get("activity_name"));
                    startActivity(i);
                    finish();
                } else {
                    // Intent i = new Intent(CartDetailsActivity.this, (Class<?>) getIntent().getExtras().get("activity_name"));
                    // startActivity(i);
                    finish();
                }
            }
        });

        cartContinue = (Button) rootView.findViewById(R.id.cart_continue);
        cartCheckout = (Button) rootView.findViewById(R.id.cart_checkout);
        mRecyclerView = (RecyclerView) rootView.findViewById(R.id.cart_recycler_view);
        noItemsLayout = (LinearLayout) rootView.findViewById(R.id.no_items);
        cartProcessLayout = (LinearLayout) rootView.findViewById(R.id.cart_process);

        mRecyclerView.setLayoutManager(new LinearLayoutManager(this));

        cartAdapter = new CartAdapter(this, cartList, mToolbar, (Class<?>) getIntent().getExtras().get("activity_name"));

        displayCart(cartList);

        mRecyclerView.setAdapter(cartAdapter);
    }

    /**
     * Loads the products in the cart from the database
     **/
    public void displayCart(List<JSONParser> cartList) {
        long count = CartDatabase.count(CartDatabase.class, null, null);

        cartList.clear();

        if (count == 0) {
            noItemsLayout.setVisibility(View.VISIBLE);
            mRecyclerView.setVisibility(View.GONE);
            cartProcessLayout.setVisibility(View.GONE);
        } else {

            // Loads all the products in the cart into a list
            List<CartDatabase> cartData = CartDatabase.listAll(CartDatabase.class);
            Double currentPrice = 0.0;

            for (int i = 0; i < count; i++) {
                try {
                    String data = cartData.get(i).getData();
                    JSONObject parser = new JSONObject(data);

                    cartList.add(new JSONParser(parser));

                    // Finds the total price of all the products in the cart
                    if (cartList.get(i).getProperty(Dictionary.dealValue).toString().equals("Not found")) {
                        currentPrice += Double.valueOf(cartList.get(i).getProperty(Dictionary.productValue).toString());
                    } else {
                        currentPrice += Double.valueOf(cartList.get(i).getProperty(Dictionary.dealValue).toString());
                    }
                } catch (JSONException ex) {
                    ex.printStackTrace();
                }
            }

            // long currentPriceCount = PriceDatabase.count(PriceDatabase.class, null, null);

            // Clear all prices from the database
            PriceDatabase.deleteAll(PriceDatabase.class);

            // Save the new total price
            PriceDatabase priceDatabase = new PriceDatabase(currentPrice);
            priceDatabase.save();

            cartAdapter = new CartAdapter(this, cartList, mToolbar, (Class<?>) getIntent().getExtras().get("activity_name"));
            cartAdapter.notifyDataSetChanged();
        }
    }

    /**
     * Click listener for the cart layout items
     **/
    public void cartOnClick(View view) {
        switch (view.getId()) {

            case (R.id.no_items_button):
                Intent i = new Intent(CartDetailsActivity.this, AllProductsActivity.class);
                startActivity(i);
                break;

            case (R.id.cart_continue):
                Intent intent = new Intent(CartDetailsActivity.this, AllProductsActivity.class);
                startActivity(intent);
                break;

            case (R.id.cart_checkout):
                Intent in = new Intent(CartDetailsActivity.this, CheckoutActivity.class);
                startActivity(in);
                break;

        }
    }
}
