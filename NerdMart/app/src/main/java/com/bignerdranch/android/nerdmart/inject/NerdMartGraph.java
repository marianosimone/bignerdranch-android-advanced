package com.bignerdranch.android.nerdmart.inject;

import com.bignerdranch.android.nerdmart.NerdMartAbstractActivity;
import com.bignerdranch.android.nerdmart.NerdMartAbstractFragment;

public interface NerdMartGraph {

    void inject(final NerdMartAbstractFragment fragment);
    void inject(final NerdMartAbstractActivity activity);
}
