package com.bignerdranch.android.nerdmart.inject;

import android.support.annotation.NonNull;

import com.bignerdranch.android.nerdmart.NerdMartAbstractFragment;

public interface NerdMartGraph {

    void inject(final @NonNull NerdMartAbstractFragment fragment);
}
