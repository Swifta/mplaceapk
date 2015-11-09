package com.swifta.zenith.marketplace.Database;

import com.orm.SugarRecord;

/**
 * Created by moyinoluwa on 9/30/15.
 */
public class PriceDatabase extends SugarRecord {
    Double price;

    public PriceDatabase() {
    }

    public PriceDatabase(Double price) {
        this.price = price;
    }

    public Double getPrice() {
        return this.price;
    }

    public void setPrice(Double price) {
        this.price = price;
    }

}
