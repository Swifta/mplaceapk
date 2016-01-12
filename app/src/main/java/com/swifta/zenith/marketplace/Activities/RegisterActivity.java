package com.swifta.zenith.marketplace.Activities;

import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.support.design.widget.Snackbar;
import android.support.design.widget.TextInputLayout;
import android.util.Base64;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.facebook.AccessToken;
import com.facebook.CallbackManager;
import com.facebook.FacebookCallback;
import com.facebook.FacebookException;
import com.facebook.FacebookRequestError;
import com.facebook.FacebookSdk;
import com.facebook.GraphRequest;
import com.facebook.GraphResponse;
import com.facebook.HttpMethod;
import com.facebook.login.LoginManager;
import com.facebook.login.LoginResult;
import com.facebook.login.widget.LoginButton;
import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
import com.koushikdutta.async.future.FutureCallback;
import com.koushikdutta.ion.Ion;
import com.swifta.zenith.marketplace.R;
import com.swifta.zenith.marketplace.Utils.NetworkConnection;
import com.swifta.zenith.marketplace.Utils.TextVerifier;
import com.swifta.zenith.marketplace.Utils.Utility;
import com.twitter.sdk.android.Twitter;
import com.twitter.sdk.android.core.Callback;
import com.twitter.sdk.android.core.Result;
import com.twitter.sdk.android.core.TwitterAuthConfig;
import com.twitter.sdk.android.core.TwitterException;
import com.twitter.sdk.android.core.TwitterSession;
import com.twitter.sdk.android.core.identity.TwitterLoginButton;

import org.json.JSONObject;

import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.security.Signature;
import java.util.ArrayList;

import io.fabric.sdk.android.Fabric;

public class RegisterActivity extends BaseToolbarActivity {

    private static final String URL = "en";
    private static SharedPreferences.Editor editor;
    View registerView;
    private LinearLayout registerOption;
    private LinearLayout registerOne;
    private LinearLayout registerTwo;
    private Spinner registerGender;
    private Spinner registerAgeRange;
    private Spinner registerCity;
    private Spinner registerCountry;
    private TextInputLayout usernameTextInputLayout;
    private TextInputLayout emailTextInputLayout;
    private TextInputLayout passwordTextInputLayout;
    private TextInputLayout confirmPasswordTextInputLayout;
    private TextInputLayout uniqueIdentifierTextInputLayout;
    private TextInputLayout firstnameTextInputLayout;
    private TextInputLayout lastnameTextInputLayout;
    private TextInputLayout mobilenumberTextInputLayout;
    private EditText registerUniqueIdentifier;
    private EditText registerConfirmPassword;
    private EditText registerPassword;
    private EditText registerUsername;
    private EditText registerEmail;
    private EditText registerFirstname;
    private EditText registerLastname;
    private EditText registerMobileNumber;
    private TextView signUpOptionText;
    private LoginButton facebookLoginButton;
    private CheckBox registerAgree;
    private ArrayList<String> cityName = new ArrayList<String>();
    private ArrayList<String> countryName = new ArrayList<String>();
    private ArrayList<String> cityId = new ArrayList<>();
    private ArrayList<String> countryId = new ArrayList<>();
    private ArrayAdapter<String> countrySpinnerAdapter;
    private ArrayAdapter<String> citySpinnerAdapter;
    private NetworkConnection networkConnection;
    private TextVerifier textVerifier;
    private ProgressDialog progressDialog;
    private String selectedCityId;
    private SharedPreferences preferences;
    private CallbackManager callbackManager;
    private AccessToken accessToken;
    private TwitterLoginButton twitterLoginButton;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        // Initiaizes the connection to the Facebook SDK from this Activity
        FacebookSdk.sdkInitialize(this);
        callbackManager = CallbackManager.Factory.create();

        String consumerKey = getResources().getString(R.string.twitter_consumer_key);
        String consumerSecret = getResources().getString(R.string.twitter_consumer_secret);

        // Initializes the connection to the Twitter SDK
        TwitterAuthConfig authConfig = new TwitterAuthConfig(consumerKey, consumerSecret);
        Fabric.with(this, new Twitter(authConfig));

        // Inflates RegisterActivity's layout into the parent's layout
        registerView = getLayoutInflater().inflate(R.layout.activity_register, mNestedScrollView);

