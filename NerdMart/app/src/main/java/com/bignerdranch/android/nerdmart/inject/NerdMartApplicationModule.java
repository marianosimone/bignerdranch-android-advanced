package com.bignerdranch.android.nerdmart.inject;

import android.content.Context;
import android.support.annotation.NonNull;

import com.bignerdranch.android.nerdmartservice.service.NerdMartService;
import com.bignerdranch.android.nerdmartservice.service.NerdMartServiceInterface;

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
    NerdMartServiceInterface provideNerdMartServiceInterface() {
        return new NerdMartService();
    }
}
