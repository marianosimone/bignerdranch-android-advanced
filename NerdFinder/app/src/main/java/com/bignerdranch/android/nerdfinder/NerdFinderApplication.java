package com.bignerdranch.android.nerdfinder;


import android.app.Application;
import android.support.annotation.NonNull;

import com.bignerdranch.android.nerdfinder.inject.Injector;
import com.bignerdranch.android.nerdfinder.inject.NerdFinderComponent;
import com.bignerdranch.android.nerdfinder.inject.NerdFinderGraph;

public class NerdFinderApplication extends Application {

    protected NerdFinderGraph mComponent;

    @Override
    public void onCreate() {
        super.onCreate();
        setupDagger();
    }

    protected void setupDagger() {
        mComponent = NerdFinderComponent.Initializer.init(this);
    }

    @Override
    public Object getSystemService(@NonNull String name) {
        if (Injector.matchesService(name)) {
            return mComponent;
        }
        return super.getSystemService(name);
    }
}
