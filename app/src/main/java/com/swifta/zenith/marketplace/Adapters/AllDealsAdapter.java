package com.swifta.zenith.marketplace.Adapters;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Paint;
import android.os.CountDownTimer;
import android.support.design.widget.Snackbar;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.PopupMenu;
import android.widget.TextView;

import com.koushikdutta.ion.Ion;
import com.swifta.zenith.marketplace.Activities.AllDealsActivity;
import com.swifta.zenith.marketplace.Activities.BaseNavigationDrawerActivity;
import com.swifta.zenith.marketplace.Activities.DealsDetailsActivity;
import com.swifta.zenith.marketplace.Activities.HomeActivity;
import com.swifta.zenith.marketplace.Activities.SignInActivity;
import com.swifta.zenith.marketplace.R;
import com.swifta.zenith.marketplace.Utils.JSONParser;
import com.swifta.zenith.marketplace.Utils.Timer;
import com.swifta.zenith.marketplace.Utils.Dictionary;

import java.text.ParseException;
import java.util.List;

/**
 * Created by moyinoluwa on 14-Aug-15.
 */
public class AllDealsAdapter extends RecyclerView.Adapter<AllDealsAdapter.ProductsViewHolder> {

    List<JSONParser> deals;
    private PopupMenu popupMenu;
    private Menu menu;
    private Context context;

    public AllDealsAdapter(Context context, List<JSONParser> deals) {
        this.deals = deals;
        this.context = context;
    }

