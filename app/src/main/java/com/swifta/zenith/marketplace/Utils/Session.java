package com.swifta.zenith.marketplace.Utils;

import android.app.Activity;
import android.content.Context;
import android.content.SharedPreferences;

/**
 * Created by moyinoluwa on 10/8/15.
 */
public class Session {

    public static final String EMPTY_STRING = "";
    private static final String KEY = "session";
    private static final String USER_ID = "userId";
    private static final String EMAIL = "email";
    private static final String LONGITUDE = "longitude";
    private static final String LATITUDE = "latitude";
    private static final String NEW_USER = "new_user";

    /**
     * Saves the user id
     */
    public static void saveUserId(String userId, Context context) {
        SharedPreferences.Editor editor = context.getSharedPreferences(
                KEY, Activity.MODE_PRIVATE).edit();
        editor.putString(USER_ID, userId);
        editor.apply();
    }

    /**
     * Returns the user id and an empty string if empty
     */
    public static String getUserId(Context context) {
        SharedPreferences savedSession = context.getSharedPreferences(KEY,
                Activity.MODE_PRIVATE);
        return savedSession.getString(USER_ID, EMPTY_STRING);
    }

    /**
     * Saves the email address
     */
    public static void saveEmail(String email, Context context) {
        SharedPreferences.Editor editor = context.getSharedPreferences(
                KEY, Activity.MODE_PRIVATE).edit();
        editor.putString(EMAIL, email);
        editor.apply();
    }

    /**
     * Returns the email address and an empty string if empty
     */
    public static String getEmail(Context context) {
        SharedPreferences savedSession = context.getSharedPreferences(
                KEY, Activity.MODE_PRIVATE);
        return savedSession.getString(EMAIL, EMPTY_STRING);
    }

    /**
     * Saves the longitude
     */
    public static void saveLongitude(String longitude, Context context) {
        SharedPreferences.Editor editor = context.getSharedPreferences(
                KEY, Activity.MODE_PRIVATE).edit();
        editor.putString(LONGITUDE, longitude);
        editor.apply();
    }

    /**
     * Returns the longitude
     */
    public static String getLongitude(Context context) {
        SharedPreferences savedSession = context.getSharedPreferences(
                KEY, Activity.MODE_PRIVATE);
        return savedSession.getString(LONGITUDE, EMPTY_STRING);
    }

    /**
     * Saves the latitude
     */
    public static void saveLatitude(String latitude, Context context) {
        SharedPreferences.Editor editor = context.getSharedPreferences(
                KEY, Activity.MODE_PRIVATE).edit();
        editor.putString(LATITUDE, latitude);
        editor.apply();
    }

    /**
     * Returns the latitude
     */
    public static String getLatitude(Context context) {
        SharedPreferences savedSession = context.getSharedPreferences(
                KEY, Activity.MODE_PRIVATE);
        return savedSession.getString(LATITUDE, EMPTY_STRING);
    }

    /**
     * Saves the user status
     */
    public static void saveUserStatus(boolean status, Context context) {
        SharedPreferences.Editor editor = context.getSharedPreferences(
                KEY, Activity.MODE_PRIVATE).edit();
        editor.putBoolean(NEW_USER, status);
        editor.apply();
    }

    /**
     * Returns the user status
     */
    public static boolean isNewUser(Context context) {
        SharedPreferences savedSession = context.getSharedPreferences(
                KEY, Activity.MODE_PRIVATE);
        return savedSession.getBoolean(NEW_USER, true);
    }

    /**
     * Clears all the data from the SharedPreference
     */
    public static void clear(Context context) {
        SharedPreferences.Editor editor = context.getSharedPreferences(KEY, Activity.MODE_PRIVATE)
                .edit();
        editor.clear();
        editor.apply();
    }
}
