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
import com.squareup.picasso.Picasso;
import com.swifta.zenith.marketplace.Database.ProfileDatabase;
import com.swifta.zenith.marketplace.R;
import com.swifta.zenith.marketplace.Utils.Dictionary;
import com.swifta.zenith.marketplace.Utils.NetworkConnection;
import com.swifta.zenith.marketplace.Utils.Session;
import com.swifta.zenith.marketplace.Utils.Utility;

import de.hdodenhof.circleimageview.CircleImageView;

public class ProfileActivity extends BaseNavigationDrawerActivity {
    final String LANG = "en";
    View homeView;
    View editFloatingButtonView;
    View saveFloatingButtonView;
    View addFloatingButtonView;
    ProgressBar mProgressBar;
    TextView noProfileData;
    CircleImageView imageView;
    EditText profileFirstName;
    EditText profileLastName;
    EditText profileEmail;
    EditText profileAddress1;
    EditText profileAddress2;
    EditText profileCityName;
    EditText profilePhone;
    CardView profileCardview;
    FloatingActionButton addFloatingActionButton;
    FloatingActionButton editFloatingActionButton;
    FloatingActionButton saveFloatingActionButton;
    NetworkConnection networkConnection;
    ProfileDatabase profileDatabase;
    private MenuItem hMenuItem;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        // Inflates ProfileActivity's layout into the parent's layout
        homeView = getLayoutInflater().inflate(R.layout.activity_profile, mNestedScrollView);

        // Selects ProfileActivity in the parent DrawerLayout and checks it
        hMenuItem = mNavigationView.getMenu().findItem(R.id.main_navigation_item_22);
        if (hMenuItem.isChecked()) {
            hMenuItem.setChecked(false);
        } else {
            hMenuItem.setChecked(true);
        }

        mProgressBar = (ProgressBar) findViewById(R.id.profile_progress);
        noProfileData = (TextView) findViewById(R.id.no_profile_detail);
        imageView = (CircleImageView) findViewById(R.id.profile_image);
        profileFirstName = (EditText) findViewById(R.id.profile_firstname);
        profileLastName = (EditText) findViewById(R.id.profile_lastname);
        profileEmail = (EditText) findViewById(R.id.profile_email);
        profileAddress1 = (EditText) findViewById(R.id.profile_address1);
        profileAddress2 = (EditText) findViewById(R.id.profile_address2);
        profileCityName = (EditText) findViewById(R.id.profile_city);
        profilePhone = (EditText) findViewById(R.id.profile_phone);
        profileCardview = (CardView) findViewById(R.id.profile_cardview);
        networkConnection = new NetworkConnection(this);

