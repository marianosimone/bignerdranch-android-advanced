package com.bignerdranch.android.nerdmail.controller;

import android.os.Bundle;
import android.support.design.widget.NavigationView;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;

import com.bignerdranch.android.nerdmail.R;

import butterknife.BindView;

public class DrawerActivity extends AppCompatActivity {
    private static final String TAG = "DrawerActivity";
    private static final String EXTRA_CURRENT_DRAWER_ITEM = "DrawerActivity.CurrentDrawerItem";

    @BindView(R.id.activity_drawer_layout)
    DrawerLayout mDrawerLayout;

    @BindView(R.id.activity_drawer_navigation_view)
    NavigationView mNavigationView;

    @BindView(R.id.activity_drawer_toolbar)
    Toolbar mToolbar;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_drawer);
        setSupportActionBar(mToolbar);
    }

    @Override
    public void onResume() {
        super.onResume();
    }

    @Override
    protected void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
    }
}
