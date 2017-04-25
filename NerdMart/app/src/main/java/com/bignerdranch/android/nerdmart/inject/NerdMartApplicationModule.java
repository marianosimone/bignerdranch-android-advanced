package com.bignerdranch.android.nerdmart.inject;

import android.content.Context;
import android.support.annotation.NonNull;

import com.bignerdranch.android.nerdmart.model.DataStore;
import com.bignerdranch.android.nerdmart.model.service.NerdMartServiceManager;
import com.bignerdranch.android.nerdmart.viewmodel.NerdMartViewModel;
import com.bignerdranch.android.nerdmartservice.service.NerdMartService;
import com.bignerdranch.android.nerdmartservice.service.NerdMartServiceInterface;

import javax.inject.Singleton;

import dagger.Module;
import dagger.Provides;

@Module
class NerdMartApplicationModule {

    @NonNull
    private final Context mApplicationContext;

    NerdMartApplicationModule(final @NonNull Context context) {
        mApplicationContext = context.getApplicationContext();
    }

    @Provides
    NerdMartServiceInterface provideNerdMartServiceInterface() {
        return new NerdMartService();
    }

    @Provides
    @Singleton
    NerdMartServiceManager provideNerdMartServiceManager(
            NerdMartServiceInterface serviceInterface, DataStore dataStore
    ) {
        return new NerdMartServiceManager(serviceInterface, dataStore);
    }

    @Provides
    @Singleton
    DataStore provideDataStore() {
        return new DataStore();
    }

    @Provides
    NerdMartViewModel provideNerdMartViewModel(final @NonNull DataStore dataStore) {
        return new NerdMartViewModel(mApplicationContext,
                dataStore.getCachedCart(), dataStore.getCachedUser());
    }
}
