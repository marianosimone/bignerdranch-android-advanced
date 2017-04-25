package com.bignerdranch.android.nerdfinder.inject;

import android.content.Context;
import android.support.annotation.NonNull;

import dagger.Module;

@Module
class NerdFinderApplicationModule {

    @NonNull
    private final Context mApplicationContext;

    NerdFinderApplicationModule(final @NonNull Context context) {
        mApplicationContext = context.getApplicationContext();
    }

//    @Provides
//    NerdMartServiceInterface provideNerdMartServiceInterface() {
//        return new NerdMartService();
//    }
//
//    @Provides
//    @Singleton
//    NerdMartServiceManager provideNerdMartServiceManager(
//            NerdMartServiceInterface serviceInterface, DataStore dataStore
//    ) {
//        return new NerdMartServiceManager(serviceInterface, dataStore);
//    }
//
//    @Provides
//    @Singleton
//    DataStore provideDataStore() {
//        return new DataStore();
//    }
//
//    @Provides
//    NerdMartViewModel provideNerdMartViewModel(final @NonNull DataStore dataStore) {
//        return new NerdMartViewModel(mApplicationContext,
//                dataStore.getCachedCart(), dataStore.getCachedUser());
//    }
}
