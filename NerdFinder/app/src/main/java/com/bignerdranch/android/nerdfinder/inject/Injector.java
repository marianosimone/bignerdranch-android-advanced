package com.bignerdranch.android.nerdfinder.inject;

import android.content.Context;

import android.content.Context;

import com.bignerdranch.android.nerdfinder.NerdFinderConstants;

public final class Injector {

    private Injector() {
        throw new AssertionError("No instances.");
    }

    @SuppressWarnings("ResourceType") // Explicitly doing a custom service.
    public static NerdFinderGraph obtain(Context context) {
        return (NerdFinderGraph) context.getApplicationContext()
                .getSystemService(NerdFinderConstants.INJECTOR_SERVICE);
    }

    public static boolean matchesService(String name) {
        return NerdFinderConstants.INJECTOR_SERVICE.equals(name);
    }
}
