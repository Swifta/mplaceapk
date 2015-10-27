package com.swifta.zenith.marketplace.Database;

import com.orm.SugarRecord;

/**
 * Created by moyinoluwa on 10/15/15.
 */
public class ShippingAddressDatabase extends SugarRecord {
    String shippingUser;
    String shippingAddress1;
    String shippingAddress2;
    String shippingState;
    String shippingCountryId;
    String shippingCityId;
    String shippingMobileNo;
    String shippingZipcode;
    String shippingCountryName;
    String shippingCityName;


    public ShippingAddressDatabase() {

    }

    public ShippingAddressDatabase(String shippingUser, String shippingAddress1, String shippingAddress2, String shippingState, String shippingCountryId, String shippingCityId, String shippingMobileNo, String shippingZipcode, String shippingCountryName, String shippingCityName) {
        this.shippingUser = shippingUser;
        this.shippingAddress1 = shippingAddress1;
        this.shippingAddress2 = shippingAddress2;
        this.shippingState = shippingState;
        this.shippingCountryId = shippingCountryId;
        this.shippingCityId = shippingCityId;
        this.shippingMobileNo = shippingMobileNo;
        this.shippingZipcode = shippingZipcode;
        this.shippingCountryName = shippingCountryName;
        this.shippingCityName = shippingCityName;
    }

    public String getShippingUser() {
        return shippingUser;
    }

    public void setShippingUser(String shippingUser) {
        this.shippingUser = shippingUser;
    }

    public String getShippingAddress1() {
        return shippingAddress1;
    }

    public void setShippingAddress1(String shippingAddress1) {
        this.shippingAddress1 = shippingAddress1;
    }

    public String getShippingAddress2() {
        return shippingAddress2;
    }

    public void setShippingAddress2(String shippingAddress2) {
        this.shippingAddress2 = shippingAddress2;
    }

    public String getShippingState() {
        return shippingState;
    }

    public void setShippingState(String shippingState) {
        this.shippingState = shippingState;
    }

    public String getShippingCountryId() {
        return shippingCountryId;
    }

    public void setShippingCountryId(String shippingCountryId) {
        this.shippingCountryId = shippingCountryId;
    }

    public String getShippingCityId() {
        return shippingCityId;
    }

    public void setShippingCityId(String shippingCityId) {
        this.shippingCityId = shippingCityId;
    }

    public String getShippingMobileNo() {
        return shippingMobileNo;
    }

    public void setShippingMobileNo(String shippingMobileNo) {
        this.shippingMobileNo = shippingMobileNo;
    }

    public String getShippingZipcode() {
        return shippingZipcode;
    }

    public void setShippingZipcode(String shippingZipcode) {
        this.shippingZipcode = shippingZipcode;
    }

    public String getShippingCountryName() {
        return shippingCountryName;
    }

    public void setShippingCountryName(String shippingCountryName) {
        this.shippingCountryName = shippingCountryName;
    }

    public String getShippingCityName() {
        return shippingCityName;
    }

    public void setShippingCityName(String shippingCityName) {
        this.shippingCityName = shippingCityName;
    }
}

