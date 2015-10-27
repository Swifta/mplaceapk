package com.swifta.zenith.marketplace.Activities;

import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.v7.widget.CardView;
import android.view.MenuItem;
import android.view.View;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.google.gson.JsonObject;
import com.koushikdutta.async.future.FutureCallback;
import com.koushikdutta.ion.Ion;
import com.swifta.zenith.marketplace.Database.ShippingAddressDatabase;
import com.swifta.zenith.marketplace.R;
import com.swifta.zenith.marketplace.Utils.Dictionary;
import com.swifta.zenith.marketplace.Utils.NetworkConnection;
import com.swifta.zenith.marketplace.Utils.Session;
import com.swifta.zenith.marketplace.Utils.Utility;

public class ShippingActivity extends BaseNavigationDrawerActivity {
    private static final String LANG = "en";
    View homeView;
    View editFloatingButtonView;
    View saveFloatingButtonView;
    View addFloatingButtonView;
    MenuItem hMenuItem;
    ProgressBar mProgressBar;
    TextView noShippingData;
    EditText shippingUser;
    EditText shippingAddress1;
    EditText shippingAddress2;
    EditText shippingCountry;
    EditText shippingState;
    EditText shippingCity;
    EditText mobileNumber;
    EditText shippingZipcode;
    CardView shippingCard;
    FloatingActionButton addFloatingActionButton;
    FloatingActionButton editFloatingActionButton;
    FloatingActionButton saveFloatingActionButton;
    ShippingAddressDatabase shippingAddressDatabase;
    private NetworkConnection networkConnection;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        // Inflates ShippingActivity's layout into the parent's layout
        homeView = getLayoutInflater().inflate(R.layout.activity_shipping, mNestedScrollView);

        // Selects ShippingActivity in the parent DrawerLayout and checks it
        hMenuItem = mNavigationView.getMenu().findItem(R.id.main_navigation_item_28);
        if (hMenuItem.isChecked()) {
            hMenuItem.setChecked(false);
        } else {
            hMenuItem.setChecked(true);
        }

        mProgressBar = (ProgressBar) findViewById(R.id.shipping_progress);
        noShippingData = (TextView) findViewById(R.id.no_shipping_details);
        shippingUser = (EditText) findViewById(R.id.shipping_user);
        shippingAddress1 = (EditText) findViewById(R.id.shipping_address_1);
        shippingAddress2 = (EditText) findViewById(R.id.shipping_address_2);
        shippingCountry = (EditText) findViewById(R.id.shipping_country);
        shippingState = (EditText) findViewById(R.id.shipping_state);
        shippingCity = (EditText) findViewById(R.id.shipping_city);
        mobileNumber = (EditText) findViewById(R.id.shipping_mobile);
        shippingZipcode = (EditText) findViewById(R.id.shipping_zipcode);
        shippingCard = (CardView) findViewById(R.id.shipping_card);
        networkConnection = new NetworkConnection(this);

