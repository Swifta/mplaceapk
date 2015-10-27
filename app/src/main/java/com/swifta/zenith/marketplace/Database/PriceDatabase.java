package com.swifta.zenith.marketplace.Database;

import com.orm.SugarRecord;

/**
 * Created by moyinoluwa on 9/30/15.
 */
public class PriceDatabase extends SugarRecord {
    long price;

    public PriceDatabase() {
    }

    public PriceDatabase(long price) {
        this.price = price;
    }

    public long getPrice() {
        return this.price;
    }

    public void setPrice(long price) {
        this.price = price;
    }

}
