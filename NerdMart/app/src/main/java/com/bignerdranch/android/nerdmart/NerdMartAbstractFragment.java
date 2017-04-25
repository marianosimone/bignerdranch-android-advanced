package com.bignerdranch.android.nerdmart;

import android.app.ProgressDialog;
import android.os.Bundle;
import android.support.annotation.CallSuper;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.bignerdranch.android.nerdmart.inject.Injector;
import com.bignerdranch.android.nerdmart.model.service.NerdMartServiceManager;

import javax.inject.Inject;

import rx.Observable;
import rx.Subscription;
import rx.subscriptions.CompositeSubscription;

public abstract class NerdMartAbstractFragment extends Fragment {

    @Inject
    NerdMartServiceManager mNerdMartServiceManager;

    private CompositeSubscription mCompositeSubscription;

    private ProgressDialog mDialog;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Injector.obtain(getContext()).inject(this);
    }

    @Override
    @Nullable
    @CallSuper
    public View onCreateView(
            final @NonNull LayoutInflater inflater,
            final @Nullable ViewGroup container,
            final @Nullable Bundle savedInstanceState) {
        mCompositeSubscription = new CompositeSubscription();
        setupLoadingDialog();
        return super.onCreateView(inflater, container, savedInstanceState);
    }

    @Override
    @CallSuper
    public void onDestroyView() {
        super.onDestroyView();
        mCompositeSubscription.clear();
    }

    protected void addSubscription(final @NonNull Subscription subscription) {
        mCompositeSubscription.add(subscription);
    }

    private void setupLoadingDialog() {
        mDialog = new ProgressDialog(getContext());
        mDialog.setIndeterminate(true);
        mDialog.setMessage(getString(R.string.loading_text));
    }

    protected <T> Observable.Transformer<T, T> loadingTransformer() {
        return observable -> observable.doOnSubscribe(mDialog::show)
                .doOnCompleted(() -> {
                    if (mDialog != null && mDialog.isShowing()) {
                        mDialog.dismiss();
                    }
                });
    }
}
