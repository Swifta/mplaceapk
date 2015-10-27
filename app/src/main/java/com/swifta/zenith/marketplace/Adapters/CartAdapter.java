package com.swifta.zenith.marketplace.Adapters;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.support.design.widget.TextInputLayout;
import android.support.v7.widget.CardView;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;

import com.koushikdutta.ion.Ion;
import com.swifta.zenith.marketplace.Activities.CartDetailsActivity;
import com.swifta.zenith.marketplace.Activities.HomeActivity;
import com.swifta.zenith.marketplace.Database.CartDatabase;
import com.swifta.zenith.marketplace.Database.PriceDatabase;
import com.swifta.zenith.marketplace.R;
import com.swifta.zenith.marketplace.Utils.Dictionary;
import com.swifta.zenith.marketplace.Utils.JSONParser;
import com.swifta.zenith.marketplace.Utils.UnicodeConverter;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by moyinoluwa on 9/28/15.
 */
public class CartAdapter extends RecyclerView.Adapter<CartAdapter.CartViewHolder> {
    List<JSONParser> cartList;
    Context context;
    Toolbar mToolbar;
    long deductedPrice;
    long updatedPrice;
    String quantity;
    ArrayList<Long> subTotalPriceArray = new ArrayList<Long>();

    public CartAdapter(Context context, List<JSONParser> carts, Toolbar mToolbar) {
        this.cartList = carts;
        this.context = context;
        this.mToolbar = mToolbar;
    }