        registerOption = (LinearLayout) registerView.findViewById(R.id.register_option);
        registerOne = (LinearLayout) registerView.findViewById(R.id.register_one);
        registerTwo = (LinearLayout) registerView.findViewById(R.id.register_two);
        signUpOptionText = (TextView) registerView.findViewById(R.id.sign_up_option_text);
        usernameTextInputLayout = (TextInputLayout) registerView.findViewById(R.id.username_textinputlayout);
        emailTextInputLayout = (TextInputLayout) registerView.findViewById(R.id.email_textinputlayout);
        passwordTextInputLayout = (TextInputLayout) registerView.findViewById(R.id.password_textinputlayout);
        confirmPasswordTextInputLayout = (TextInputLayout) registerView.findViewById(R.id.confirm_password_textinputlayout);
        uniqueIdentifierTextInputLayout = (TextInputLayout) registerView.findViewById(R.id.unique_identifier_textinputlayout);
        firstnameTextInputLayout = (TextInputLayout) registerView.findViewById(R.id.firstname_textinputlayout);
        lastnameTextInputLayout = (TextInputLayout) registerView.findViewById(R.id.lastname_textinputlayout);
        mobilenumberTextInputLayout = (TextInputLayout) registerView.findViewById(R.id.mobile_textinputlayout);
        registerUsername = (EditText) registerView.findViewById(R.id.register_username);
        registerEmail = (EditText) registerView.findViewById(R.id.register_email);
        registerConfirmPassword = (EditText) registerView.findViewById(R.id.register_confirm_password);
        registerPassword = (EditText) registerView.findViewById(R.id.register_password);
        registerUniqueIdentifier = (EditText) registerView.findViewById(R.id.register_unique_identifier);
        registerFirstname = (EditText) registerView.findViewById(R.id.register_first_name);
        registerLastname = (EditText) registerView.findViewById(R.id.register_last_name);
        registerMobileNumber = (EditText) registerView.findViewById(R.id.register_mobile_number);
        registerGender = (Spinner) registerView.findViewById(R.id.register_gender);
        registerAgeRange = (Spinner) registerView.findViewById(R.id.register_age_range);
        registerCountry = (Spinner) registerView.findViewById(R.id.register_country);
        registerCity = (Spinner) registerView.findViewById(R.id.register_city);
        facebookLoginButton = (LoginButton) registerView.findViewById(R.id.facebook_login_button);
        registerAgree = (CheckBox) registerView.findViewById(R.id.register_agree);
        twitterLoginButton = (TwitterLoginButton) findViewById(R.id.twitter_login_button);
        networkConnection = new NetworkConnection(RegisterActivity.this);
        preferences = getSharedPreferences("Preferences", Context.MODE_PRIVATE);
        editor = preferences.edit();
        textVerifier = new TextVerifier(this);

