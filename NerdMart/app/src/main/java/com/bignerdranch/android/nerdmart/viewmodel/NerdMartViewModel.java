package com.bignerdranch.android.nerdmart.viewmodel;

import android.content.Context;
import android.databinding.BaseObservable;
import android.databinding.Bindable;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;

import com.bignerdranch.android.nerdmart.BR;
import com.bignerdranch.android.nerdmart.R;
import com.bignerdranch.android.nerdmartservice.service.payload.Cart;
import com.bignerdranch.android.nerdmartservice.service.payload.User;

public class NerdMartViewModel extends BaseObservable {

    @NonNull
    private final Context mContext;

    @Nullable
    private Cart mCart;

    @NonNull
    private final User mUser;

    public NerdMartViewModel(
            final @NonNull Context context,
            final @Nullable Cart cart,
            final @NonNull User user) {
        mContext = context;
        mCart = cart;
        mUser = user;
    }

    private String formatCartItemsDisplay() {
        int numItems = 0;
        if (mCart != null && mCart.getProducts() != null) {
            numItems = mCart.getProducts().size();
        }
        return mContext.getResources().getQuantityString(R.plurals.cart,
                numItems, numItems);
    }

    public String getUserGreeting() {
        return mContext.getString(R.string.user_greeting, mUser.getName());
    }

    @Bindable
    public String getCartDisplay() {
        return formatCartItemsDisplay();
    }

    public void updateCartStatus(final @NonNull Cart cart) {
        mCart = cart;
        notifyPropertyChanged(BR.cartDisplay);
    }
}