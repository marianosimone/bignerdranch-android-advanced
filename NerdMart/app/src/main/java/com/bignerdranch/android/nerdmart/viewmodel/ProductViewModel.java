package com.bignerdranch.android.nerdmart.viewmodel;

import android.content.Context;
import android.databinding.BaseObservable;
import android.support.annotation.NonNull;

import com.bignerdranch.android.nerdmart.R;
import com.bignerdranch.android.nerdmartservice.service.payload.Product;

import java.text.NumberFormat;

public class ProductViewModel extends BaseObservable {

    private final Context mContext;

    private final Product mProduct;

    private final int mRowNumber;

    public ProductViewModel(
            final @NonNull Context context,
            final @NonNull Product product,
            final int rowNumber) {
        mContext = context;
        mProduct = product;
        mRowNumber = rowNumber;
    }

    public Integer getId() {
        return mProduct.getId();
    }

    public String getSKU() {
        return mProduct.getSKU();
    }

    public String getTitle() {
        return mProduct.getTitle();
    }

    public String getDescription() {
        return mProduct.getDescription();
    }

    public String getDisplayPrice() {
        return NumberFormat.getCurrencyInstance()
                .format(mProduct.getPriceInCents() / 100.0);
    }

    public String getProductUrl() {
        return mProduct.getProductUrl();
    }

    public String getProductQuantityDisplay() {
        return mContext.getString(R.string.quantity_display_text,
                mProduct.getBackendQuantity());
    }

    public int getRowColor() {
        int resourceId = mRowNumber % 2 == 0 ? R.color.white : R.color.light_blue;
        return mContext.getResources().getColor(resourceId);
    }
}