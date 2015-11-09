package com.swifta.zenith.marketplace.Adapters;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.design.widget.Snackbar;
import android.support.v7.widget.CardView;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
import com.koushikdutta.async.future.FutureCallback;
import com.koushikdutta.ion.Ion;
import com.swifta.zenith.marketplace.Activities.SubCategoryActivity;
import com.swifta.zenith.marketplace.R;
import com.swifta.zenith.marketplace.Utils.Dictionary;
import com.swifta.zenith.marketplace.Utils.JSONParser;
import com.swifta.zenith.marketplace.Utils.NetworkConnection;
import com.swifta.zenith.marketplace.Utils.Utility;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by moyinoluwa on 24-Aug-15.
 */
public class CategoryAdapter extends RecyclerView.Adapter<CategoryAdapter.CategoryViewHolder> {

    List<JSONParser> category;
    ArrayList<JSONParser> subCategoryList = new ArrayList<JSONParser>();
    ArrayList<String> subCategoryName = new ArrayList<>();
    ArrayList<String> subCategoryId = new ArrayList<>();
    ArrayList<String> countProduct = new ArrayList<>();
    private Context context;
    String categoryId;
    String categoryName;
    String fragmentName;
    private View view;
    NetworkConnection networkConnection;

    public CategoryAdapter(Context context, List<JSONParser> category, String fragmentName) {
        this.category = category;
        this.context = context;
        this.fragmentName = fragmentName;
    }

    @Override
    public CategoryViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.category_recycler_view, parent, false);
        CategoryViewHolder categoryViewHolder = new CategoryViewHolder(view);
        networkConnection = new NetworkConnection(context);

        return categoryViewHolder;
    }

    @Override
    public void onBindViewHolder(CategoryViewHolder holder, final int position) {
        holder.categoryName.setText(category.get(position)
                .getProperty(Dictionary.categoryName).toString());
        holder.categoryCount.setText("(" + category.get(position)
                .getProperty(Dictionary.countProduct).toString() + ")");

        holder.categoryCard.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View view) {
                categoryId = category.get(position)
                        .getProperty(Dictionary.categoryId).toString();
                categoryName = category.get(position)
                        .getProperty(Dictionary.categoryName).toString();

                initializeCategories(categoryId);
            }
        });
    }

    @Override
    public int getItemCount() {
        return this.category.size();
    }

    public static class CategoryViewHolder extends RecyclerView.ViewHolder {
        TextView categoryName;
        TextView categoryCount;
        CardView categoryCard;

        CategoryViewHolder(View itemView) {
            super(itemView);
            categoryName = (TextView) itemView.findViewById(R.id.category_name);
            categoryCount = (TextView) itemView.findViewById(R.id.category_count);
            categoryCard = (CardView) itemView.findViewById(R.id.category_cardview);
        }

    }

    /**
     * Gets data from the APIs using the Ion Library
     */
    private void initializeCategories(String categoryId) {
        String subCategory = null;

        subCategoryList.clear();
        subCategoryId.clear();
        subCategoryName.clear();
        countProduct.clear();

        // Gets the name of the fragment and updates the url based on that
        switch (fragmentName) {
            case ("ProductCategoryFragment"):
                subCategory = categoryId + "/product/13.html";
                break;
            case ("DealsCategoryFragment"):
                subCategory = categoryId + "/deal/13.html";
                break;
            case ("AuctionsCategoryFragment"):
                subCategory = categoryId + "/auction/13.html";
                break;

        }

        if (networkConnection.isInternetOn()) {
            Ion.with(context)
                    .load(Utility.HOST_VALUE + Utility.SUB_CATEGORY_PATH_VALUE + subCategory)
                    .asJsonObject()
                    .setCallback(new FutureCallback<JsonObject>() {
                        @Override
                        public void onCompleted(Exception e, JsonObject result) {

                            if (e == null) {
                                JsonArray resultArray = result.get("categorylist").getAsJsonArray();
                                JsonObject resultObject = resultArray.get(0).getAsJsonObject();
                                JsonObject result_inner = resultObject.get("category_list").getAsJsonObject();
                                int httpCode = result_inner.get("httpCode").getAsInt();

                                switch (httpCode) {
                                    case 200:
                                        for (int i = 0; i < resultArray.size(); i++) {
                                            resultObject = resultArray.get(i).getAsJsonObject();
                                            result_inner = resultObject.get("category_list").getAsJsonObject();

                                            try {
                                                JSONObject jsonObject = new JSONObject(result_inner.toString());
                                                subCategoryList.add(new JSONParser(jsonObject));

                                            } catch (JSONException exception) {
                                                exception.printStackTrace();
                                            }
                                        }

                                        for (int i = 0; i < subCategoryList.size(); i++) {
                                            subCategoryId.add(subCategoryList.get(i).getProperty(Dictionary.subCategoryId).toString());
                                            subCategoryName.add(subCategoryList.get(i).getProperty(Dictionary.subCategoryName).toString());
                                            countProduct.add(subCategoryList.get(i).getProperty(Dictionary.countProduct).toString());
                                        }

                                        createNewIntent(subCategoryId, subCategoryName, countProduct);

                                        break;
                                    case 401:
                                        Snackbar.make(view, result_inner.get("Message").getAsString(), Snackbar.LENGTH_LONG);
                                }
                            } else {
                                Snackbar.make(view, context.getString(R.string.unable_to_connect), Snackbar.LENGTH_LONG);
                            }
                        }
                    });
        } else {
            networkConnection.displayAlert();
        }
    }

    /**
     * Creates an intent to transfer data into new activity
     */
    private void createNewIntent(ArrayList<String> subCategoryId,
                                 ArrayList<String> subCategoryName,
                                 ArrayList<String> countProduct) {
        Intent intent = new Intent(context, SubCategoryActivity.class);

        Bundle bundle = new Bundle();
        bundle.putStringArrayList("sub-category-id", subCategoryId);
        bundle.putStringArrayList("sub-category-name", subCategoryName);
        bundle.putStringArrayList("count-product", countProduct);
        bundle.putString("category-name", categoryName);
        bundle.putString("fragment-name", fragmentName);

        intent.putExtras(bundle);

        context.startActivity(intent);
    }
}