package com.bignerdranch.android.nerdmail.inject;

import android.support.annotation.NonNull;

import com.bignerdranch.android.nerdmail.NerdMailApplication;

import javax.inject.Singleton;

import dagger.Component;

@Singleton
@Component(modules = {
        NerdMailApplicationModule.class,
})
public interface NerdMailComponent extends NerdMailGraph {

    final class Initializer {

        private Initializer() {
            throw new AssertionError("No instances.");
        }

        public static NerdMailGraph init(final @NonNull NerdMailApplication app) {
            return DaggerNerdMailComponent.builder()
                    .nerdMailApplicationModule(new NerdMailApplicationModule(app))
                    .build();
        }
    }
}
