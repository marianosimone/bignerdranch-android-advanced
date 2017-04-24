package com.bignerdranch.android.nerdfinder.model;

import java.util.List;

public class Venue {
    private String mId;
    private String mName;
    private boolean mVerified;
    private Location mLocation;
    private List<Category> mCategoryList;

    public String getId() {
        return mId;
    }

    public String getName() {
        return mName;
    }

    public String getFormattedAddress() {
        return mLocation.getFormattedAddress();
    }
}
