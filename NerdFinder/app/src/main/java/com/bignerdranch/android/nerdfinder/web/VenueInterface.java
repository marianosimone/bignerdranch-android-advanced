package com.bignerdranch.android.nerdfinder.web;

import com.bignerdranch.android.nerdfinder.model.VenueSearchResponse;

import retrofit2.Call;
import retrofit2.http.GET;
import retrofit2.http.Query;

public interface VenueInterface {

    @GET("venues/search")
    Call<VenueSearchResponse> venueSearch(@Query("ll") String latLngString);

}
