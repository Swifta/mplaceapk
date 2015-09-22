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
import android.widget.EditText;

import com.google.gson.JsonObject;
import com.koushikdutta.async.future.FutureCallback;
import com.koushikdutta.ion.Ion;
import com.swifta.zenith.marketplace.Utils.NetworkConnection;
import com.swifta.zenith.marketplace.R;
import com.swifta.zenith.marketplace.Utils.Utility;

public class SignInActivity extends BaseToolbarActivity {
    private View signInView;
    private TextInputLayout emailTextInputLayout;
    private TextInputLayout passwordTextInputLayout;
    private TextInputLayout emailIdTextInputLayout;
    private EditText signInEmail;
    private EditText signInPassword;
    private EditText emailIdEditText;
    private NetworkConnection networkConnection;
    private String email;
    private String forgotPasswordEmail;
    private String password;
    private ProgressDialog progressDialog;
    private static final String URL = "en";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        // Inflates SignInActivity's layout into the parent's layout
        signInView = getLayoutInflater().inflate(R.layout.activity_sign_in, mNestedScrollView);

        emailTextInputLayout = (TextInputLayout) signInView.findViewById(R.id.email_textinputlayout);
        passwordTextInputLayout = (TextInputLayout) signInView.findViewById(R.id.password_textinputlayout);
        signInEmail = (EditText) signInView.findViewById(R.id.sign_in_email);
        signInPassword = (EditText) signInView.findViewById(R.id.sign_in_password);
        networkConnection = new NetworkConnection(SignInActivity.this);

