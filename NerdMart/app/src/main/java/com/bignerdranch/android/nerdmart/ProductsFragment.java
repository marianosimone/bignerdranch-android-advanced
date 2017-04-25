package com.bignerdranch.android.nerdmart;

import android.databinding.DataBindingUtil;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v7.widget.LinearLayoutManager;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import com.bignerdranch.android.nerdmart.databinding.FragmentProductsBinding;
import com.bignerdranch.android.nerdmartservice.service.payload.Product;

import java.util.Collections;

import rx.Observable;
import rx.Subscription;
import rx.schedulers.Schedulers;

public class ProductsFragment extends NerdMartAbstractFragment {

    private FragmentProductsBinding mFragmentProductsBinding;

    private ProductRecyclerViewAdapter mAdapter;

    @Override
    public View onCreateView(
            final @NonNull LayoutInflater inflater,
            final @Nullable ViewGroup container,
            final @Nullable Bundle savedInstanceState) {
        super.onCreateView(inflater, container, savedInstanceState);
        mFragmentProductsBinding = DataBindingUtil.inflate(inflater,
                R.layout.fragment_products, container, false);
        final ProductRecyclerViewAdapter.AddProductClickEvent addProductClickEvent
                = this::postProductToCart;
        mAdapter = new ProductRecyclerViewAdapter(
                Collections.emptyList(), getContext(), addProductClickEvent);
        setupAdapter();
        updateUI();
        return mFragmentProductsBinding.getRoot();
    }

    private void setupAdapter() {
        final LinearLayoutManager linearLayoutManager =
                new LinearLayoutManager(getContext());
        mFragmentProductsBinding.fragmentProductsRecyclerView
                .setLayoutManager(linearLayoutManager);
        mFragmentProductsBinding.fragmentProductsRecyclerView
                .setAdapter(mAdapter);
    }

    private void updateUI() {
        addSubscription(
                mNerdMartServiceManager
                        .getProducts()
                        .toList()
                        .compose(loadingTransformer())
                        .subscribe(products -> {
                            mAdapter.setProducts(products);
                            mAdapter.notifyDataSetChanged();
                        }));
    }

    private void postProductToCart(final @NonNull Product product) {
        final Observable<Boolean> cartSuccessObservable = mNerdMartServiceManager
                .postProductToCart(product)
                .compose(loadingTransformer())
                .cache();
        final Subscription cartUpdateNotificationObservable = cartSuccessObservable
                .subscribe(aBoolean -> {
                    final int message = aBoolean ? R.string.product_add_success_message :
                            R.string.product_add_failure_message;
                    Toast.makeText(getContext(), message, Toast.LENGTH_SHORT).show();
                });
        addSubscription(cartUpdateNotificationObservable);
        addSubscription(cartSuccessObservable.filter(aBoolean -> aBoolean)
                .subscribeOn(Schedulers.newThread())
                .flatMap(aBoolean -> mNerdMartServiceManager.getCart())
                .subscribe(cart -> {
                    ((NerdMartAbstractActivity) getActivity()).updateCartStatus(cart);
                    updateUI();
                }));
    }
}
