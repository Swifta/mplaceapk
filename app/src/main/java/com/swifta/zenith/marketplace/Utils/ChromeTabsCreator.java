package com.swifta.zenith.marketplace.Utils;

import android.app.Activity;
import android.content.Context;
import android.net.Uri;
import android.os.Bundle;
import android.support.customtabs.CustomTabsCallback;
import android.support.customtabs.CustomTabsClient;
import android.support.customtabs.CustomTabsIntent;
import android.support.customtabs.CustomTabsServiceConnection;
import android.support.customtabs.CustomTabsSession;
import android.support.v4.content.ContextCompat;
import android.util.Log;

import com.swifta.zenith.marketplace.R;

/**
 * Created by moyinoluwa on 10/8/15.
 */
public class ChromeTabsCreator {

    private static final String TAG = "CustomTabsClient";
    private CustomTabsSession mCustomTabsSession;
    private CustomTabsClient mClient;
    private CustomTabsServiceConnection mConnection;

    /**
     * Creates a custom Chrome tab for the blog
     */
    public void createDefaultChromeTab(Context context, Activity activity) {
        CustomTabsIntent.Builder builder = new CustomTabsIntent.Builder(getSession());
        builder.setToolbarColor(ContextCompat.getColor(context, R.color.colorPrimary)).setShowTitle(true);
        builder.setStartAnimations(context, android.R.anim.slide_out_right, android.R.anim.slide_in_left);
        builder.setExitAnimations(context, android.R.anim.slide_in_left, android.R.anim.slide_out_right);
        CustomTabsIntent customTabsIntent = builder.build();
        customTabsIntent.launchUrl(activity, Uri.parse(Utility.BLOG_VALUE));
    }

    private CustomTabsSession getSession() {
        if (mClient == null) {
            mCustomTabsSession = null;
        } else if (mCustomTabsSession == null) {
            mCustomTabsSession = mClient.newSession(new CustomTabsCallback() {
                @Override
                public void onNavigationEvent(int navigationEvent, Bundle extras) {
                    Log.w(TAG, "onNavigationEvent: Code = " + navigationEvent);
                }
            });
        }
        return mCustomTabsSession;
    }

    /**
     * Resets the client and session when the connection is null
     **/
    public void unbindCustomTabsService() {
        if (mConnection == null) return;
        // unbindService(mConnection);
        mClient = null;
        mCustomTabsSession = null;
    }
}
