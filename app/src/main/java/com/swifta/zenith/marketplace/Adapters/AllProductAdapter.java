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
import android.widget.Toast;

import com.koushikdutta.ion.Ion;
import com.swifta.zenith.marketplace.Activities.BaseNavigationDrawerActivity;
import com.swifta.zenith.marketplace.Activities.HomeActivity;
import com.swifta.zenith.marketplace.Activities.ProductDetailsActivity;
import com.swifta.zenith.marketplace.Activities.SignInActivity;
import com.swifta.zenith.marketplace.Database.CartDatabase;
import com.swifta.zenith.marketplace.Fragments.AllProductFragment;
import com.swifta.zenith.marketplace.Fragments.NearProductsFragment;
import com.swifta.zenith.marketplace.Fragments.ProductCategoryListFragment;
import com.swifta.zenith.marketplace.R;
import com.swifta.zenith.marketplace.Utils.Dictionary;
import com.swifta.zenith.marketplace.Utils.JSONParser;
import com.swifta.zenith.marketplace.Utils.UnicodeConverter;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by moyinoluwa on 14-Aug-15.
 */
public class AllProductAdapter extends RecyclerView.Adapter<AllProductAdapter.ProductsViewHolder> {

    List<JSONParser> products;
    private LayoutInflater mInflater;
    private PopupMenu popupMenu;
    private Menu menu;
    private Context context;
    private int fragmentTag;

    public AllProductAdapter(Context context, List<JSONParser> products, int fragmentTag) {
        this.products = products;
        this.context = context;
        this.fragmentTag = fragmentTag;
        mInflater = LayoutInflater.from(context);
    }

    @Override
    public ProductsViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        final View view = mInflater.inflate(R.layout.products_recycler_view, parent, false);
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
                .load(products.get(position).getProperty(Dictionary.imageUrl).toString());

        // Loads the views with data from the JSON response
        holder.productName.setText(products.get(position).getProperty(Dictionary.dealTitle)
                .toString());

        holder.discount.setText(products.get(position).getProperty(Dictionary.productDiscount)
                .toString() + "% off");

        discount = products.get(position).getProperty(Dictionary.productDiscount).toString();
        oldPrice = UnicodeConverter.getConversionResult(products.get(position).getProperty(Dictionary.currencySymbol).toString())
                + products.get(position).getProperty("deal_price").toString();
        newPrice = UnicodeConverter.getConversionResult(products.get(position).getProperty(Dictionary.currencySymbol).toString()) +
                products.get(position).getProperty("deal_value").toString();

        comparePrices(discount, oldPrice, newPrice, holder.oldPrice, holder.newPrice);


        holder.linearLayout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent i = new Intent(context, ProductDetailsActivity.class);
                i.putExtra("product", products.get(position).toString());
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

                                    if (fragmentTag == 1) {
                                        AllProductFragment.displayWishlistCount();
                                    } else if (fragmentTag == 2) {
                                        ProductCategoryListFragment.displayWishlistCount();
                                    } else if (fragmentTag == 3) {
                                        NearProductsFragment.displayWishlistCount();
                                    }

                                    Snackbar.make(view, products.get(position).getProperty(Dictionary.dealTitle).toString()
                                            + context.getString(R.string.added_to_wishlist), Snackbar.LENGTH_SHORT).show();
                                    return true;
                                case 2:
                                    HomeActivity.compareCount += 1;

                                    if (fragmentTag == 1) {
                                        AllProductFragment.displayCompareCount();
                                    } else if (fragmentTag == 2) {
                                        ProductCategoryListFragment.displayCompareCount();
                                    } else if (fragmentTag == 3) {
                                        NearProductsFragment.displayCompareCount();
                                    }
                                    Snackbar.make(view, products.get(position).getProperty(Dictionary.dealTitle).toString()
                                            + context.getString(R.string.added_to_compare), Snackbar.LENGTH_SHORT).show();
                                    return true;
                                case 3:
                                    // Get the deal key of this product
                                    String dealKey = products.get(position).getProperty(Dictionary.dealKey).toString();
                                    long count = CartDatabase.count(CartDatabase.class, null, null);

