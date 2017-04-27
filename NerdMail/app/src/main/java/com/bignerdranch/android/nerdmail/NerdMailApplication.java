package com.bignerdranch.android.nerdmail;

import android.app.Application;
import android.support.annotation.NonNull;

import com.bignerdranch.android.nerdmail.controller.EmailService;
import com.bignerdranch.android.nerdmail.inject.Injector;
import com.bignerdranch.android.nerdmail.inject.NerdMailComponent;
import com.bignerdranch.android.nerdmail.inject.NerdMailGraph;

import timber.log.Timber;


public class NerdMailApplication extends Application {

    protected NerdMailGraph mComponent;

    @Override
    public void onCreate() {
        super.onCreate();
        Timber.plant(new Timber.DebugTree());
        setupDagger();
    }

    protected void setupDagger() {
        mComponent = NerdMailComponent.Initializer.init(this);
    }

    @Override
    public Object getSystemService(@NonNull String name) {
        if (Injector.matchesService(name)) {
            return mComponent;
        }
        return super.getSystemService(name);
    }

    public void inject(EmailService emailService) {

    }
}
