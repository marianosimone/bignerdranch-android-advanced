package com.bignerdranch.android.nerdfinder.web.interceptor;

import android.support.annotation.NonNull;

import com.bignerdranch.android.nerdfinder.model.TokenStore;

import java.io.IOException;

import okhttp3.HttpUrl;
import okhttp3.Interceptor;
import okhttp3.Request;
import okhttp3.Response;

import static com.bignerdranch.android.nerdfinder.web.FoursquareConfig.FOURSQUARE_VERSION;
import static com.bignerdranch.android.nerdfinder.web.FoursquareConfig.SWARM_MODE;

/**
 * Interceptor that takes care of adding the required Oauth parameters for Foursquare
 */
public class FoursquareAuthenticatedInterceptor implements Interceptor {

    private final TokenStore mTokenStore;

    public FoursquareAuthenticatedInterceptor(final @NonNull TokenStore tokenStore) {
        mTokenStore = tokenStore;
    }

    @Override
    public Response intercept(Chain chain) throws IOException {
        HttpUrl url = chain.request().url().newBuilder()
                .addQueryParameter("oauth_token", mTokenStore.getAccessToken())
                .addQueryParameter("v", FOURSQUARE_VERSION)
                .addQueryParameter("m", SWARM_MODE)
                .build();
        Request request = chain.request().newBuilder()
                .url(url)
                .build();
        return chain.proceed(request);
    }
}
