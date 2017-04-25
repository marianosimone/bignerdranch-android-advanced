package com.bignerdranch.android.nerdmart.inject;

import rx.Scheduler;
import rx.schedulers.Schedulers;

public class TestNerdMartServiceModule extends NerdMartServiceModule {
    @Override
    protected Scheduler provideScheduler() {
        return Schedulers.immediate();
    }
}
