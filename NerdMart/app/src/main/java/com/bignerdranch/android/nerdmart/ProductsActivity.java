package com.bignerdranch.android.nerdmart;

import android.content.Context;
import android.content.Intent;
import android.support.v4.app.Fragment;
import android.os.Bundle;

public class ProductsActivity extends NerdMartAbstractActivity {

    public static Intent newIntent(Context context) {
        Intent intent = new Intent(context, ProductsActivity.class);
        return intent;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    protected Fragment getFragment() {
        return new ProductsFragment();
    }
}
