package com.bignerdranch.android.nerdmart;

import com.bignerdranch.android.nerdmart.inject.NerdMartGraph;
import com.bignerdranch.android.nerdmart.inject.TestNerdMartComponent;

public class TestNerdMartApplication extends NerdMartApplication {

    @Override
    protected void setupDagger() {
        NerdMartGraph graph = TestNerdMartComponent.Initializer.init(this);
        setComponent(graph);
    }

    public void setComponent(NerdMartGraph graph) {
        mComponent = graph;
    }
}
