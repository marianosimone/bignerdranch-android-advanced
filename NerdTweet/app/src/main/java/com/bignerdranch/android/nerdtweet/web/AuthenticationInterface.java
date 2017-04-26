package com.bignerdranch.android.nerdtweet.web;

import okhttp3.ResponseBody;
import retrofit2.http.Body;
import retrofit2.http.Field;
import retrofit2.http.FormUrlEncoded;
import retrofit2.http.POST;
import rx.Observable;

public interface AuthenticationInterface {

    @POST("oauth/request_token")
    Observable<ResponseBody> fetchRequestToken(@Body String body);

    @FormUrlEncoded
    @POST("oauth/access_token")
    Observable<ResponseBody> fetchAccessToken(@Field("oauth_verifier") String verifier);
}
