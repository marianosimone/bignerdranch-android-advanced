package com.bignerdranch.android.nerdmart.inject;

import android.content.Context;

import com.bignerdranch.android.nerdmart.model.DataStore;
import com.bignerdranch.android.nerdmart.viewmodel.NerdMartViewModel;

import javax.inject.Singleton;

import dagger.Module;
import dagger.Provides;

@Module
public class NerdMartCommonModule {

    @Provides
    NerdMartViewModel provideNerdMartViewModel(
            Context context, DataStore dataStore) {
        return new NerdMartViewModel(context, dataStore.getCachedCart(),
                dataStore.getCachedUser());
    }

    @Provides
    @Singleton
    DataStore provideDataStore() {
        return new DataStore();
    }
}