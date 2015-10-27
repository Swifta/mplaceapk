package com.swifta.zenith.marketplace.Adapters;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Paint;
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
import com.swifta.zenith.marketplace.Activities.BaseNavigationDrawerActivity;
import com.swifta.zenith.marketplace.Activities.HomeActivity;
import com.swifta.zenith.marketplace.Activities.ProductDetailsActivity;
import com.swifta.zenith.marketplace.Activities.SignInActivity;
import com.swifta.zenith.marketplace.Database.CartDatabase;
import com.swifta.zenith.marketplace.R;
import com.swifta.zenith.marketplace.Utils.Dictionary;
import com.swifta.zenith.marketplace.Utils.JSONParser;
import com.swifta.zenith.marketplace.Utils.UnicodeConverter;

import java.util.List;

/**
 * Created by moyinoluwa on 14-Aug-15.
 */
public class SimilarProductsAdapter extends RecyclerView.Adapter<SimilarProductsAdapter.ProductsViewHolder> {

    List<JSONParser> products;
    private PopupMenu popupMenu;
    private Menu menu;
    private Context context;

    public SimilarProductsAdapter(Context context, List<JSONParser> products) {
        this.products = products;
        this.context = context;
    }

    @Override
    public ProductsViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        final View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.products_recycler_view, parent, false);
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
        String discount;

        /**
         * Loads the image directly from the server with the Ion Library
         */
        Ion.with(holder.imageView)
                .placeholder(R.drawable.home_background)
                .load(products.get(position).getProperty("image_url").toString());

        // Loads the views with data from the JSON response
        holder.productName.setText(products.get(position).getProperty("product_title")
                .toString());
        holder.discount.setText(products.get(position).getProperty("product_discount")
                .toString() + "% off");

        discount = products.get(position).getProperty(Dictionary.productDiscount).toString();
        oldPrice = UnicodeConverter.getConversionResult(products.get(position).getProperty(Dictionary.currencySymbol)
                .toString()) + products.get(position).getProperty("product_price").toString();
        newPrice = UnicodeConverter.getConversionResult(products.get(position).getProperty(Dictionary.currencySymbol).toString()) +
                products.get(position).getProperty("product_value").toString();

        comparePrices(discount, oldPrice, newPrice, holder.oldPrice, holder.newPrice);


        holder.linearLayout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent i = new Intent(context, ProductDetailsActivity.class);
                i.putExtra("product", products.get(position).toString());
                context.startActivity(i);
            }
        });

        holder.popupMenuAnchor.setOnClickListener(
                new View.OnClickListener() {

                    @Override
                    public void onClick(final View view) {
                        popupMenu = new PopupMenu(context, holder.popupMenuAnchor);
                        popupMenu.getMenu().add(Menu.NONE, 1, Menu.NONE, "Add to wishlist");
                        popupMenu.getMenu().add(Menu.NONE, 2, Menu.NONE, "Add to compare");
                        popupMenu.getMenu().add(Menu.NONE, 3, Menu.NONE, "Add to cart");
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
                                                    ProductDetailsActivity.displayWishlistCount();
                                                    Snackbar.make(view, products.get(position).getProperty("product_title").toString()
                                                            + context.getString(R.string.added_to_wishlist), Snackbar.LENGTH_SHORT).show();
                                                    return true;
                                                case 2:
                                                    HomeActivity.compareCount += 1;
                                                    ProductDetailsActivity.displayCompareCount();
                                                    Snackbar.make(view, products.get(position).getProperty("product_title").toString()
                                                            + context.getString(R.string.added_to_compare), Snackbar.LENGTH_SHORT).show();
                                                    return true;
                                                case 3:
                                                    HomeActivity.cartCount += 1;
                                                    ProductDetailsActivity.displayCartCount();

                                                    // Creates a new CartDatabase instance with Sugar and saves an ArrayList in it
                                                    CartDatabase mDatabase = new CartDatabase(products.get(position).toString());
                                                    mDatabase.save();

                                                    Snackbar.make(view, products.get(position).getProperty("product_title").toString()
                                                            + context.getString(R.string.added_to_cart), Snackbar.LENGTH_SHORT).show();
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
        return this.products.size();
    }

    /**
     * Ensures that both old prices and new prices are not displayed they not equal
     **/
    private void comparePrices(String discount, String oldPrice, String newPrice,
                               TextView oldPriceText, TextView newPriceText) {
        if (discount.equals("0")) {
            oldPriceText.setText("");
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
        TextView discount;
        TextView oldPrice;
        TextView newPrice;
        ImageButton popupMenuAnchor;
        LinearLayout linearLayout;

        ProductsViewHolder(View itemView) {
            super(itemView);
            imageView = (ImageView) itemView.findViewById(R.id.image_view);
            productName = (TextView) itemView.findViewById(R.id.product_name);
            discount = (TextView) itemView.findViewById(R.id.discount);
            oldPrice = (TextView) itemView.findViewById(R.id.old_price);
            newPrice = (TextView) itemView.findViewById(R.id.new_price);
            popupMenuAnchor = (ImageButton) itemView.findViewById(R.id.popupmenu);
            linearLayout = (LinearLayout) itemView.findViewById(R.id.linearLayout);
        }
    }
}
