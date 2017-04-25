package com.bignerdranch.android.nerdfinder.web.interceptor;

import android.support.annotation.NonNull;

import java.io.IOException;

import okhttp3.HttpUrl;
import okhttp3.Interceptor;
import okhttp3.Request;
import okhttp3.Response;

import static com.bignerdranch.android.nerdfinder.web.FoursquareConfig.CLIENT_ID;
import static com.bignerdranch.android.nerdfinder.web.FoursquareConfig.CLIENT_SECRET;
import static com.bignerdranch.android.nerdfinder.web.FoursquareConfig.FOURSQUARE_MODE;
import static com.bignerdranch.android.nerdfinder.web.FoursquareConfig.FOURSQUARE_VERSION;

/**
 * Interceptor that adds all required Foursquare parameters
 */
public class FoursquareRequestInterceptor implements Interceptor {

    @Override
    public Response intercept(final @NonNull Chain chain) throws IOException {
        final HttpUrl url = chain.request().url().newBuilder()
                .addQueryParameter("client_id", CLIENT_ID)
                .addQueryParameter("client_secret", CLIENT_SECRET)
                .addQueryParameter("v", FOURSQUARE_VERSION)
                .addQueryParameter("m", FOURSQUARE_MODE)
                .build();
        final Request request = chain.request().newBuilder()
                .url(url)
                .build();
        return chain.proceed(request);
    }
}
