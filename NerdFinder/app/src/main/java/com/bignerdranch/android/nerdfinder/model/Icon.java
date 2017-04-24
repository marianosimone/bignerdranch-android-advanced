package com.bignerdranch.android.nerdfinder.model;

import com.google.gson.annotations.SerializedName;

public class Icon {

    private static final String BG_VARIANT = "bg_";

    private static final String DEFAULT_SIZE = "88";
    @SerializedName("prefix")
    private String mPrefix;

    @SerializedName("suffix")
    private String mSuffix;

    public String getUrl() {
        return mPrefix + BG_VARIANT + DEFAULT_SIZE + mSuffix;
    }
}
