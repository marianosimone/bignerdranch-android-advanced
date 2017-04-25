package com.bignerdranch.android.nerdmart.inject;

import com.bignerdranch.android.nerdmart.service.NerdMartServiceManagerTest;

public interface TestNerdMartGraph extends NerdMartGraph {
    void inject(NerdMartServiceManagerTest nerdMartServiceManagerTest);
}