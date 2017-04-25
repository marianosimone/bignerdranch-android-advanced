package com.bignerdranch.android.nerdmart;

import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import timber.log.Timber;

public class ProductsFragment extends NerdMartAbstractFragment {

    @Override
    public View onCreateView(
            final @NonNull LayoutInflater inflater,
            final @Nullable ViewGroup container,
            final @Nullable Bundle savedInstanceState) {
        super.onCreateView(inflater, container, savedInstanceState);
        final View view =
                inflater.inflate(R.layout.fragment_products, container, false);
        updateUI();
        return view;
    }

    private void updateUI() {
        addSubscription(
                mNerdMartServiceManager
                        .getProducts()
                        .toList()
                        .compose(loadingTransformer())
                        .subscribe(products -> Timber.i("received products: " + products)));
    }
}
