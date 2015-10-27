package com.swifta.zenith.marketplace.Database;

import com.orm.SugarRecord;

/**
 * Created by moyinoluwa on 9/23/15.
 */
public class CartDatabase extends SugarRecord<CartDatabase> {
    String data;

    public CartDatabase() {
    }

    public CartDatabase(String data) {
        this.data = data;
    }

    public String getData() {
        return this.data;
    }

    public String toString() {
        return this.data.toString();
    }
}
