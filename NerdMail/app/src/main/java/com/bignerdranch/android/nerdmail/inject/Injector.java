package com.bignerdranch.android.nerdmail.inject;

import android.content.Context;
import android.support.annotation.NonNull;

import com.bignerdranch.android.nerdmail.NerdMailConstants;

public final class Injector {

    private Injector() {
        throw new AssertionError("No instances.");
    }

    @SuppressWarnings("ResourceType") // Explicitly doing a custom service.
    public static NerdMailGraph obtain(final @NonNull Context context) {
        return (NerdMailGraph) context.getApplicationContext()
                .getSystemService(NerdMailConstants.INJECTOR_SERVICE);
    }

    public static boolean matchesService(final @NonNull String name) {
        return NerdMailConstants.INJECTOR_SERVICE.equals(name);
    }
}