    @Override
    public CartViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.cart_recycler_view, parent, false);
        CartViewHolder cartViewHolder = new CartViewHolder(view);

        return cartViewHolder;
    }

    @Override
    public void onBindViewHolder(final CartViewHolder holder, final int position) {

        // Loads the image directly from the server with the Ion Library
        Ion.with(holder.productImage)
                .placeholder(R.drawable.home_background)
                .load(cartList.get(position).getProperty(Dictionary.imageUrl).toString());

        holder.productName.setText(cartList.get(position).getProperty(Dictionary.dealTitle).toString());

        holder.productPrice.setText("Price: " + UnicodeConverter.getConversionResult(cartList.get(position).getProperty(Dictionary.currencySymbol).toString())
                + cartList.get(position).getProperty(Dictionary.dealValue).toString());

        deductedPrice = Long.valueOf(cartList.get(position).getProperty(Dictionary.dealValue).toString());
        subTotalPriceArray.add(position, deductedPrice);

        final List<PriceDatabase> priceDatabase = PriceDatabase.listAll(PriceDatabase.class);

        mToolbar.setSubtitle("Grand total = " + UnicodeConverter.getConversionResult(cartList.get(position).getProperty(Dictionary.currencySymbol).toString())
                + priceDatabase.get(0).getPrice());

        holder.shipping.setText("Shipping: " + cartList.get(position).getProperty(Dictionary.shippingMethod).toString());

        quantity = "1";
        setQuantity(quantity);

        // Sets the correct tense depending on the number of items present
        if (getQuantity().equals("0") || getQuantity().equals("1")) {
            holder.productQuantity.setText("Quantity: " + getQuantity() + " item");
        } else {
            holder.productQuantity.setText("Quantity: " + getQuantity() + " items");
        }

        holder.subTotalPrice.setText("Subtotal Price: "
                + UnicodeConverter.getConversionResult(cartList.get(position).getProperty(Dictionary.currencySymbol).toString())
                + cartList.get(position).getProperty(Dictionary.dealValue).toString());


        holder.delete.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                new AlertDialog.Builder(context)
                        .setTitle("Are you sure?")
                        .setMessage("Deleting this product will remove it from your cart.")
                        .setCancelable(true)
                        .setPositiveButton("OK", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialogInterface, int i) {

                                // Reduces the deleted item from the cart
                                int currentCount = HomeActivity.cartCount - 1;
                                if ((currentCount) <= 0) {
                                    HomeActivity.cartCount = 0;
                                } else {
                                    HomeActivity.cartCount -= 1;
                                }

                                cartList.remove(position);

                                deductGrandTotalPrice();
                                subTotalPriceArray.remove(position);

                                resetCartList(cartList);

                                Intent intent = new Intent(context, CartDetailsActivity.class);
                                context.startActivity(intent);
                            }
                        })
                        .setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialogInterface, int i) {
                                dialogInterface.dismiss();
                            }
                        })
                        .show();
            }
        });

        holder.update.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String price = cartList.get(position).getProperty(Dictionary.dealValue).toString();
                updateQuantityText(holder.productQuantity, holder.subTotalPrice, price, position);
            }
        });
    }

    /**
     * Resets the cart list based on the current items in the cart list
     **/
    private void resetCartList(List<JSONParser> cartList) {
        CartDatabase.deleteAll(CartDatabase.class);

        for (int count = 0; count < cartList.size(); count++) {
            CartDatabase mCartDatabase = new CartDatabase(cartList.get(count).toString());
            mCartDatabase.save();
        }
    }

    /**
     * Deducts the deleted price from the current total price and resets the price database
     **/
    private void deductGrandTotalPrice() {
        List<PriceDatabase> priceDatabaseList = PriceDatabase.listAll(PriceDatabase.class);
        deductedPrice = priceDatabaseList.get(0).getPrice() - deductedPrice;

        PriceDatabase.deleteAll(PriceDatabase.class);

        PriceDatabase priceDatabase = new PriceDatabase(deductedPrice);
        priceDatabase.save();
    }

    /**
     * Updates the the cart list based on the current items in the cart list
     **/
    private void updateGrandTotalPrice() {

        updatedPrice = 0;

        for (int i = 0; i < subTotalPriceArray.size(); i++) {
            updatedPrice += subTotalPriceArray.get(i);
        }

        PriceDatabase.deleteAll(PriceDatabase.class);

        PriceDatabase priceDatabases = new PriceDatabase(updatedPrice);
        priceDatabases.save();
    }

    private String getQuantity() {
        return this.quantity;
    }

    private void setQuantity(String quantity) {
        this.quantity = quantity;
    }

    /**
     * Creates a dialog to update the number of items for a given product
     **/
    private void updateQuantityText(final TextView quantityText, final TextView subTotalText, final String price, final int position) {
        // Inflates the dialog's layout
        View quantityView = View.inflate(context, R.layout.update_quantity_dialog, null);

        final EditText quantityEditText = (EditText) quantityView.findViewById(R.id.update_quantity);
        final TextInputLayout quantityTextInputLayout = (TextInputLayout) quantityView.findViewById(R.id.update_quantity_textinputlayout);

        AlertDialog.Builder builder = new AlertDialog.Builder(context);
        builder.setView(quantityView);
        builder.setTitle(R.string.update)
                .setCancelable(true)
                .setPositiveButton(R.string.ok, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        String updatedValue = quantityEditText.getText().toString();
                        if (updatedValue == null || updatedValue.equals("") || updatedValue.equals("0")) {
                            quantityTextInputLayout.setError(context.getString(R.string.update_error));
                        } else {
                            setQuantity(updatedValue);
                            quantityText.setText("Quantity: " + updatedValue + " items");
                            long subToTalPrice = Integer.valueOf(getQuantity()) *
                                    Integer.valueOf(price);
                            subTotalText.setText("Subtotal Price: " + UnicodeConverter.getConversionResult(cartList.get(position).getProperty(Dictionary.currencySymbol).toString())
                                    + String.valueOf(subToTalPrice));
                            subTotalPriceArray.set(position, subToTalPrice);

                            updateGrandTotalPrice();

                            mToolbar.setSubtitle("Grand total = " + UnicodeConverter.getConversionResult(cartList.get(position).getProperty(Dictionary.currencySymbol).toString())
                                    + updatedPrice);
                        }
                    }
                });
        AlertDialog alertDialog = builder.create();
        alertDialog.show();
    }

    @Override
    public int getItemCount() {
        return this.cartList.size();
    }

    public static class CartViewHolder extends RecyclerView.ViewHolder {
        ImageView productImage;
        TextView productName;
        TextView productPrice;
        TextView shipping;
        TextView productQuantity;
        TextView subTotalPrice;
        TextView delete;
        TextView update;
        CardView cartCardView;

        CartViewHolder(View itemView) {
            super(itemView);
            productImage = (ImageView) itemView.findViewById(R.id.product_image);
            productName = (TextView) itemView.findViewById(R.id.product_name);
            productPrice = (TextView) itemView.findViewById(R.id.product_price);
            shipping = (TextView) itemView.findViewById(R.id.shipping);
            productQuantity = (TextView) itemView.findViewById(R.id.product_quantity);
            subTotalPrice = (TextView) itemView.findViewById(R.id.sub_total_price);
            delete = (TextView) itemView.findViewById(R.id.delete);
            update = (TextView) itemView.findViewById(R.id.update);
            cartCardView = (CardView) itemView.findViewById(R.id.cart_card_view);
        }
    }

}


