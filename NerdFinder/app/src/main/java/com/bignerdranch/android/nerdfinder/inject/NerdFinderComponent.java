package com.bignerdranch.android.nerdfinder.inject;

import com.bignerdranch.android.nerdfinder.NerdFinderApplication;

import javax.inject.Singleton;

import dagger.Component;

@Singleton
@Component(modules = {NerdFinderApplicationModule.class})
public interface NerdFinderComponent extends NerdFinderGraph {

    final class Initializer {

        private Initializer() {
            throw new AssertionError("No instances.");
        }

        public static NerdFinderGraph init(NerdFinderApplication app) {
            return DaggerNerdFinderComponent.builder()
                    .nerdFinderApplicationModule(new NerdFinderApplicationModule(app))
                    .build();
        }
    }
}
