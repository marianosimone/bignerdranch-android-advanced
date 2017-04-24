package com.bignerdranch.android.nerdfinder.web;

import com.bignerdranch.android.nerdfinder.model.VenueSearchResponse;

import retrofit2.http.Field;
import retrofit2.http.FormUrlEncoded;
import retrofit2.http.GET;
import retrofit2.http.POST;
import retrofit2.http.Query;
import rx.Observable;

interface VenueInterface {

    @GET("venues/search")
    Observable<VenueSearchResponse> venueSearch(@Query("ll") String latLngString);

    @FormUrlEncoded
    @POST("checkins/add")
    Observable<Object> venueCheckIn(@Field("venueId") String venueId);
}
