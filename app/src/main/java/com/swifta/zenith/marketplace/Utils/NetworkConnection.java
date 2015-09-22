package com.swifta.zenith.marketplace.Utils;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;

import com.swifta.zenith.marketplace.R;

/**
 * Created by moyinoluwa on 18-Aug-15.
 */
public class NetworkConnection {
    private Context context;

    public NetworkConnection(Context context) {
        this.context = context;
    }

    public final boolean isInternetOn() {
        boolean hasConnectedWifi = false;
        boolean hasConnectedMobile = false;

        ConnectivityManager connectivityManager = (ConnectivityManager)
                context.getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo[] networkInfo = connectivityManager.getAllNetworkInfo();
        for (NetworkInfo netInfo : networkInfo) {
            if (netInfo.getTypeName().equalsIgnoreCase("WIFI")) {
                if (netInfo.isConnected()) {
                    hasConnectedWifi = true;
                }
            }
            if (netInfo.getTypeName().equalsIgnoreCase("MOBILE")) {
                if (netInfo.isConnected()) {
                    hasConnectedMobile = true;
                }
            }
        }
        return hasConnectedMobile || hasConnectedWifi;
    }

    public void displayAlert() {
        new AlertDialog.Builder(context)
                .setMessage(R.string.working_data_connection)
                .setTitle(R.string.network_failure)
                .setCancelable(true)
                .setNeutralButton("", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {

                    }
                })
                .show();
    }
}
