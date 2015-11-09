package com.swifta.zenith.marketplace.Utils;

import android.content.Context;
import android.view.View;

import com.swifta.zenith.marketplace.R;

/**
 * Created by moyinoluwa on 10/29/15.
 */
public class ShippingType {

    public static String getValue(String type, Context context) {
        String value = null;

        switch (type) {
            case "1":
                value = context.getResources().getString(R.string.free_ship);
                break;
            case "2":
                value = context.getResources().getString(R.string.flat_ship);
                break;
            case "3":
                value = context.getResources().getString(R.string.product_ship);
                break;
            case "4":
                value = context.getResources().getString(R.string.quantity_ship);
                break;
        }
        return value;
    }
}

