package com.bignerdranch.android.nerdfinder.web;

import android.content.Context;
import android.support.annotation.NonNull;

import com.bignerdranch.android.nerdfinder.model.TokenStore;

import retrofit2.Retrofit;
import rx.Scheduler;
import rx.schedulers.Schedulers;

public class TestDataManager extends DataManager {

    public TestDataManager(
            final @NonNull Context context,
            final @NonNull TokenStore tokenStore,
            final @NonNull Retrofit retrofit,
            final @NonNull Retrofit authenticatedRetrofit) {
        super(context, tokenStore, retrofit, authenticatedRetrofit);
        sDataManager = this;
    }

    Scheduler getSubscribeOnScheduler() {
        return Schedulers.immediate();
    }

    Scheduler getObserveOnScheduler() {
        return Schedulers.immediate();
    }
}
