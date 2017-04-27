package com.bignerdranch.android.nerdmail.inject;

import com.bignerdranch.android.nerdmail.controller.EmailListFragment;
import com.bignerdranch.android.nerdmail.view.EmailListItemView;

public interface NerdMailGraph {
    void inject(final EmailListFragment emailListFragment);
    void inject(final EmailListItemView emailListItemView);
}
