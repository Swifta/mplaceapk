package com.swifta.zenith.marketplace.Activities;

import android.annotation.TargetApi;
import android.os.Build;
import android.os.Bundle;
import android.support.v4.content.ContextCompat;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.ImageView;
import android.widget.TextView;

import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.MapFragment;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;
import com.koushikdutta.ion.Ion;
import com.swifta.zenith.marketplace.R;
import com.swifta.zenith.marketplace.Utils.Dictionary;
import com.swifta.zenith.marketplace.Utils.JSONParser;

import org.json.JSONException;
import org.json.JSONObject;

public class StoreDetailsActivity extends BaseToolbarActivity implements OnMapReadyCallback {
    private View rootView;
    private Bundle bundle;
    private JSONParser store;
    private TextView storeName;
    private TextView storeType;
    private TextView storeAddress1;
    private TextView storeAddress2;
    private TextView storeCity;
    private TextView storeWebsite;
    private TextView storePhone;
    private ImageView storeImage;
    private MapFragment mapFragment;

    @TargetApi(Build.VERSION_CODES.LOLLIPOP)
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        //setContentView(R.layout.activity_store_details);
        rootView = getLayoutInflater().inflate(R.layout.activity_store_details, mNestedScrollView);

        // Sets the status bar color
        Window window = getWindow();
        window.addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS);
        window.clearFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS);
        if (android.os.Build.VERSION.SDK_INT >= 21) {
            window.setStatusBarColor(ContextCompat.getColor(this, R.color.colorPrimaryDarker));
        }

        storeImage = (ImageView) rootView.findViewById(R.id.image_view);
        storeName = (TextView) rootView.findViewById(R.id.store_name);
        storeType = (TextView) rootView.findViewById(R.id.store_type);
        storeAddress1 = (TextView) rootView.findViewById(R.id.store_address1);
        storeAddress2 = (TextView) rootView.findViewById(R.id.store_address2);
        storeCity = (TextView) rootView.findViewById(R.id.store_city);
        storePhone = (TextView) rootView.findViewById(R.id.store_phone);
        storeWebsite = (TextView) rootView.findViewById(R.id.store_website);
        mapFragment = (MapFragment) getFragmentManager()
                .findFragmentById(R.id.map_fragment);
        mapFragment.getMapAsync(this);

        // Populates the store details using bundles with
        // details from the store adapter
        bundle = getIntent().getExtras();
        // Confirms that bundle is not empty before usage
        if (bundle != null) {

            String data = null;

            // Checks if the there is data in any of the bundles
            if (bundle.getString("store") != null) {
                data = bundle.getString("store");
            } else if (bundle.getString("store_details") != null) {
                data = bundle.getString("store_details");
            }
            try {
                store = new JSONParser(new JSONObject(data));
            } catch (JSONException e) {
            }

            // Sets the Toolbar name
            getSupportActionBar().setTitle(store.getProperty(Dictionary.storeName).toString());

            // Loads the image into the imageview with  Ion
            Ion.with(storeImage)
                    .placeholder(R.drawable.home_background)
                    .load(store.getProperty("store_image").toString());

            // Sets the values for StoreDetails variables
            storeName.setText(store.getProperty(Dictionary.storeName).toString());
            storeType.setText(store.getProperty(Dictionary.storeType).toString());
            storeAddress1.setText(store.getProperty(Dictionary.storeAddress1).toString());
            // Confirms the second store address is not empty before using it
            if (store.getProperty(Dictionary.storeAddress2).toString() != null &&
                    store.getProperty(Dictionary.storeAddress2).toString().length() != 0) {
                // Confirms the second store address is not equal to the first store address
                if (!store.getProperty(Dictionary.storeAddress1).toString()
                        .equals(store.getProperty(Dictionary.storeAddress2).toString())) {
                    storeAddress2.setVisibility(View.VISIBLE);
                    storeAddress2.setText(store.getProperty(Dictionary.storeAddress2).toString());
                }
            }
            storeCity.setText(store.getProperty(Dictionary.cityName).toString());
            storePhone.setText(store.getProperty(Dictionary.phoneNumber).toString());
            storeWebsite.setText(store.getProperty(Dictionary.website).toString());
        }
    }

    @Override
    public void onMapReady(GoogleMap googleMap) {
        LatLng storeLocation = new LatLng(
                Double.valueOf(store.getProperty(Dictionary.longitude).toString()),
                Double.valueOf(store.getProperty(Dictionary.latitude).toString()));

        googleMap.setMyLocationEnabled(true);
        googleMap.moveCamera(CameraUpdateFactory.newLatLngZoom(storeLocation, 13));
        googleMap.addMarker(new MarkerOptions()
                .position(storeLocation)
                .snippet(store.getProperty(Dictionary.storeType).toString())
                .title(store.getProperty(Dictionary.storeName).toString()));
    }
}
