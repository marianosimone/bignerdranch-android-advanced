package com.bignerdranch.android.nerdmail.inject;

import android.content.Context;
import android.support.annotation.NonNull;

import com.bignerdranch.android.nerdmail.model.DataManager;

import javax.inject.Singleton;

import dagger.Module;
import dagger.Provides;

@Module
class NerdMailApplicationModule {

    @NonNull
    private final Context mApplicationContext;

    NerdMailApplicationModule(final @NonNull Context context) {
        mApplicationContext = context.getApplicationContext();
    }

    @Provides
    Context provideContext() {
        return mApplicationContext;
    }

//    @Provides
//    @Singleton
//    DataManager provideDataManager(final @NonNull Context context) {
//        return new DataManager(context);
//    }
}
