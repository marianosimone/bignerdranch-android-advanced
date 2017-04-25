package com.bignerdranch.android.nerdmart.inject;

import android.content.Context;
import android.support.annotation.NonNull;

import dagger.Module;
import dagger.Provides;

@Module
public class NerdMartApplicationModule {

    @NonNull
    private final Context mApplicationContext;

    public NerdMartApplicationModule(final @NonNull Context context) {
        mApplicationContext = context.getApplicationContext();
    }

    @Provides
    public Context provideContext() {
        return mApplicationContext;
    }
}
