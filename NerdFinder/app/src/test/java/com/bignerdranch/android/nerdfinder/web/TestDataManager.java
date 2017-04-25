package com.bignerdranch.android.nerdfinder.web;

import android.support.annotation.NonNull;

import com.bignerdranch.android.nerdfinder.helper.NerdFinderSQLiteOpenHelper;
import com.bignerdranch.android.nerdfinder.model.TokenStore;

import retrofit2.Retrofit;
import rx.Scheduler;
import rx.schedulers.Schedulers;

public class TestDataManager extends DataManager {

    public TestDataManager(
            final @NonNull TokenStore tokenStore,
            final @NonNull Retrofit retrofit,
            final @NonNull Retrofit authenticatedRetrofit,
            final @NonNull NerdFinderSQLiteOpenHelper sqLiteOpenHelper) {
        super(tokenStore, retrofit, authenticatedRetrofit, sqLiteOpenHelper);
        sDataManager = this;
    }

    Scheduler getSubscribeOnScheduler() {
        return Schedulers.immediate();
    }

    Scheduler getObserveOnScheduler() {
        return Schedulers.immediate();
    }
}