                                    if (count == 0) {
                                        // If the cart is empty, add the product
                                        HomeActivity.cartCount += 1;

                                        if (fragmentTag == 1) {
                                            AllProductFragment.displayCartCount();
                                        } else if (fragmentTag == 2) {
                                            ProductCategoryListFragment.displayCartCount();
                                        } else if (fragmentTag == 3) {
                                            NearProductsFragment.displayCartCount();
                                        }

                                        CartDatabase mCartDatabase = new CartDatabase(products.get(position).toString());
                                        mCartDatabase.save();

                                        Snackbar.make(view, products.get(position).getProperty(Dictionary.dealTitle).toString()
                                                + context.getString(R.string.added_to_cart), Snackbar.LENGTH_SHORT).show();
                                    } else {
                                        if (containsDuplicateProduct(dealKey)) {
                                            Snackbar.make(view, products.get(position).getProperty(Dictionary.dealTitle).toString()
                                                    + " is already in your cart.", Snackbar.LENGTH_SHORT).show();
                                        } else {
                                            // Save the product to cart if there is no duplicate
                                            HomeActivity.cartCount += 1;

                                            if (fragmentTag == 1) {
                                                AllProductFragment.displayCartCount();
                                            } else if (fragmentTag == 2) {
                                                ProductCategoryListFragment.displayCartCount();
                                            } else if (fragmentTag == 3) {
                                                NearProductsFragment.displayCartCount();
                                            }

                                            CartDatabase mCartDatabase = new CartDatabase(products.get(position).toString());
                                            mCartDatabase.save();

                                            Snackbar.make(view, products.get(position).getProperty(Dictionary.dealTitle).toString()
                                                    + context.getString(R.string.added_to_cart), Snackbar.LENGTH_SHORT).show();
                                        }
                                    }
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
        return this.products.size();
    }

    /**
     * Animates the process of removing, adding or moving items around in the list
     **/
    public void animateTo(List<JSONParser> jsonParser) {
        applyAndAnimateRemovals(jsonParser);
        applyAndAnimateAdditions(jsonParser);
        applyAndAnimateMovedItems(jsonParser);
    }

    /**
     * Animates the process of removing items from the list
     **/
    private void applyAndAnimateRemovals(List<JSONParser> jsonParsers) {
        for (int i = products.size() - 1; i >= 0; i--) {
            final JSONParser model = this.products.get(i);
            if (!jsonParsers.contains(model)) {
                removeItem(i);
            }
        }
    }

    /**
     * Animates the process of adding items to the list
     **/
    private void applyAndAnimateAdditions(List<JSONParser> newModels) {
        for (int i = 0, count = newModels.size(); i < count; i++) {
            final JSONParser model = newModels.get(i);
            if (!this.products.contains(model)) {
                addItem(i, model);
            }
        }
    }

    /**
     * Animates the process of moving items about in the list
     **/
    private void applyAndAnimateMovedItems(List<JSONParser> newModels) {
        for (int toPosition = newModels.size() - 1; toPosition >= 0; toPosition--) {
            final JSONParser model = newModels.get(toPosition);
            final int fromPosition = this.products.indexOf(model);
            if (fromPosition >= 0 && fromPosition != toPosition) {
                moveItem(fromPosition, toPosition);
            }
        }
    }

    /**
     * Removes an item from the list and notifies the adapter
     **/
    public JSONParser removeItem(int position) {
        final JSONParser jsonParser = this.products.remove(position);
        notifyItemRemoved(position);
        return jsonParser;
    }

    /**
     * Adds a new item to the list and notifies the adapter
     **/
    public void addItem(int postion, JSONParser jsonParser) {
        this.products.add(postion, jsonParser);
        notifyItemInserted(postion);
    }

    /**
     * Changes the position of an item in the list and notifies the adapter
     **/
    public void moveItem(int fromPosition, int toPosition) {
        final JSONParser jsonParser = this.products.remove(fromPosition);
        this.products.add(toPosition, jsonParser);
        notifyItemMoved(fromPosition, toPosition);
    }

    /**
     * Ensures that when both old prices and new prices are equal(when there is no discount), they are not displayed
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

    /**
     * Compare it to the items already present in the Cart to check if it's a duplicate item
     **/
    private boolean containsDuplicateProduct(String dealKey) {
        boolean duplicateProduct = false;
        String cartDealKey;
        // Get the list of all items in the Cart Database
        List<CartDatabase> allProducts = CartDatabase.listAll(CartDatabase.class);
        List<JSONParser> cartList = new ArrayList<JSONParser>();

        // Search through the List
        for (int i = 0; i < allProducts.size(); i++) {
            try {
                String data = allProducts.get(i).getData();
                JSONObject parser = new JSONObject(data);

                cartList.add(new JSONParser(parser));

                // Finds the deal_key of all the products in the cart and compares it to the new one
                cartDealKey = String.valueOf(cartList.get(i).getProperty(Dictionary.dealKey).toString());

                if (dealKey.equals(cartDealKey)) {
                    duplicateProduct = true;
                    break;
                }
            } catch (JSONException ex) {
                ex.printStackTrace();
            }
        }
        return duplicateProduct;
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
