package com.bignerdranch.android.nerdmart.inject;

import android.content.Context;

import com.bignerdranch.android.nerdmart.NerdMartConstants;

public class TestInjector {

    private TestInjector() {
        throw new AssertionError("No instances.");
    }

    @SuppressWarnings("ResourceType") // Explicitly doing a custom service.
    public static TestNerdMartComponent obtain(Context context) {
        return (TestNerdMartComponent) context.getApplicationContext()
                .getSystemService(NerdMartConstants.INJECTOR_SERVICE);
    }

    public static boolean matchesService(String name) {
        return NerdMartConstants.INJECTOR_SERVICE.equals(name);
    }
}