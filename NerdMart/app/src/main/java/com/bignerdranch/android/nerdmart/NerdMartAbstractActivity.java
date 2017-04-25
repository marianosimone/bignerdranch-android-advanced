package com.bignerdranch.android.nerdmart;

import android.content.Intent;
import android.databinding.DataBindingUtil;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v4.app.Fragment;
import android.support.v7.app.AppCompatActivity;

import com.bignerdranch.android.nerdmart.databinding.ActivityNerdmartAbstractBinding;
import com.bignerdranch.android.nerdmart.inject.Injector;
import com.bignerdranch.android.nerdmart.model.service.NerdMartServiceManager;
import com.bignerdranch.android.nerdmart.viewmodel.NerdMartViewModel;
import com.bignerdranch.android.nerdmartservice.service.payload.Cart;

import javax.inject.Inject;

import rx.Subscription;
import rx.subscriptions.CompositeSubscription;

public abstract class NerdMartAbstractActivity extends AppCompatActivity {

    private CompositeSubscription mCompositeSubscription;

    @Inject
    NerdMartServiceManager mNerdMartServiceManager;

    @Inject
    NerdMartViewModel mNerdMartViewModel;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mCompositeSubscription = new CompositeSubscription();
        Injector.obtain(this).inject(this);
        ActivityNerdmartAbstractBinding binding = DataBindingUtil
                .setContentView(this, R.layout.activity_nerdmart_abstract);
        binding.setLogoutButtonClickListener(v -> signOut());
        binding.setNerdMartViewModel(mNerdMartViewModel);
        if (savedInstanceState == null) {
            getSupportFragmentManager().beginTransaction()
                    .add(binding.activityAbstractNerdmartFragmentFrame.getId(),
                            getFragment())
                    .commit();
        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        mCompositeSubscription.clear();
    }

    protected void addSubscription(final @NonNull Subscription subscription) {
        mCompositeSubscription.add(subscription);
    }

    private void signOut() {
        addSubscription(mNerdMartServiceManager
                .signOut()
                .subscribe(aBoolean -> {
                    final Intent intent = new Intent(this, LoginActivity.class);
                    intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK |
                            Intent.FLAG_ACTIVITY_CLEAR_TASK);
                    startActivity(intent);
                }));
    }

    public void updateCartStatus(final @NonNull Cart cart) {
        mNerdMartViewModel.updateCartStatus(cart);
    }

    protected abstract Fragment getFragment();
}
