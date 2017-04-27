package com.bignerdranch.android.nerdmail.controller;

import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.design.widget.NavigationView;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.widget.TextView;

import com.bignerdranch.android.nerdmail.R;

import butterknife.BindView;
import butterknife.ButterKnife;
import timber.log.Timber;

public class DrawerActivity extends AppCompatActivity {
    private static final String EXTRA_CURRENT_DRAWER_ITEM = "DrawerActivity.CurrentDrawerItem";

    @BindView(R.id.activity_drawer_layout)
    DrawerLayout mDrawerLayout;

    @BindView(R.id.activity_drawer_navigation_view)
    NavigationView mNavigationView;

    @BindView(R.id.activity_drawer_toolbar)
    Toolbar mToolbar;

    private int mCurrentToolbarTitle;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_drawer);
        ButterKnife.bind(this);
        setSupportActionBar(mToolbar);

        mNavigationView.setNavigationItemSelectedListener(
                item -> {
                    mDrawerLayout.closeDrawers();
                    final Fragment fragment;
                    switch (item.getItemId()) {
                        case R.id.nav_drawer_inbox:
                            fragment = new InboxFragment();
                            mCurrentToolbarTitle = R.string.nav_drawer_inbox;
                            break;
                        case R.id.nav_drawer_important:
                            fragment = new ImportantFragment();
                            mCurrentToolbarTitle = R.string.nav_drawer_important;
                            break;
                        case R.id.nav_drawer_spam:
                            fragment = new SpamFragment();
                            mCurrentToolbarTitle = R.string.nav_drawer_spam;
                            break;
                        case R.id.nav_drawer_all:
                            mCurrentToolbarTitle = R.string.nav_drawer_all;
                            fragment = new AllFragment();
                            break;
                        default:
                            Timber.e("Incorrect nav drawer item selection");
                            return false;
                    }
                    updateFragment(fragment);
                    updateToolbarTitle();
                    return true;
                });

        final View headerView = mNavigationView.getHeaderView(0);
        final TextView usernameView = (TextView)
                headerView.findViewById(R.id.nav_drawer_header_username);
        usernameView.setText("nerdynerd@bignerdranch.com");

        final ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(
                this, mDrawerLayout, mToolbar,
                R.string.open_drawer_content_description,
                R.string.close_drawer_content_description);
        mDrawerLayout.addDrawerListener(toggle);
        toggle.syncState();

        if (savedInstanceState == null) {
            updateFragment(new InboxFragment());
            mCurrentToolbarTitle = R.string.nav_drawer_inbox;
        } else {
            mCurrentToolbarTitle = savedInstanceState
                    .getInt(EXTRA_CURRENT_DRAWER_ITEM, R.string.nav_drawer_inbox);
        }
    }

    @Override
    public void onResume() {
        super.onResume();
        updateToolbarTitle();
    }

    @Override
    protected void onSaveInstanceState(final @NonNull Bundle outState) {
        super.onSaveInstanceState(outState);
        outState.putInt(EXTRA_CURRENT_DRAWER_ITEM, mCurrentToolbarTitle);
    }

    @Override
    public void onBackPressed() {
        if (mDrawerLayout.isDrawerOpen(mNavigationView)) {
            mDrawerLayout.closeDrawer(mNavigationView);
        } else {
            super.onBackPressed();
        }
    }

    private void updateFragment(final @NonNull Fragment fragment) {
        final FragmentManager fm = getSupportFragmentManager();
        fm.beginTransaction()
                .replace(R.id.activity_drawer_fragment_container, fragment)
                .commit();
    }

    private void updateToolbarTitle() {
        if (mCurrentToolbarTitle != 0) {
            mToolbar.setTitle(mCurrentToolbarTitle);
        }
    }
}
