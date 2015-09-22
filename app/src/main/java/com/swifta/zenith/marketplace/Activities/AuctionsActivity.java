package com.swifta.zenith.marketplace.Activities;

import android.content.Intent;
import android.os.Bundle;
import android.view.MenuItem;

import com.swifta.zenith.marketplace.Fragments.AuctionsCategoryFragment;
import com.swifta.zenith.marketplace.Fragments.ProductCategoryFragment;
import com.swifta.zenith.marketplace.R;

public class AuctionsActivity extends BaseNavigationDrawerActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        //Ignore: setContentView(R.layout.activity_home);

        // Selects the specific item in the Navigation View and checks it
        MenuItem hMenuItem = mNavigationView.getMenu().getItem(position);
        if (hMenuItem.isChecked()) {
            hMenuItem.setChecked(false);
        } else {
            hMenuItem.setChecked(true);
        }

        getLayoutInflater().inflate(R.layout.activity_auctions, mNestedScrollView);
        getSupportFragmentManager().beginTransaction()
                .replace(R.id.frameLayout, new AuctionsCategoryFragment())
                .commit();
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();

        Intent i = new Intent(AuctionsActivity.this, HomeActivity.class);
        startActivity(i);
    }
//    @Override
//   public boolean onCreateOptionsMenu(Menu menu) {
////        // Inflate the menu; this adds items to the action bar if it is present.
////        getMenuInflater().inflate(R.menu.menu_auctions, menu);
////        return true;
//    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

//        //noinspection SimplifiableIfStatement
//        if (id == R.id.action_settings) {
//            return true;
//        }

        return super.onOptionsItemSelected(item);
    }
}
