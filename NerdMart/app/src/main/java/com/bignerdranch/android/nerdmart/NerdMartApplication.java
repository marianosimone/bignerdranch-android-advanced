package com.bignerdranch.android.nerdmart;

import android.app.Application;
import android.support.annotation.NonNull;

import com.bignerdranch.android.nerdmart.inject.Injector;
import com.bignerdranch.android.nerdmart.inject.NerdMartComponent;
import com.bignerdranch.android.nerdmart.inject.NerdMartGraph;

import timber.log.Timber;

public class NerdMartApplication extends Application {

    protected NerdMartGraph mComponent;

    @Override
    public void onCreate() {
        super.onCreate();
        Timber.plant(new Timber.DebugTree());
        setupDagger();
    }

    protected void setupDagger() {
        mComponent = NerdMartComponent.Initializer.init(this);
    }

    @Override
    public Object getSystemService(@NonNull String name) {
        if (Injector.matchesService(name)) {
            return mComponent;
        }
        return super.getSystemService(name);
    }
}
