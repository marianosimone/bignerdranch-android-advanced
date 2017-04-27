package com.bignerdranch.android.nerdmail.inject;

import com.bignerdranch.android.nerdmail.controller.DrawerActivity;
import com.bignerdranch.android.nerdmail.controller.EmailListFragment;
import com.bignerdranch.android.nerdmail.controller.EmailService;
import com.bignerdranch.android.nerdmail.view.EmailListItemView;

public interface NerdMailGraph {

    void inject(final DrawerActivity drawerActivity);

    void inject(final EmailListFragment emailListFragment);

    void inject(final EmailListItemView emailListItemView);

    void inject(final EmailService emailService);
}
