package com.bignerdranch.android.animals.web;

import com.bignerdranch.android.animals.model.GalleryItemList;

import retrofit2.Call;
import retrofit2.http.GET;
import retrofit2.http.Query;

public interface FlickrInterface {

    @GET("services/rest?method=flickr.photos.search&format=json&nojsoncallback=1&extras=url_s")
    Call<GalleryItemList> photoSearch(@Query("api_key") String apiKey, @Query("text") String query);
}