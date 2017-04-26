package com.bignerdranch.android.nerdtweet.web;

import com.bignerdranch.android.nerdtweet.model.TweetSearchResponse;

import retrofit2.http.GET;
import retrofit2.http.Query;
import rx.Observable;

public interface TweetInterface {

    @GET("search/tweets.json")
    Observable<TweetSearchResponse> searchTweets(@Query("q") String query);
}