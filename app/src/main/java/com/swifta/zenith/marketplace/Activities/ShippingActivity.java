package com.swifta.zenith.marketplace.Activities;

import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v7.widget.CardView;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
import com.koushikdutta.async.future.FutureCallback;
import com.koushikdutta.ion.Ion;
import com.swifta.zenith.marketplace.Database.ShippingAddressDatabase;
import com.swifta.zenith.marketplace.R;
import com.swifta.zenith.marketplace.Utils.Dictionary;
import com.swifta.zenith.marketplace.Utils.NetworkConnection;
import com.swifta.zenith.marketplace.Utils.Session;
import com.swifta.zenith.marketplace.Utils.Utility;

import java.util.ArrayList;

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
    Spinner selectShippingCountry;
    Spinner selectShippingCity;
    CardView shippingCard;
    FloatingActionButton addFloatingActionButton;
    FloatingActionButton editFloatingActionButton;
    FloatingActionButton saveFloatingActionButton;
    ShippingAddressDatabase shippingAddressDatabase;
    private String selectedCityId;
    private String selectedCountryId;
    private ArrayList<String> countryName = new ArrayList<String>();
    private ArrayList<String> cityName = new ArrayList<String>();
    private ArrayList<String> countryIdArray = new ArrayList<>();
    private ArrayList<String> cityIdArray = new ArrayList<>();
    private ArrayAdapter<String> citySpinnerAdapter;
    private ArrayAdapter<String> countrySpinnerAdapter;
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
        selectShippingCountry = (Spinner) findViewById(R.id.select_shipping_country);
        selectShippingCity = (Spinner) findViewById(R.id.select_shipping_city);
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

                                // Hides the save floating action button
                                saveFloatingButtonView = getLayoutInflater().inflate(R.layout.save_shipping_fab, mCoordinatorLayout);
                                saveFloatingActionButton = (FloatingActionButton) saveFloatingButtonView.findViewById(R.id.save_floating_action_button);
                                saveFloatingActionButton.setVisibility(View.GONE);

                                // Displays the edit floating action button
                                editFloatingButtonView = getLayoutInflater().inflate(R.layout.edit_shipping_fab, mCoordinatorLayout);
                                editFloatingActionButton = (FloatingActionButton) editFloatingButtonView.findViewById(R.id.edit_floating_action_button);
                                editFloatingActionButton.setVisibility(View.VISIBLE);

                                if (resultElement != 200) {
                                    noShippingData.setVisibility(View.VISIBLE);
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
                                }

                                editFloatingActionButton.setOnClickListener(new View.OnClickListener() {
                                    @Override
                                    public void onClick(View v) {
                                        noShippingData.setVisibility(View.GONE);
                                        shippingCard.setVisibility(View.VISIBLE);

                                        enableAllEditTexts(shippingUser, shippingAddress1, shippingAddress2, shippingCountry,
                                                shippingState, shippingCity, mobileNumber, shippingZipcode);

                                        shippingCountry.setVisibility(View.GONE);
                                        shippingCity.setVisibility(View.GONE);
                                        selectShippingCountry.setVisibility(View.VISIBLE);
                                        selectShippingCity.setVisibility(View.VISIBLE);

                                        // Check the network connection and get the countries and cities
                                        if (networkConnection.isInternetOn()) {
                                            // Populates the country spinner with data from the server
                                            getCityList();
                                            createCountrySpinner(selectShippingCountry, countryName);
                                            createCitySpinner(selectShippingCity, cityName);

                                            selectShippingCity.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
                                                @Override
                                                public void onItemSelected(AdapterView<?> adapterView, View view, int i, long l) {
                                                    selectedCityId = cityIdArray.get(i);
                                                }

                                                @Override
                                                public void onNothingSelected(AdapterView<?> adapterView) {

                                                }
                                            });

                                            selectShippingCountry.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
                                                @Override
                                                public void onItemSelected(AdapterView<?> adapterView, View view, int i, long l) {
                                                    selectedCountryId = countryIdArray.get(i);
                                                }

                                                @Override
                                                public void onNothingSelected(AdapterView<?> adapterView) {
                                                }
                                            });
                                        } else {
                                            createSnackbar(selectShippingCountry, R.string.network_failure);
                                            networkConnection.displayAlert();
                                        }

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
                                        String country = selectShippingCountry.getSelectedItem().toString();
                                        String city = selectShippingCity.getSelectedItem().toString();

                                        shippingAddressDatabase = new ShippingAddressDatabase(user, address1, address2, state, selectedCountryId, selectedCityId, mobile, zipcode, country, city);
                                        shippingAddressDatabase.save();

                                        Toast.makeText(ShippingActivity.this, "Your changes have been saved successfully.", Toast.LENGTH_SHORT).show();

                                        selectShippingCountry.setVisibility(View.GONE);
                                        selectShippingCity.setVisibility(View.GONE);
                                        shippingCountry.setVisibility(View.VISIBLE);
                                        shippingCity.setVisibility(View.VISIBLE);

                                        disableAllEditTexts(shippingUser, shippingAddress1, shippingAddress2, shippingCountry,
                                                shippingState, shippingCity, mobileNumber, shippingZipcode);

                                        saveFloatingActionButton.setVisibility(View.GONE);

                                        editFloatingActionButton.setVisibility(View.VISIBLE);
                                    }
                                });
                            }
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

    /**
     * Gets the City lists from the server to display in a list with Ion
     */
    private void getCityList() {
        if (networkConnection.isInternetOn()) {
            Ion.with(this)
                    .load(Utility.HOST_VALUE + Utility.CITY_PATH_VALUE)
                    .asJsonObject()
                    .setCallback(new FutureCallback<JsonObject>() {
                        @Override
                        public void onCompleted(Exception e, JsonObject result) {
                            if (e == null) {
                                countryName.add(getString(R.string.country_name));
                                cityName.add(getString(R.string.city_name));
                                countryIdArray.add("country_id");
                                cityIdArray.add("city_id");

                                JsonArray resultArray = result.get("citylist").getAsJsonArray();

                                for (int i = 0; i < resultArray.size(); i++) {
                                    JsonObject resultObject = resultArray.get(i).getAsJsonObject();
                                    JsonObject result_inner = resultObject.get("city_list").getAsJsonObject();

                                    // Avoids duplicate values in the countryName List
                                    String countryNameValue = result_inner.get("country_name").getAsString();
                                    if (!countryName.contains(countryNameValue)) {
                                        countryName.add(countryNameValue);
                                        countryIdArray.add(result_inner.get("country_id").getAsString());
                                    }

                                    // Avoids duplicate values in the cityName List
                                    String cityNameValue = result_inner.get("city_name").getAsString();
                                    if (!cityName.contains(cityNameValue)) {
                                        cityName.add(result_inner.get("city_name").getAsString());
                                        cityIdArray.add(result_inner.get("city_id").getAsString());
                                    }
                                }
                                countrySpinnerAdapter.notifyDataSetChanged();
                                citySpinnerAdapter.notifyDataSetChanged();
                            }
                        }
                    });
        }
    }

    /**
     * Creates a spinner for the countries based on a given arraylist
     */
    public void createCountrySpinner(Spinner spinner, ArrayList<String> array) {
        countrySpinnerAdapter = new ArrayAdapter<String>(this,
                android.R.layout.simple_spinner_item, array
        );

        countrySpinnerAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinner.setAdapter(countrySpinnerAdapter);
        spinner.setSelection(0, true);
    }

    /**
     * Creates a spinner for the cities based on a given array
     */
    public void createCitySpinner(Spinner spinner, ArrayList<String> array) {
        citySpinnerAdapter = new ArrayAdapter<String>(this,
                android.R.layout.simple_spinner_item, array
        );

        citySpinnerAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinner.setAdapter(citySpinnerAdapter);
        spinner.setSelection(0, true);
    }

    /**
     * Creates a Snackbar with an OK text
     */
    public void createSnackbar(View view, int resID) {
        final Snackbar snackbar = Snackbar.make(view, resID, Snackbar.LENGTH_INDEFINITE);
        snackbar.setAction("Ok", new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                snackbar.dismiss();
            }
        })
                .show();
    }
}