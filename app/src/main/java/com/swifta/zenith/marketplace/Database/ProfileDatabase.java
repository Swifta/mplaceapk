package com.swifta.zenith.marketplace.Database;

import com.orm.SugarRecord;

/**
 * Created by moyinoluwa on 10/19/15.
 */
public class ProfileDatabase extends SugarRecord {

    String email;
    String firstname;
    String lastname;
    String address1;
    String address2;
    String cityId;
    String cityName;
    String phone;
    String userImage;

    public ProfileDatabase() {
    }

    public ProfileDatabase(String email, String firstname, String lastname, String address1, String address2,
                           String cityId, String cityName, String phone, String userImage) {

        this.email = email;
        this.firstname = firstname;
        this.lastname = lastname;
        this.address1 = address1;
        this.address2 = address2;
        this.cityId = cityId;
        this.cityName = cityName;
        this.phone = phone;
        this.userImage = userImage;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getFirstname() {
        return firstname;
    }

    public void setFirstname(String firstname) {
        this.firstname = firstname;
    }

    public String getLastname() {
        return lastname;
    }

    public void setLastname(String lastname) {
        this.lastname = lastname;
    }

    public String getAddress1() {
        return address1;
    }

    public void setAddress1(String address1) {
        this.address1 = address1;
    }

    public String getAddress2() {
        return address2;
    }

    public void setAddress2(String address2) {
        this.address2 = address2;
    }

    public String getCityId() {
        return cityId;
    }

    public void setCityId(String cityId) {
        this.cityId = cityId;
    }

    public String getCityName() {
        return cityName;
    }

    public void setCityName(String cityName) {
        this.cityName = cityName;
    }

    public String getPhone() {
        return phone;
    }

    public void setPhone(String phone) {
        this.phone = phone;
    }

    public String getUserImage() {
        return userImage;
    }

    public void setUserImage(String userImage) {
        this.userImage = userImage;
    }
}
