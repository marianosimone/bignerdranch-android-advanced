package com.bignerdranch.android.nerdfinder.model;

import com.google.gson.annotations.SerializedName;

import java.util.List;

public class VenueSearchResponse {

    @SerializedName("venues")
    List<Venue> mVenueList;

    public List<Venue> getVenueList() {
        return mVenueList;
    }
}