        initializeShippingDetails();
    }

    /**
     * Loads all the shipping details from the server with Ion
     */
    private void initializeShippingDetails() {
        if (networkConnection.isInternetOn()) {
            Ion.with(this)
                    .load(Utility.HOST_VALUE + Utility.SHIPPING_PATH_VALUE + LANG + "/" + Session.getUserId(ShippingActivity.this) + ".html")
                    .asJsonObject()
                    .setCallback(new FutureCallback<JsonObject>() {
                        @Override
                        public void onCompleted(Exception e, JsonObject result) {
                            mProgressBar.setVisibility(View.GONE);
                            if (e == null) {

                                final String countryId;
                                final String cityId;

                                JsonObject resultObject = result.get("response").getAsJsonObject();
                                int resultElement = resultObject.get("httpCode").getAsInt();
                                if (resultElement != 200) {
                                    noShippingData.setVisibility(View.VISIBLE);
                                    addFloatingButtonView = getLayoutInflater().inflate(R.layout.add_shipping_fab, mCoordinatorLayout);
                                    addFloatingActionButton = (android.support.design.widget.FloatingActionButton) addFloatingButtonView.findViewById(R.id.add_floating_action_button);

                                    noShippingData.setText("You don't have a shipping address yet.");
                                } else {
                                    shippingCard.setVisibility(View.VISIBLE);

                                    // Transfers all the data from the JsonObject into Strings
                                    String user = resultObject.get(Dictionary.shippingUser).getAsString();
                                    String address1 = resultObject.get(Dictionary.shippingAddress1).getAsString();
                                    String address2 = resultObject.get(Dictionary.shippingAddress2).getAsString();
                                    String country = resultObject.get(Dictionary.shippingCountryName).getAsString();
                                    String state = resultObject.get(Dictionary.shippingState).getAsString();
                                    String city = resultObject.get(Dictionary.shippingCityName).getAsString();
                                    String mobile = resultObject.get(Dictionary.shippingMobileno).getAsString();
                                    String zipcode = resultObject.get(Dictionary.shippingZipcode).getAsString();
                                    cityId = resultObject.get(Dictionary.shippingCityId).getAsString();
                                    countryId = resultObject.get(Dictionary.shippingCountryId).getAsString();

                                    // Sets the edittexts with their respective values
                                    shippingUser.setText(user);
                                    shippingAddress1.setText(address1);
                                    shippingAddress2.setText(address2);
                                    shippingCountry.setText(country);
                                    shippingState.setText(state);
                                    shippingCity.setText(city);
                                    mobileNumber.setText(mobile);
                                    shippingZipcode.setText(zipcode);

                                    // Stores new data into the Shipping database
                                    shippingAddressDatabase = new ShippingAddressDatabase(user, address1, address2, state, countryId, cityId, mobile, zipcode, country, city);
                                    shippingAddressDatabase.save();

                                    // Displays the edit floating action button
                                    editFloatingButtonView = getLayoutInflater().inflate(R.layout.edit_shipping_fab, mCoordinatorLayout);
                                    editFloatingActionButton = (FloatingActionButton) editFloatingButtonView.findViewById(R.id.edit_floating_action_button);

                                    // Hides the save floating action button
                                    saveFloatingButtonView = getLayoutInflater().inflate(R.layout.save_shipping_fab, mCoordinatorLayout);
                                    saveFloatingActionButton = (FloatingActionButton) saveFloatingButtonView.findViewById(R.id.save_floating_action_button);
                                    saveFloatingActionButton.setVisibility(View.GONE);

                                    // Sets a click listener for the edit floating action button
                                    editFloatingActionButton.setOnClickListener(new View.OnClickListener() {
                                        @Override
                                        public void onClick(View v) {

                                            enableAllEditTexts(shippingUser, shippingAddress1, shippingAddress2, shippingCountry,
                                                    shippingState, shippingCity, mobileNumber, shippingZipcode);

                                            Toast.makeText(ShippingActivity.this, "Update any field by touching it.", Toast.LENGTH_SHORT).show();

                                            editFloatingActionButton.setVisibility(View.GONE);

                                            saveFloatingActionButton.setVisibility(View.VISIBLE);
                                        }
                                    });

                                    // Sets a listener for the save floating action button
                                    saveFloatingActionButton.setOnClickListener(new View.OnClickListener() {
                                        @Override
                                        public void onClick(View v) {

                                            ShippingAddressDatabase.deleteAll(ShippingAddressDatabase.class);

                                            String user = shippingUser.getText().toString();
                                            String address1 = shippingAddress1.getText().toString();
                                            String address2 = shippingAddress2.getText().toString();
                                            String state = shippingState.getText().toString();
                                            String mobile = mobileNumber.getText().toString();
                                            String zipcode = shippingZipcode.getText().toString();
                                            String country = shippingCountry.getText().toString();
                                            String city = shippingCity.getText().toString();

                                            shippingAddressDatabase = new ShippingAddressDatabase(user, address1, address2, state, countryId, cityId, mobile, zipcode, country, city);
                                            shippingAddressDatabase.save();

                                            Toast.makeText(ShippingActivity.this, "Your changes have been saved successfully.", Toast.LENGTH_SHORT).show();

                                            disableAllEditTexts(shippingUser, shippingAddress1, shippingAddress2, shippingCountry,
                                                    shippingState, shippingCity, mobileNumber, shippingZipcode);

                                            saveFloatingActionButton.setVisibility(View.GONE);

                                            editFloatingActionButton.setVisibility(View.VISIBLE);
                                        }
                                    });
                                }
                            }
//                            } else {
//                                noProducts.setText(getString(R.string.unable_to_connect));
//                                noProducts.setVisibility(View.VISIBLE);
//                            }
                        }
                    });
        } else {
            networkConnection.displayAlert();
        }
    }

    /**
     * Enables interaction with the edittext
     */
    private void enableEditText(EditText editText) {
        editText.setClickable(true);
        editText.setCursorVisible(true);
        editText.setFocusable(true);
        editText.setFocusableInTouchMode(true);
    }

    /**
     * Enables all the edittexts in the Shipping Activity
     */
    private void enableAllEditTexts(EditText shippingUser, EditText shippingAddress1, EditText shippingAddress2, EditText shippingCountry,
                                    EditText shippingState, EditText shippingCity, EditText mobileNumber, EditText shippingZipcode) {
        enableEditText(shippingUser);
        enableEditText(shippingAddress1);
        enableEditText(shippingAddress2);
        enableEditText(shippingCountry);
        enableEditText(shippingState);
        enableEditText(shippingCity);
        enableEditText(mobileNumber);
        enableEditText(shippingZipcode);
    }

    /**
     * Disables interaction with the Edittexts
     */
    private void disableEditText(EditText editText) {
        editText.setClickable(false);
        editText.setCursorVisible(false);
        editText.setFocusable(false);
        editText.setFocusableInTouchMode(false);
    }

    /**
     * Disables all the edittexts in the Shipping Activity
     */
    private void disableAllEditTexts(EditText shippingUser, EditText shippingAddress1, EditText shippingAddress2, EditText shippingCountry,
                                     EditText shippingState, EditText shippingCity, EditText mobileNumber, EditText shippingZipcode) {
        disableEditText(shippingUser);
        disableEditText(shippingAddress1);
        disableEditText(shippingAddress2);
        disableEditText(shippingCountry);
        disableEditText(shippingState);
        disableEditText(shippingCity);
        disableEditText(mobileNumber);
        disableEditText(shippingZipcode);
    }
}