    @Override
    public ProductsViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        final View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.deals_recycler_view, parent, false);
        final ProductsViewHolder productsViewHolder = new ProductsViewHolder(view);

        return productsViewHolder;
    }

    @Override
    public void onAttachedToRecyclerView(RecyclerView recyclerView) {
        super.onAttachedToRecyclerView(recyclerView);
    }

    @Override
    public void onBindViewHolder(final ProductsViewHolder holder, final int position) {
        String oldPrice;
        String newPrice;

        /**
         * Loads the image directly from the server with the Ion Library
         */
        Ion.with(holder.imageView)
                .placeholder(R.drawable.home_background)
                .load(deals.get(position).getProperty(Dictionary.imageUrl).toString());

        // Loads the views with data from the JSON response
        holder.productName.setText(deals.get(position).getProperty(Dictionary.dealTitle)
                .toString());
        // Gets the date in strings and converts it to a usable format
        String endDate = deals.get(0).getProperty("enddate").toString();

        // Sets the timer to the difference between the current date and the end date;
        try {
            new CountDownTimer(Timer.getDateDifference(endDate), 1000) {
                @Override
                public void onTick(long millisUntilFinished) {
                    // Displays the time left in days, hours and seconds
                    holder.dealTime.setText(Timer.calculateTime(millisUntilFinished / 1000));
                }

                @Override
                public void onFinish() {
                    holder.dealTime.setText("00:00:00");
                }
            }.start();
        } catch (ParseException e1) {
            e1.printStackTrace();
        }

        holder.discount.setText(deals.get(position).getProperty(Dictionary.productDiscount)
                .toString() + "% off");

        oldPrice = deals.get(position).getProperty(Dictionary.currencySymbol)
                .toString() + deals.get(position).getProperty("deal_price").toString();
        newPrice = deals.get(position).getProperty(Dictionary.currencySymbol).toString() +
                deals.get(position).getProperty("deal_value").toString();

        comparePrices(oldPrice, newPrice, holder.oldPrice, holder.newPrice);


        holder.linearLayout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent i = new Intent(context, DealsDetailsActivity.class);
                i.putExtra("deals", deals.get(position).toString());
                context.startActivity(i);
            }
        });

        holder.popupMenuAnchor.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(final View view) {
                popupMenu = new PopupMenu(context, holder.popupMenuAnchor);
                popupMenu.getMenu().add(Menu.NONE, 1, Menu.NONE, "Add to wishlist");
                popupMenu.getMenu().add(Menu.NONE, 2, Menu.NONE, "Add to compare");
                popupMenu.getMenu().add(Menu.NONE, 3, Menu.NONE, "Add to cart");
                popupMenu.setOnMenuItemClickListener(new PopupMenu.OnMenuItemClickListener() {
                    @Override
                    public boolean onMenuItemClick(MenuItem menuItem) {

                        if (!BaseNavigationDrawerActivity.SIGNED_IN) {
                            new AlertDialog.Builder(context)
                                    .setMessage("Please sign in first.")
                                    .setCancelable(true)
                                    .setPositiveButton("Sign In", new DialogInterface.OnClickListener() {
                                        @Override
                                        public void onClick(DialogInterface dialogInterface, int i) {
                                            Intent intent = new Intent(context, SignInActivity.class);
                                            context.startActivity(intent);
                                        }
                                    })
                                    .setNeutralButton("Cancel", new DialogInterface.OnClickListener() {
                                        @Override
                                        public void onClick(DialogInterface dialog, int which) {
                                            dialog.dismiss();
                                        }
                                    })
                                    .show();
                            return true;
                        } else {
                            switch (menuItem.getItemId()) {
                                case 1:
                                    HomeActivity.wishlistCount += 1;
                                    AllDealsActivity.displayWishlistCount();
                                    Snackbar.make(view, deals.get(position).getProperty(Dictionary.dealTitle).toString()
                                            + context.getString(R.string.added_to_wishlist), Snackbar.LENGTH_SHORT).show();
                                    return true;
                                case 2:
                                    HomeActivity.compareCount += 1;
                                    AllDealsActivity.displayCompareCount();
                                    Snackbar.make(view, deals.get(position).getProperty(Dictionary.dealTitle).toString()
                                            + context.getString(R.string.added_to_compare), Snackbar.LENGTH_SHORT).show();
                                    return true;
                                case 3:
                                    HomeActivity.cartCount += 1;
                                    AllDealsActivity.displayCartCount();
                                    Snackbar.make(view, deals.get(position).getProperty(Dictionary.dealTitle).toString()
                                            + context.getString(R.string.added_to_cart), Snackbar.LENGTH_SHORT).show();
                                    return true;
                                default:
                                    return false;
                            }
                        }
                    }
                });
                popupMenu.show();
            }
        });
    }

    @Override
    public int getItemCount() {
        return this.deals.size();
    }

    /**
     * Ensures that both old prices and new prices are not displayed they not equal
     **/
    private void comparePrices(String oldPrice, String newPrice,
                               TextView oldPriceText, TextView newPriceText) {
        if (oldPrice.equals(newPrice)) {
            oldPriceText.setVisibility(View.GONE);
            newPriceText.setText(newPrice);
        } else {
            oldPriceText.setText(oldPrice);
            oldPriceText.setPaintFlags(oldPriceText.getPaintFlags() | Paint.STRIKE_THRU_TEXT_FLAG);
            newPriceText.setText(newPrice);
        }
    }

    public static class ProductsViewHolder extends RecyclerView.ViewHolder {
        ImageView imageView;
        TextView productName;
        TextView dealTime;
        TextView discount;
        TextView oldPrice;
        TextView newPrice;
        ImageButton popupMenuAnchor;
        LinearLayout linearLayout;

        ProductsViewHolder(View itemView) {
            super(itemView);
            imageView = (ImageView) itemView.findViewById(R.id.image_view);
            productName = (TextView) itemView.findViewById(R.id.deals_name);
            dealTime = (TextView) itemView.findViewById(R.id.deals_time);
            discount = (TextView) itemView.findViewById(R.id.discount);
            oldPrice = (TextView) itemView.findViewById(R.id.old_price);
            newPrice = (TextView) itemView.findViewById(R.id.new_price);
            popupMenuAnchor = (ImageButton) itemView.findViewById(R.id.popupmenu);
            linearLayout = (LinearLayout) itemView.findViewById(R.id.linearLayout);
        }

    }

}
