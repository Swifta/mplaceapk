package com.swifta.zenith.marketplace.Utils;

import android.text.Html;
import android.text.Spanned;

/**
 * Created by moyinoluwa on 10/6/15.
 */
public class UnicodeConverter {

    /**
     * Allows String to display HTML in the Textview
     **/
    public static Spanned getConversionResult(String unicode) {
        return Html.fromHtml(unicode);
    }
}
