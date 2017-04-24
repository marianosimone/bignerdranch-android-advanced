package com.bignerdranch.android.nerdfinder.model;

import com.google.gson.annotations.SerializedName;

public class VenueStats {

    @SerializedName("checkinsCount")
    int mCheckInsCount;

    public int getCheckInsCount() {
        return mCheckInsCount;
    }
}