        // Sets the Back button on the Toolbar to HomeActivity
        mToolbar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent i = new Intent(SignInActivity.this, HomeActivity.class);
                startActivity(i);
                finish();
            }
        });
    }

    /**
     * Handles the clicks for the necessary views
     */
    public void signInOnClick(View view) {
        switch (view.getId()) {
            case (R.id.sign_in_button):
                email = signInEmail.getText().toString();
                password = signInPassword.getText().toString();
                signIn();
                break;
            case (R.id.sign_up_text):
                Intent i = new Intent(SignInActivity.this, RegisterActivity.class);
                startActivity(i);
                finish();
                break;
            case (R.id.forgot_password_text):
                createForgotPasswordAlert(view);
                break;
        }
    }

    /**
     * Validates the email address and password fields
     */
    public boolean validateSignInField() {
        // Checks if the email address is empty
        if (email.length() == 0) {
            emailTextInputLayout.setError(getString(R.string.error_email));
            return false;
        }
        // Checks if the email address is valid
        if (!RegisterActivity.validEmail(email)) {
            emailTextInputLayout.setError(getString(R.string.error_valid_email));
            return false;
        } else {
            RegisterActivity.unsetLayoutError(emailTextInputLayout);
        }
        // Verifies that the password is not empty
        if (password.length() == 0) {
            passwordTextInputLayout.setError(getString(R.string.error_password));
            return false;
        }
        // Verifies that the password is greater than six
        if (password.length() < 6) {
            passwordTextInputLayout.setError(getString(R.string.error_short_password));
            return false;
        }
        return true;
    }

    /**
     * Sends the user's details to the server for verification with Ion
     */
    private void signIn() {
        if (validateSignInField()) {
            if (networkConnection.isInternetOn()) {

                // Loads the progress dialog
                progressDialog = ProgressDialog.show(SignInActivity.this, "",
                        getResources().getString(R.string.verify_account_info));

                Ion.with(this)
                        .load(Utility.HOST_VALUE + Utility.SIGN_IN_PATH_VALUE)
                        .setBodyParameter("email", email)
                        .setBodyParameter("password", password)
                        .setBodyParameter("lang", URL)
                        .asJsonObject()
                        .setCallback(new FutureCallback<JsonObject>() {

                            @Override
                            public void onCompleted(Exception e, JsonObject result) {
                                progressDialog.dismiss();
                                JsonObject response = result.get("response").getAsJsonObject();

                                // Checks if login was successful
                                if (e == null) {
                                    if (response.get("httpCode").getAsInt() != 200) {
                                        // Displays the error message in a dialog
                                        AlertDialog.Builder builder = new AlertDialog.Builder(SignInActivity.this);
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
                                        Snackbar.make(signInView, R.string.successful_login, Snackbar.LENGTH_SHORT)
                                                .show();
                                        BaseNavigationDrawerActivity.SIGNED_IN = true;

                                        SharedPreferences sharedPreferences = getSharedPreferences("Preferences", Context.MODE_PRIVATE);
                                        SharedPreferences.Editor editor = sharedPreferences.edit();
                                        editor.putString("username", "Welcome back, " + email);
                                        editor.commit();
                                        startActivity(new Intent(SignInActivity.this,
                                                HomeActivity.class));
                                        finish();
                                    }
                                } else {
                                    AlertDialog.Builder builder = new AlertDialog.Builder(SignInActivity.this);

                                    builder.setMessage(R.string.unable_to_connect)
                                            .setCancelable(true)
                                            .setPositiveButton(R.string.try_again, new DialogInterface.OnClickListener() {
                                                @Override
                                                public void onClick(DialogInterface dialog, int which) {
                                                    signIn();
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
     * Uses an alternative method to sign the user in with Ion
     */
    private void forgotPassword() {
        if (validateEmailId()) {
            if (networkConnection.isInternetOn()) {
                progressDialog = ProgressDialog.show(SignInActivity.this, "",
                        getResources().getString(R.string.verify_account_info));

                // Connecting to the server with ION
                Ion.with(this)
                        .load(Utility.HOST_VALUE + Utility.FORGOT_PASSWORD_PATH_VALUE)
                        .setBodyParameter("emailid", forgotPasswordEmail)
                        .setBodyParameter("lang", URL)
                        .asJsonObject()
                        .setCallback(new FutureCallback<JsonObject>() {
                            @Override
                            public void onCompleted(Exception e, JsonObject result) {
                                progressDialog.dismiss();
                                // Checks if login was successful
                                if (e == null) {
                                    JsonObject response = result.get("response").getAsJsonObject();
                                    if (response.get("httpCode").getAsInt() != 200) {
                                        // Displays the error message in a dialog
                                        AlertDialog.Builder builder = new AlertDialog.Builder(SignInActivity.this);
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
                                    }
                                } else {
                                    AlertDialog.Builder builder = new AlertDialog.Builder(SignInActivity.this);

                                    builder.setMessage(R.string.unable_to_connect)
                                            .setCancelable(true)
                                            .setPositiveButton(R.string.try_again, new DialogInterface.OnClickListener() {
                                                @Override
                                                public void onClick(DialogInterface dialog, int which) {
                                                    forgotPassword();
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
     * Displays an Alertdialog on click of the Forgot Password text
     */
    private void createForgotPasswordAlert(View view) {
        view = View.inflate(SignInActivity.this, R.layout.forgot_password_dialog, null);

        emailIdTextInputLayout = (TextInputLayout)
                view.findViewById(R.id.forgot_password_textinputlayout);
        emailIdEditText = (EditText) view.findViewById(R.id.forgot_password_email_id);

        AlertDialog.Builder builder = new AlertDialog.Builder(SignInActivity.this);

        builder.setView(view);
        builder.setTitle(R.string.forgot_password)
                .setCancelable(true)
                .setPositiveButton(R.string.request_password, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        forgotPasswordEmail = emailIdEditText.getText().toString();
                        forgotPassword();
                    }
                });
        AlertDialog alertDialog = builder.create();
        alertDialog.show();
    }

    /**
     * Validates the email address to check for empty and non-valid addresses
     * on Forgot Password click
     */
    public boolean validateEmailId() {
        // Checks if the address is null or empty
        if (forgotPasswordEmail == null || forgotPasswordEmail.length() == 0) {
            emailIdTextInputLayout.setError(getString(R.string.error_email));
            return false;
        }
        // Checks if the email address is valid
        if (!RegisterActivity.validEmail(forgotPasswordEmail)) {
            emailIdTextInputLayout.setError(getString(R.string.error_valid_email));
            return false;
        }
        return true;
    }

    /**
     * Overrides the default onBackpressed() to send the user back to the landing page
     */
    @Override
    public void onBackPressed() {
        Intent i = new Intent(SignInActivity.this, HomeActivity.class);
        startActivity(i);
        finish();
    }
}

