package com.bignerdranch.android.nerdmail.controller;

import android.app.ProgressDialog;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.bignerdranch.android.nerdmail.R;
import com.bignerdranch.android.nerdmail.inject.Injector;
import com.bignerdranch.android.nerdmail.model.DataManager;
import com.bignerdranch.android.nerdmail.view.EmailAdapter;
import com.bignerdranch.android.nerdmailservice.Email;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import javax.inject.Inject;

import rx.Observable;
import rx.Subscription;
import rx.subscriptions.CompositeSubscription;
import timber.log.Timber;

public abstract class EmailListFragment extends Fragment {

    private RecyclerView mEmailRecyclerView;

    @Inject
    DataManager mDataManager;

    private CompositeSubscription mCompositeSubscription;
    private ProgressDialog mProgressDialog;

    @Override
    public View onCreateView(
            final @NonNull LayoutInflater inflater,
            final @Nullable ViewGroup parent,
            final @Nullable Bundle savedInstanceState) {
        Injector.obtain(getContext()).inject(this);
        mCompositeSubscription = new CompositeSubscription();
        View view = inflater.inflate(R.layout.fragment_email_list, parent, false);
        setupLoadingDialog();

        mEmailRecyclerView = (RecyclerView)
                view.findViewById(R.id.email_recycler_view);
        mEmailRecyclerView.setLayoutManager(new LinearLayoutManager(getContext()));
        mEmailRecyclerView.setAdapter(
                new EmailAdapter(getContext(), Collections.emptyList()));

        return view;
    }

    @Override
    public void onResume() {
        super.onResume();
        updateUI();
        startEmailNotifications();
    }

    @Override
    public void onPause() {
        super.onPause();
        stopEmailNotifications();
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        mCompositeSubscription.clear();
    }

    private void setupAdapter(List<Email> emails) {
        mEmailRecyclerView.setAdapter(new EmailAdapter(getContext(), emails));
    }

    private void updateUI() {
        List<Email> emailList = new ArrayList<>();
        addSubscription(getEmailList()
                .compose(loadingTransformer())
                .subscribe(emailList::add,
                        e -> Timber.e(e, "got error"),
                        () -> setupAdapter(emailList)));
    }

    protected <T> Observable.Transformer<T, T> loadingTransformer() {
        return observable -> observable.doOnSubscribe(mProgressDialog::show)
                .doOnCompleted(() -> {
                    if (mProgressDialog != null && mProgressDialog.isShowing()) {
                        mProgressDialog.dismiss();
                    }
                });
    }

    private void setupLoadingDialog() {
        mProgressDialog = new ProgressDialog(getContext());
        mProgressDialog.setIndeterminate(true);
        mProgressDialog.setMessage(getString(R.string.loading_text));
    }

    protected void addSubscription(Subscription subscription) {
        mCompositeSubscription.add(subscription);
    }

    protected abstract Observable<Email> getEmailList();

    private void startEmailNotifications() {
        mDataManager.startEmailNotifications(getContext());
    }

    private void stopEmailNotifications() {
        mDataManager.stopEmailNotifications();
    }
}
