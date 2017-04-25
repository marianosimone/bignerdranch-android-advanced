package com.bignerdranch.android.nerdmart.inject;

import com.bignerdranch.android.nerdmart.NerdMartApplication;

import javax.inject.Singleton;

import dagger.Component;

@Singleton
@Component(modules = {NerdMartApplicationModule.class})
public interface NerdMartComponent extends NerdMartGraph {

    final class Initializer {

        private Initializer() {
            throw new AssertionError("No instances.");
        }

        public static NerdMartGraph init(NerdMartApplication app) {
            return DaggerNerdMartComponent.builder()
                    .nerdMartApplicationModule(new NerdMartApplicationModule(app))
                    .build();
        }
    }
}
