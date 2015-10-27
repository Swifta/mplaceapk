package com.swifta.zenith.marketplace.Utils;

/**
 * Created by moyinoluwa on 20-Aug-15.
 */
public class Utility {
    // Local server address - http://192.168.1.127 http://ecommerce-g0k7am7h.cloudapp.net/marketplace/
    // http://ecommerce-g0k7am7h.cloudapp.net
    //http://intranet.swifta.com
    public static final String HOST_VALUE = "http://zenithmktdemo03.cloudapp.net";

    // Blog path
    public static final String BLOG_VALUE = HOST_VALUE + "/blog";

    // Registration paths
    public static final String SIGN_IN_PATH_VALUE = "/api/users/login.html";
    public static final String FORGOT_PASSWORD_PATH_VALUE = "/api/users/forgot-password.html";
    public static final String REGISTER_PATH_VALUE = "/api/users/registration.html";
    public static final String CITY_PATH_VALUE = "/api/city-list/en.html";

    // Category paths
    public static final String PRODUCT_CATEGORY_PATH_VALUE = "/api/category-list/en/product/";
    public static final String DEALS_CATEGORY_PATH_VALUE = "/api/category-list/en/deal/";
    public static final String AUCTIONS_CATEGORY_PATH_VALUE = "/api/category-list/en/auction/";

    // SubCategory paths
    public static final String SUB_CATEGORY_PATH_VALUE = "/api/sub_category_list/en/";

    // Product Category List paths
    public static final String PRODUCT_CATEGORY_LIST_PATH_VALUE = "/api/products-category-listing/en/";
    public static final String DEALS_CATEGORY_LIST_PATH_VALUE = "/api/deals-category-listing/en/";
    public static final String AUCTIONS_CATEGORY_LIST_PATH_VALUE = "/api/auctions-category-listing/en/";

    // All Products path
    public static final String ALL_PRODUCTS_PATH_VALUE = "/api/product_listing/en.html";
    public static final String ALL_DEALS_PATH_VALUE = "/api/deals_listing/en.html";
    public static final String ALL_AUCTIONS_PATH_VALUE = "/api/auction_listing/en.html";

    // Hot paths
    public static final String HOT_PRODUCTS_PATH_VALUE = "/api/hot_product_listing/en/hot.html";
    public static final String HOT_DEALS_PATH_VALUE = "/api/hot_deals_listing/en/hot.html";
    public static final String HOT_AUCTIONS_PATH_VALUE = "/api/hot_auction_listing/en/hot.html";

    // Near paths
    public static final String NEAR_PRODUCTS_PATH_VALUE = "/api/geo-products/en/";
    public static final String NEAR_DEALS_PATH_VALUE = "/api/geo-deals/en/";
    public static final String NEAR_AUCTIONS_PATH_VALUE = "/api/geo-auctions/en/";

    // Product details
    public static final String PRODUCTS_DETAILS = "/api/product-details/en/";
    public static final String DEALS_DETAILS = "/api/deal-details/en/";
    public static final String AUCTIONS_DETAILS = "/api/auction-details/en/";

    // Similar paths
    public static final String SIMILAR_PRODUCT_BY_PRODUCTS_PATH_VALUE = "/api/similar_product_by_products/en/";
    public static final String SIMILAR_DEALS_BY_DEAL_PATH_VALUE = "/api/similar-deals-by-deal/en/";
    public static final String SIMILAR_AUCTIONS_PATH_VALUE = "/api/similar_auctions/en";

    // Store list
    public static final String STORE_LIST_PATH_VALUE = "/api/store_list/en.html";

    // Shipping
    public static final String SHIPPING_PATH_VALUE = "/api/users/shipping/";
    public static final String EDIT_SHIPPING_PATH_VALUE = "/api/users/edit-shipping-address.html";

    // Profile
    public static final String PROFILE_PATH_VALUE = "/api/users/profile/";
}