        initializeProfileDetails();

    }

    /**
     * Loads all the profile details from the server with Ion
     */
    private void initializeProfileDetails() {
        if (networkConnection.isInternetOn()) {
            Ion.with(this)
                    .load(Utility.HOST_VALUE + Utility.PROFILE_PATH_VALUE + LANG + "/" + Session.getUserId(ProfileActivity.this) + ".html")
                    .asJsonObject()
                    .setCallback(new FutureCallback<JsonObject>() {
                        @Override
                        public void onCompleted(Exception e, JsonObject result) {
                            mProgressBar.setVisibility(View.GONE);
                            if (e == null) {

                                final String cityId;
                                final String image;

                                JsonObject resultObject = result.get("response").getAsJsonObject();
                                int resultElement = resultObject.get("httpCode").getAsInt();
                                if (resultElement != 200) {
                                    noProfileData.setVisibility(View.VISIBLE);
                                    addFloatingButtonView = getLayoutInflater().inflate(R.layout.add_shipping_fab, mCoordinatorLayout);
                                    addFloatingActionButton = (android.support.design.widget.FloatingActionButton) addFloatingButtonView.findViewById(R.id.add_floating_action_button);

                                    noProfileData.setText("You don't have a profile yet.");
                                } else {
                                    profileCardview.setVisibility(View.VISIBLE);

                                    // Transfers all the data from the JsonObject into Strings
                                    String email = resultObject.get(Dictionary.email).getAsString();
                                    String firstname = resultObject.get(Dictionary.firstName).getAsString();
                                    String lastname = resultObject.get(Dictionary.lastName).getAsString();
                                    String address1 = resultObject.get(Dictionary.address1).getAsString();
                                    String address2 = resultObject.get(Dictionary.address2).getAsString();
                                    String cityName = resultObject.get(Dictionary.cityName).getAsString();
                                    String phone = resultObject.get(Dictionary.phone).getAsString();
                                    image = resultObject.get(Dictionary.userImage).getAsString();
                                    cityId = resultObject.get(Dictionary.cityId).getAsString();

                                    // Loads the image directly from the server with the Picasso into a circular imageview
                                    Picasso.with(ProfileActivity.this)
                                            .load(image)
                                            .placeholder(R.drawable.home_background)
                                            .error(R.drawable.home_background)
                                            .noFade()
                                            .into(imageView);

                                    // Sets the edittexts with their respective values
                                    profileFirstName.setText(firstname);
                                    profileLastName.setText(lastname);
                                    profileEmail.setText(email);
                                    profileAddress1.setText(address1);
                                    profileAddress2.setText(address2);
                                    profileCityName.setText(cityName);
                                    profilePhone.setText(phone);

                                    // Stores new data into the Profile database
                                    profileDatabase = new ProfileDatabase(email, firstname, lastname, address1, address2, cityId, cityName, phone, image);
                                    profileDatabase.save();

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

                                            enableAllEditTexts(profileFirstName, profileLastName, profileEmail, profileAddress1,
                                                    profileAddress2, profileCityName, profilePhone);

                                            Toast.makeText(ProfileActivity.this, "Update any field by touching it.", Toast.LENGTH_SHORT).show();

                                            editFloatingActionButton.setVisibility(View.GONE);

                                            saveFloatingActionButton.setVisibility(View.VISIBLE);
                                        }
                                    });

                                    // Sets a listener for the save floating action button
                                    saveFloatingActionButton.setOnClickListener(new View.OnClickListener() {
                                        @Override
                                        public void onClick(View v) {

                                            ProfileDatabase.deleteAll(ProfileDatabase.class);

                                            String firstname = profileFirstName.getText().toString();
                                            String lastname = profileLastName.getText().toString();
                                            String email = profileEmail.getText().toString();
                                            String address1 = profileAddress1.getText().toString();
                                            String address2 = profileAddress2.getText().toString();
                                            String cityName = profileCityName.getText().toString();
                                            String phone = profilePhone.getText().toString();

                                            profileDatabase = new ProfileDatabase(email, firstname, lastname, address1, address2, cityId, cityName, phone, image);
                                            profileDatabase.save();

                                            Toast.makeText(ProfileActivity.this, "Your changes have been saved successfully.", Toast.LENGTH_SHORT).show();

                                            disableAllEditTexts(profileFirstName, profileLastName, profileEmail, profileAddress1,
                                                    profileAddress2, profileCityName, profilePhone);

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
     * Enables all the edittexts in the Profile Activity
     */
    private void enableAllEditTexts(EditText profileFirstName, EditText profileLastName, EditText profileEmail, EditText profileAddress1,
                                    EditText profileAddress2, EditText profileCityName, EditText profilePhone) {

        enableEditText(profileFirstName);
        enableEditText(profileLastName);
        enableEditText(profileEmail);
        enableEditText(profileAddress1);
        enableEditText(profileAddress2);
        enableEditText(profileCityName);
        enableEditText(profilePhone);
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
     * Disables all the edittexts in the Profile Activity
     */
    private void disableAllEditTexts(EditText profileFirstName, EditText profileLastName, EditText profileEmail, EditText profileAddress1,
                                     EditText profileAddress2, EditText profileCityName, EditText profilePhone) {
        disableEditText(profileFirstName);
        disableEditText(profileLastName);
        disableEditText(profileEmail);
        disableEditText(profileAddress1);
        disableEditText(profileAddress2);
        disableEditText(profileCityName);
        disableEditText(profilePhone);
    }
}
