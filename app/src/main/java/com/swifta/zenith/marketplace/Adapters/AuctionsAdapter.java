package com.swifta.zenith.marketplace.Adapters;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
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
import com.swifta.zenith.marketplace.Activities.AuctionDetailsActivity;
import com.swifta.zenith.marketplace.Activities.BaseNavigationDrawerActivity;
import com.swifta.zenith.marketplace.Activities.HomeActivity;
import com.swifta.zenith.marketplace.Activities.SignInActivity;
import com.swifta.zenith.marketplace.R;
import com.swifta.zenith.marketplace.Utils.Dictionary;
import com.swifta.zenith.marketplace.Utils.JSONParser;
import com.swifta.zenith.marketplace.Utils.Timer;
import com.swifta.zenith.marketplace.Utils.UnicodeConverter;

import java.text.ParseException;
import java.util.List;

/**
 * Created by moyinoluwa on 26-Aug-15.
 */
public class AuctionsAdapter extends RecyclerView.Adapter<AuctionsAdapter.AuctionViewHolder> {
    List<JSONParser> auctions;
    private PopupMenu popupMenu;
    private Context context;
    private View view;

    public AuctionsAdapter(Context context, List<JSONParser> auctions) {
        this.auctions = auctions;
        this.context = context;
    }

    @Override
    public AuctionViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        view = LayoutInflater.from(parent.getContext()).inflate(R.layout.auctions_recycler_view, parent, false);
        AuctionViewHolder auctionViewHolder = new AuctionViewHolder(view);

        return auctionViewHolder;
    }

    @Override
    public void onAttachedToRecyclerView(RecyclerView recyclerView) {
        super.onAttachedToRecyclerView(recyclerView);
    }


    @Override
    public void onBindViewHolder(final AuctionViewHolder holder, final int position) {
        String newPrice;

        // Loads the image directly from the server with the Ion Library
        Ion.with(holder.imageView)
                .placeholder(R.drawable.home_background)
                .load(auctions.get(position).getProperty(Dictionary.imageUrl).toString());

        // Loads the views with data from the Deals List which contains the JSON response
        holder.productName.setText(auctions.get(position).getProperty(Dictionary.dealTitle).toString());

        // Gets the date in strings and converts it to a usable format
        String endDate = auctions.get(0).getProperty("enddate").toString();

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

        newPrice = UnicodeConverter.getConversionResult(auctions.get(position).getProperty(Dictionary.currencySymbol).toString())
                + auctions.get(position).getProperty("deal_value").toString();

        holder.newPrice.setText(newPrice);

        String lastBidder = auctions.get(position).getProperty(Dictionary.lastBidder).toString();
        holder.lastBidder.setText(lastBidder.equals("0") ? "Not Yet Bid" : lastBidder);

        holder.linearLayout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent i = new Intent(context, AuctionDetailsActivity.class);
                i.putExtra("auctions", auctions.get(position).toString());
                context.startActivity(i);
            }
        });

        holder.popupMenuAnchor.setOnClickListener(new View.OnClickListener() {

                                                      @Override
                                                      public void onClick(View v) {
                                                          popupMenu = new PopupMenu(context, holder.popupMenuAnchor);
                                                          popupMenu.getMenu().add(Menu.NONE, 1, Menu.NONE, "Bid now");
                                                          popupMenu.setOnMenuItemClickListener(
                                                                  new PopupMenu.OnMenuItemClickListener() {
                                                                      @Override
                                                                      public boolean onMenuItemClick(MenuItem menuItem) {

                                                                          if (!BaseNavigationDrawerActivity.SIGNED_IN) {
                                                                              new AlertDialog.Builder(context)
                                                                                      .setMessage(R.string.please_sign_in)
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
                                                                                      HomeActivity.displayWishlistCount();
                                                                                      Snackbar.make(view, auctions.get(position).getProperty(Dictionary.dealTitle).toString()
                                                                                              + context.getString(R.string.added_to_wishlist), Snackbar.LENGTH_SHORT).show();
                                                                                      return true;
                                                                                  default:
                                                                                      return false;
                                                                              }

                                                                          }

                                                                      }
                                                                  }

                                                          );

                                                          popupMenu.show();
                                                      }

                                                  }
        );
    }

    @Override
    public int getItemCount() {
        return this.auctions.size();
    }

    public static class AuctionViewHolder extends RecyclerView.ViewHolder {
        ImageView imageView;
        TextView productName;
        TextView dealTime;
        TextView lastBidder;
        TextView newPrice;
        ImageButton popupMenuAnchor;
        LinearLayout linearLayout;

        AuctionViewHolder(View itemView) {
            super(itemView);
            imageView = (ImageView) itemView.findViewById(R.id.image_view);
            productName = (TextView) itemView.findViewById(R.id.auction_name);
            dealTime = (TextView) itemView.findViewById(R.id.auction_time);
            lastBidder = (TextView) itemView.findViewById(R.id.last_bidder);
            newPrice = (TextView) itemView.findViewById(R.id.new_price);
            popupMenuAnchor = (ImageButton) itemView.findViewById(R.id.popupmenu);
            linearLayout = (LinearLayout) itemView.findViewById(R.id.linearLayout);
        }
    }


}
