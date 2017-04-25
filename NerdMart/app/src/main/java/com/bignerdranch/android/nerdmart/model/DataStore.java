package com.bignerdranch.android.nerdmart.model;

import android.support.annotation.Nullable;

import com.bignerdranch.android.nerdmartservice.service.payload.Cart;
import com.bignerdranch.android.nerdmartservice.service.payload.Product;
import com.bignerdranch.android.nerdmartservice.service.payload.User;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

public class DataStore {

    private User mCachedUser;

    private List<Product> mCachedProducts;

    private Cart mCachedCart;

    @Nullable
    public UUID getCachedAuthToken() {
        return mCachedUser.getAuthToken();
    }

    public void setCachedUser(final @Nullable User user) {
        mCachedUser = user;
    }

    @Nullable
    public User getCachedUser() {
        return mCachedUser;
    }

    public void setCachedProducts(final List<Product> products) {
        mCachedProducts = products;
    }

    public void setCachedCart(Cart cart) {
        mCachedCart = cart;
    }

    public Cart getCachedCart() {
        return mCachedCart;
    }

    public void clearCache() {
        mCachedProducts = new ArrayList<>();
        mCachedCart = null;
        mCachedUser = null;
    }
}