        // Sets the Back button on the Toolbar to HomeActivity
        mToolbar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent i = new Intent(RegisterActivity.this, HomeActivity.class);
                startActivity(i);
                finish();
            }
        });

        // Sets the permissions to be asked from the user
        facebookLoginButton.setReadPermissions("public_profile", "email");

        facebookLoginButton.registerCallback(callbackManager, new FacebookCallback<LoginResult>() {
            @Override
            public void onSuccess(LoginResult loginResult) {

                GraphRequest.newMeRequest(
                        loginResult.getAccessToken(), new GraphRequest.GraphJSONObjectCallback() {
                            @Override
                            public void onCompleted(JSONObject object, GraphResponse response) {
                                if (response.getError() != null) {
                                    Toast.makeText(RegisterActivity.this, "Could not get user's details.", Toast.LENGTH_SHORT).show();
                                } else {
                                    // Id not needed yet
                                    // String id = object.optString("id");
                                    // String email = object.optString("email");
                                    String firstName = object.optString("first_name", "User");
                                    performSuccessfulLoginAction("facebook", firstName);
                                }
                            }
                        }
                ).executeAsync();
            }

            @Override
            public void onCancel() {

            }

            @Override
            public void onError(FacebookException error) {
                Log.e("Facebook Login", error.getMessage());
            }
        });

        twitterLoginButton.setCallback(new Callback<TwitterSession>() {
            @Override
            public void success(Result<TwitterSession> result) {
                TwitterSession session = result.data;
                performSuccessfulLoginAction("twitter", session.getUserName());
            }

            @Override
            public void failure(TwitterException e) {
                Log.e("Twitterkit", "Login with Twitter failure", e);

            }
        });

        signUpOptionText.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                registerOption.setVisibility(View.GONE);
                registerOne.setVisibility(View.VISIBLE);
            }
        });

        createSpinner(registerGender, R.array.gender_array);
        createSpinner(registerAgeRange, R.array.age_range);

        // Check the network connection and get the countries and cities
        if (networkConnection.isInternetOn()) {
            // Populates the country spinner with data from the server
            getCountryList();
            createCountrySpinner(this.registerCountry, countryName);
            createCitySpinner(this.registerCity, cityName);

            registerCity.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
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

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        // For Facebook
        callbackManager.onActivityResult(requestCode, resultCode, data);

        // For Twitter
        twitterLoginButton.onActivityResult(requestCode, resultCode, data);
    }

    /**
     * Handles the clicks for the necessary views
     */
    public void registerOnClick(View view) {
        switch (view.getId()) {
            case (R.id.register_next):
                if (signinValidationOne()) {
                    registerOne.setVisibility(View.GONE);
                    registerTwo.setVisibility(View.VISIBLE);
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
                registerOne.setVisibility(View.VISIBLE);
                registerTwo.setVisibility(View.GONE);
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
        boolean username = false;
        boolean email = false;
        boolean password = false;
        boolean confirmPassword = false;
        boolean uniqueIdentifier = false;

        username = textVerifier.resolveEditText(registerUsername, usernameTextInputLayout, getString(R.string.error_username));

        if (username) {
            email = textVerifier.resolveEmailEditText(registerEmail, emailTextInputLayout,
                    getString(R.string.error_email), getString(R.string.error_valid_email));
        }

        if (email) {
            password = textVerifier.resolvePasswordEditText(registerPassword, passwordTextInputLayout,
                    getString(R.string.error_password), getString(R.string.error_short_password), 6);
        }

        if (password) {
            confirmPassword = textVerifier.resolveConfirmPasswordEditText(registerPassword, registerConfirmPassword,
                    confirmPasswordTextInputLayout, getString(R.string.error_confirm_password), getString(R.string.error_compare_password));
        }

        if (confirmPassword) {
            uniqueIdentifier = textVerifier.resolveEditText(registerUniqueIdentifier, uniqueIdentifierTextInputLayout, getString(R.string.error_unique_identifier));
        }

        return username && email && password && confirmPassword && uniqueIdentifier;
    }

    /**
     * Validates the second set of sign in fields
     */
    public boolean signinValidationTwo() {
        boolean firstName = false;
        boolean lastName = false;
        boolean mobileNumber = false;

        firstName = textVerifier.resolveEditText(registerFirstname, firstnameTextInputLayout, getString(R.string.error_firstname));

        if (!firstName) return firstName;

        if (firstName) {
            lastName = textVerifier.resolveEditText(registerLastname, lastnameTextInputLayout, getString(R.string.error_lastname));
        }

        if (!lastName) return lastName;


        if (registerGender.getSelectedItem().toString().equals("Gender")) {
            createSnackbar(registerTwo, R.string.error_gender);
            return false;
        }

        if (registerAgeRange.getSelectedItem().toString().equals("Age Range")) {
            createSnackbar(registerTwo, R.string.error_age_range);
            return false;
        }

        if (lastName) {
            mobileNumber = textVerifier.resolveEditText(registerMobileNumber, mobilenumberTextInputLayout, getString(R.string.error_mobile));
        }

        if (!mobileNumber) return mobileNumber;

        if (registerCountry.getSelectedItem().toString().equals("Country")) {
            createSnackbar(registerTwo, R.string.error_country);
            return false;
        }

        if (registerCity.getSelectedItem().toString().equals("City")) {
            createSnackbar(registerTwo, R.string.error_city);
            return false;
        }
        if (!registerAgree.isChecked()) {
            createSnackbar(registerTwo, R.string.error_agree);
            return false;
        }

        return true;
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
                        .setBodyParameter("firstname", registerFirstname.getText().toString())
                        .setBodyParameter("lang", URL)
                        .setBodyParameter("email", registerEmail.getText().toString())
                        .setBodyParameter("password", registerConfirmPassword.getText().toString())
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
                                        performSuccessfulLoginAction("email", registerUsername.getText().toString());
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

    /**
     * Performs this on successful user login
     */
    private void performSuccessfulLoginAction(String platform, String username) {
        Toast.makeText(RegisterActivity.this, R.string.successful_login, Toast.LENGTH_SHORT)
                .show();

        switch (platform) {
            case "email":
                BaseNavigationDrawerActivity.EMAIL_SIGNED_IN = true;
                break;
            case "facebook":
                BaseNavigationDrawerActivity.FACEBOOK_SIGNED_IN = true;
                break;
            case "twitter":
                BaseNavigationDrawerActivity.TWITTER_SIGNED_IN = true;
                break;
        }

        // Saves the username to a SharedPreference for use in another activity
        editor.putString("username", "Welcome, " + username);
        editor.commit();

        // Logs the user in directly
        Intent i = new Intent(RegisterActivity.this, HomeActivity.class);
        startActivity(i);
        finish();
    }
}