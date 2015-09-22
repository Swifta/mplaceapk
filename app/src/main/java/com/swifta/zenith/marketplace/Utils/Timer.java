package com.swifta.zenith.marketplace.Utils;

import android.text.format.Time;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.GregorianCalendar;
import java.util.Locale;

/**
 * Created by moyinoluwa on 9/17/15.
 */
public class Timer {

    /**
     * Displays the time left in days, hours and seconds
     */
    public static String calculateTime(double time) {

        long days;
        long hours;
        long minutes;
        long seconds;
        String daysT = "";
        String restT = "";

        days = (Math.round(time) / 86400);
        hours = (Math.round(time) / 3600) - (days * 24);
        minutes = (Math.round(time) / 60) - (days * 1440) - (hours * 60);
        seconds = Math.round(time) % 60;

        if (days == 1) {
            daysT = String.format("%d day ", days);
        }
        if (days > 1) {
            daysT = String.format("%d days ", days);
        }

        restT = String.format("%02d:%02d:%02d", hours, minutes, seconds);

        return daysT + restT;
    }

    /**
     * Calculates the difference between the current date and the end date
     */
    public static long getDateDifference(String date) throws ParseException {

        Date endDate = convertStringToDate(date);

        // Gets the current date
        Date startDate = new Date();

        return endDate.getTime() - startDate.getTime();
    }

    /**
     * Converts a string into a date
     */
    private static Date convertStringToDate(String date) throws ParseException {
        SimpleDateFormat dateFormat = new SimpleDateFormat("dd-MM-yyyy HH:mm:ss", Locale.US);

        Date endDate = dateFormat.parse(date);

        return endDate;
    }

    /**
     * Converts a string into a different date format
     */
    public static String changeDateFormat(String date) throws ParseException {
        SimpleDateFormat dateFormat = new SimpleDateFormat("dd-MM-yyyy HH:mm:ss", Locale.US);

        Date result = dateFormat.parse(date.toString());

        return result.toString();
    }
}

