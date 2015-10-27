package com.swifta.zenith.marketplace.Utils;

import android.content.Context;
import android.support.design.widget.TextInputLayout;
import android.widget.EditText;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * Created by moyinoluwa on 10/12/15.
 */
public class TextVerifier {

    private final String EMPTY_EDIT_TEXT_VALUE = "";
    private Context mContext;

    public TextVerifier(Context context) {
        this.mContext = context;
    }

    /**
     * Verifies a normal input field is not empty
     */
    public boolean resolveEditText(EditText editText, TextInputLayout textInputLayout, String errorText) {

        if (isEditTextEmpty(editText)) {
            setLayoutErrorMessage(textInputLayout, errorText);
            return false;
        } else {
            removeLayoutErrorMessage(textInputLayout);
            return true;
        }
    }

    /**
     * Verifies an email input field is not empty and is valid
     */
    public boolean resolveEmailEditText(EditText editText, TextInputLayout textInputLayout, String emptyErrorText, String validErrorText) {

        if (isEditTextEmpty(editText)) {
            setLayoutErrorMessage(textInputLayout, emptyErrorText);
            return false;
        }

        if (!validEmail(editText.getText().toString())) {
            setLayoutErrorMessage(textInputLayout, validErrorText);
            return false;
        } else {
            removeLayoutErrorMessage(textInputLayout);
            return true;
        }
    }

    /**
     * Verifies a password input field is not empty and it's length is not less than a given number
     */
    public boolean resolvePasswordEditText(EditText editText, TextInputLayout textInputLayout, String emptyErrorText, String shortErrorText, int minimumLength) {
        if (isEditTextEmpty(editText)) {
            setLayoutErrorMessage(textInputLayout, emptyErrorText);
            return false;
        }

        if (isPasswordShort(editText, minimumLength)) {
            setLayoutErrorMessage(textInputLayout, shortErrorText);
            return false;
        } else {
            removeLayoutErrorMessage(textInputLayout);
            return true;
        }
    }

    /**
     * Verifies a confirm password input field is not empty and is equal to the initial password entered
     */
    public boolean resolveConfirmPasswordEditText(EditText firstPasswordEditText, EditText secondPasswordEditText,
                                                  TextInputLayout textInputLayout, String emptyErrorText, String equalErrorText) {
        if (isEditTextEmpty(secondPasswordEditText)) {
            setLayoutErrorMessage(textInputLayout, emptyErrorText);
            return false;
        }

        if (!isPasswordEqual(firstPasswordEditText, secondPasswordEditText)) {
            setLayoutErrorMessage(textInputLayout, equalErrorText);
            return false;
        } else {
            removeLayoutErrorMessage(textInputLayout);
            return true;
        }
    }

    /**
     * Verifies edittext is not empty by checking the length of it's text
     */
    private boolean isEditTextEmpty(EditText editText) {

        return editText.getText().length() == 0;
    }

    /**
     * Checks if password length is longer than a given amount
     */
    private boolean isPasswordShort(EditText editText, int length) {
        String password = editText.getText().toString();

        return password.length() < length;
    }

    /**
     * Checks if both initial and confirm passwords are equal
     */
    private boolean isPasswordEqual(EditText firstPasswordEditText, EditText secondPasswordEditText) {
        String firstPassword = firstPasswordEditText.getText().toString();
        String secondPassword = secondPasswordEditText.getText().toString();

        return firstPassword.equals(secondPassword);
    }

    /**
     * Validates the email address
     */
    private boolean validEmail(String string) {
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
     * Adds an error message to the TextInputLayout
     */
    private void setLayoutErrorMessage(TextInputLayout textInputLayout, String errorText) {
        textInputLayout.setError(errorText);
    }

    /**
     * Removes the error message from the TextInputLayout
     */
    private void removeLayoutErrorMessage(TextInputLayout textInputLayout) {
        textInputLayout.setError(EMPTY_EDIT_TEXT_VALUE);
    }

}
