package com.swifta.zenith.marketplace.Activities;

import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.support.design.widget.Snackbar;
import android.support.design.widget.TextInputLayout;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.Spinner;

import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
import com.koushikdutta.async.future.FutureCallback;
import com.koushikdutta.ion.Ion;
import com.swifta.zenith.marketplace.R;
import com.swifta.zenith.marketplace.Utils.NetworkConnection;
import com.swifta.zenith.marketplace.Utils.Utility;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class RegisterActivity extends BaseToolbarActivity {

    View registerView;
    private LinearLayout register_one;
    private LinearLayout register_two;
    private Spinner register_gender;
    private Spinner register_age_range;
    private Spinner register_city;
    private Spinner register_country;
    private TextInputLayout usernameTextInputLayout;
    private TextInputLayout emailTextInputLayout;
    private TextInputLayout passwordTextInputLayout;
    private TextInputLayout confirmPasswordTextInputLayout;
    private TextInputLayout uniqueIdentifierTextInputLayout;
    private TextInputLayout firstnameTextInputLayout;
    private TextInputLayout lastnameTextInputLayout;
    private TextInputLayout mobilenumberTextInputLayout;
    private EditText register_unique_identifier;
    private EditText register_confirm_password;
    private EditText register_password;
    private EditText register_username;
    private EditText register_email;
    private EditText register_firstname;
    private EditText register_lastname;
    private EditText register_mobile_number;
    private CheckBox register_agree;
    private ArrayList<String> cityName = new ArrayList<String>();
    private ArrayList<String> countryName = new ArrayList<String>();
    private ArrayList<String> cityId = new ArrayList<>();
    private ArrayList<String> countryId = new ArrayList<>();
    private ArrayAdapter<String> countrySpinnerAdapter;
    private ArrayAdapter<String> citySpinnerAdapter;
    private NetworkConnection networkConnection;
    private ProgressDialog progressDialog;
    private String selectedCityId;
    private static final String URL = "en";
    private SharedPreferences preferences;
    private static SharedPreferences.Editor editor;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        // Inflates RegisterActivity's layout into the parent's layout
        registerView = getLayoutInflater().inflate(R.layout.activity_register, mNestedScrollView);

        register_one = (LinearLayout) registerView.findViewById(R.id.register_one);
        register_two = (LinearLayout) registerView.findViewById(R.id.register_two);
        usernameTextInputLayout = (TextInputLayout) registerView.findViewById(R.id.username_textinputlayout);
        emailTextInputLayout = (TextInputLayout) registerView.findViewById(R.id.email_textinputlayout);
        passwordTextInputLayout = (TextInputLayout) registerView.findViewById(R.id.password_textinputlayout);
        confirmPasswordTextInputLayout = (TextInputLayout) registerView.findViewById(R.id.confirm_password_textinputlayout);
        uniqueIdentifierTextInputLayout = (TextInputLayout) registerView.findViewById(R.id.unique_identifier_textinputlayout);
        firstnameTextInputLayout = (TextInputLayout) registerView.findViewById(R.id.firstname_textinputlayout);
        lastnameTextInputLayout = (TextInputLayout) registerView.findViewById(R.id.lastname_textinputlayout);
        mobilenumberTextInputLayout = (TextInputLayout) registerView.findViewById(R.id.mobile_textinputlayout);
        register_username = (EditText) registerView.findViewById(R.id.register_username);
        register_email = (EditText) registerView.findViewById(R.id.register_email);
        register_confirm_password = (EditText) registerView.findViewById(R.id.register_confirm_password);
        register_password = (EditText) registerView.findViewById(R.id.register_password);
        register_unique_identifier = (EditText) registerView.findViewById(R.id.register_unique_identifier);
        register_firstname = (EditText) registerView.findViewById(R.id.register_first_name);
        register_lastname = (EditText) registerView.findViewById(R.id.register_last_name);
        register_firstname = (EditText) registerView.findViewById(R.id.register_first_name);
        register_mobile_number = (EditText) registerView.findViewById(R.id.register_mobile_number);
        register_gender = (Spinner) registerView.findViewById(R.id.register_gender);
        register_age_range = (Spinner) registerView.findViewById(R.id.register_age_range);
        register_country = (Spinner) registerView.findViewById(R.id.register_country);
        register_city = (Spinner) registerView.findViewById(R.id.register_city);
        register_agree = (CheckBox) registerView.findViewById(R.id.register_agree);
        networkConnection = new NetworkConnection(RegisterActivity.this);
        preferences = getSharedPreferences("Preferences", Context.MODE_PRIVATE);
        editor = preferences.edit();

        // Sets the Back button on the Toolbar to HomeActivity
        mToolbar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent i = new Intent(RegisterActivity.this, HomeActivity.class);
                startActivity(i);
                finish();
            }
        });

        createSpinner(register_gender, R.array.gender_array);
        createSpinner(register_age_range, R.array.age_range);

        // Check the network connection and get the countries and cities
        if (networkConnection.isInternetOn()) {
            // Populates the country spinner with data from the server
            getCountryList();
            createCountrySpinner(this.register_country, countryName);
            createCitySpinner(this.register_city, cityName);

            register_city.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
                @Override
                public void onItemSelected(AdapterView<?> adapterView, View view, int i, long l) {
                    selectedCityId = cityId.get(i);
                }

                @Override
                public void onNothingSelected(AdapterView<?> adapterView) {

                }
            });
        } else {
            createSnackbar(registerView, R.string.network_failure);
            networkConnection.displayAlert();
        }
    }

    /**
     * Handles the clicks for the necessary views
     */
    public void registerOnClick(View view) {
        switch (view.getId()) {
            case (R.id.register_next):
                if (signinValidationOne()) {
                    register_one.setVisibility(View.GONE);
                    register_two.setVisibility(View.VISIBLE);
                }
                break;
            case (R.id.register_sign_in_here):
                Intent i = new Intent(RegisterActivity.this, SignInActivity.class);
                startActivity(i);
                finish();
                break;
            case (R.id.register_terms):
                AlertDialog.Builder builder = new AlertDialog.Builder(RegisterActivity.this);
                builder.setMessage(R.string.register_terms_message)
                        .setTitle(R.string.register_terms)
                        .setCancelable(true)
                        .setPositiveButton(R.string.ok, new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                dialog.cancel();
                            }
                        });
                AlertDialog alertDialog = builder.create();
                alertDialog.show();
                break;
            case (R.id.register_back):
                register_one.setVisibility(View.VISIBLE);
                register_two.setVisibility(View.GONE);
                break;
            case (R.id.register_submit):
                submitData();
                break;
        }


    }

    /**
     * Creates a spinner for the countries based on a given array
     */
    public void createSpinner(Spinner spinner, int array) {
        ArrayAdapter<CharSequence> adapter = ArrayAdapter.createFromResource(
                this, array, android.R.layout.simple_spinner_item);

        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinner.setAdapter(adapter);
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
     * Validates the first set of sign in fields
     */
    public boolean signinValidationOne() {
        if (register_username.getText().length() == 0) {
            usernameTextInputLayout.setError(getString(R.string.error_username));
            return false;
        } else {
            unsetLayoutError(usernameTextInputLayout);
        }
        if (register_email.getText().length() == 0) {
            emailTextInputLayout.setError(getString(R.string.error_email));
            return false;
        }
        if (!validEmail(register_email.getText().toString())) {
            emailTextInputLayout.setError(getString(R.string.error_valid_email));
            return false;
        } else {
            unsetLayoutError(emailTextInputLayout);
        }
        if (register_password.getText().length() == 0) {
            passwordTextInputLayout.setError(getString(R.string.error_password));
            return false;
        }
        if (register_password.getText().length() < 6) {
            passwordTextInputLayout.setError(getString(R.string.error_short_password));
            return false;
        } else {
            unsetLayoutError(passwordTextInputLayout);
        }
        if (register_confirm_password.getText().length() == 0) {
            confirmPasswordTextInputLayout.setError(getString(R.string.error_confirm_password));
            return false;
        }
        if (!register_confirm_password.getText().toString().equals(register_password.getText().toString())) {
            confirmPasswordTextInputLayout.setError(getString(R.string.error_compare_password));
            return false;
        } else {
            unsetLayoutError(confirmPasswordTextInputLayout);
        }
        if (register_unique_identifier.getText().length() == 0) {
            uniqueIdentifierTextInputLayout.setError(getString(R.string.error_unique_identifier));
            return false;
        } else {
            unsetLayoutError(uniqueIdentifierTextInputLayout);
        }
        return true;
    }

    /**
     * Validates the second set of sign in fields
     */
    public boolean signinValidationTwo() {
        if (register_firstname.getText().length() == 0) {
            firstnameTextInputLayout.setError(getString(R.string.error_firstname));
            return false;
        } else {
            unsetLayoutError(firstnameTextInputLayout);
        }
        if (register_lastname.getText().length() == 0) {
            lastnameTextInputLayout.setError(getString(R.string.error_lastname));
            return false;
        } else {
            unsetLayoutError(lastnameTextInputLayout);
        }
        if (register_gender.getSelectedItem().toString().equals("Gender")) {
            createSnackbar(register_two, R.string.error_gender);
            return false;
        }
        if (register_age_range.getSelectedItem().toString().equals("Age Range")) {
            createSnackbar(register_two, R.string.error_age_range);
            return false;
        }
        if (register_mobile_number.getText().length() == 0) {
            mobilenumberTextInputLayout.setError(getString(R.string.error_mobile));
            return false;
        } else {
            unsetLayoutError(mobilenumberTextInputLayout);
        }
        if (register_country.getSelectedItem().toString().equals("Country")) {
            createSnackbar(register_two, R.string.error_country);
            return false;
        }
        if (register_city.getSelectedItem().toString().equals("City")) {
            createSnackbar(register_two, R.string.error_city);
            return false;
        }
        if (!register_agree.isChecked()) {
            createSnackbar(register_two, R.string.error_agree);
            return false;
        }
        return true;
    }

    /**
     * Validates the email address
     */
    public static boolean validEmail(String string) {
        boolean isValid = false;

        String expression = "^(([\\w-]+\\.)+[\\w-]+|([a-zA-Z]{1}|[\\w-]{2,}))@"
                + "((([0-1]?[0-9]{1,2}|25[0-5]|2[0-4][0-9])\\.([0-1]?"
                + "[0-9]{1,2}|25[0-5]|2[0-4][0-9])\\."
                + "([0-1]?[0-9]{1,2}|25[0-5]|2[0-4][0-9])\\.([0-1]?"
                + "[0-9]{1,2}|25[0-5]|2[0-4][0-9])){1}|"
                + "([a-zA-Z]+[\\w-]+\\.)+[a-zA-Z]{2,4})$";

        Pattern pattern = Pattern.compile(expression);
        Matcher matcher = pattern.matcher(string);
        if (matcher.matches()) {
            isValid = true;
        }
        return isValid;
    }

    /**
     * Removes the error message from the TextInputLayout
     */
    public static void unsetLayoutError(TextInputLayout textInputLayout) {
        textInputLayout.setError("");
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

    /**
     * Gets the Country lists from the server to display in a list with Ion
     */
    private void getCountryList() {
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
                                countryId.add("country_id");
                                cityId.add("city_id");

                                JsonArray resultArray = result.get("citylist").getAsJsonArray();

                                for (int i = 0; i < resultArray.size(); i++) {
                                    JsonObject resultObject = resultArray.get(i).getAsJsonObject();
                                    JsonObject result_inner = resultObject.get("city_list").getAsJsonObject();

                                    // Avoids duplicate values in the cityName List
                                    String cityNameValue = result_inner.get("city_name").getAsString();
                                    if (!cityName.contains(cityNameValue)) {
                                        cityName.add(result_inner.get("city_name").getAsString());
                                        cityId.add(result_inner.get("city_id").getAsString());
                                    }

                                    // Avoids duplicate values in the countryName List
                                    String countryNameValue = result_inner.get("country_name").getAsString();
                                    if (!countryName.contains(countryNameValue)) {
                                        countryName.add(countryNameValue);
                                        countryId.add(result_inner.get("country_id").getAsString());
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
     * Submits the signup details to the server with Ion
     */
    private void submitData() {
        if (signinValidationTwo()) {
            if (networkConnection.isInternetOn()) {
                progressDialog = ProgressDialog.show(RegisterActivity.this, "",
                        getResources().getString(R.string.register_account_info));

                Ion.with(this)
                        .load(Utility.HOST_VALUE + Utility.REGISTER_PATH_VALUE)
                        .setBodyParameter("firstname", register_firstname.getText().toString())
                        .setBodyParameter("lang", URL)
                        .setBodyParameter("email", register_email.getText().toString())
                        .setBodyParameter("password", register_confirm_password.getText().toString())
                        .setBodyParameter("city_id", selectedCityId)
                        .asJsonObject()
                        .setCallback(new FutureCallback<JsonObject>() {

                            @Override
                            public void onCompleted(Exception e, JsonObject result) {
                                progressDialog.dismiss();

                                JsonObject response = result.get("response").getAsJsonObject();

                                // Checks if Signup was successful
                                if (e == null) {
                                    if (response.get("httpCode").getAsInt() != 200) {
                                        // Displays the error message in a dialog
                                        AlertDialog.Builder builder = new AlertDialog.Builder(RegisterActivity.this);
                                        builder.setMessage(response.get("Message").getAsString())
                                                .setCancelable(true)
                                                .setPositiveButton(R.string.ok, new DialogInterface.OnClickListener() {
                                                    @Override
                                                    public void onClick(DialogInterface dialog, int which) {
                                                        dialog.dismiss();
                                                    }
                                                });
                                        AlertDialog alertDialog = builder.create();
                                        alertDialog.show();
                                    } else {
                                        // Saves the username to a SharedPrefrence for use in another activity
                                        Snackbar.make(registerView, R.string.successful_login, Snackbar.LENGTH_SHORT)
                                                .show();
                                        BaseNavigationDrawerActivity.SIGNED_IN = true;
                                        editor.putString("username", "Welcome, " + register_username.getText().toString());
                                        editor.commit();

                                        // Logs the user in directly
                                        Intent i = new Intent(RegisterActivity.this, HomeActivity.class);
                                        startActivity(i);
                                        finish();
                                    }
                                } else {
                                    AlertDialog.Builder builder = new AlertDialog.Builder(RegisterActivity.this);

                                    builder.setMessage(R.string.unable_to_connect)
                                            .setCancelable(true)
                                            .setPositiveButton(R.string.try_again, new DialogInterface.OnClickListener() {
                                                @Override
                                                public void onClick(DialogInterface dialog, int which) {
                                                    submitData();
                                                }
                                            })
                                            .setNegativeButton(R.string.ok, new DialogInterface.OnClickListener() {
                                                @Override
                                                public void onClick(DialogInterface dialogInterface, int i) {
                                                    dialogInterface.dismiss();
                                                }
                                            });
                                    AlertDialog alertDialog = builder.create();
                                    alertDialog.show();
                                }
                            }
                        });
            } else {
                networkConnection.displayAlert();
            }
        }
    }

    /**
     * Overrides the default onBackpressed() to send the user back to the landing page
     */
    @Override
    public void onBackPressed() {
        Intent i = new Intent(RegisterActivity.this, HomeActivity.class);
        startActivity(i);
        finish();
    }
}