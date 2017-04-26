package com.bignerdranch.android.nerdtweet.web;

import android.util.Log;

import java.io.IOException;
import java.security.InvalidKeyException;
import java.security.NoSuchAlgorithmException;

import okhttp3.Interceptor;
import okhttp3.Request;
import okhttp3.Response;

public class AuthorizationInterceptor implements Interceptor {
    private static final String TAG = "AuthInterceptor";
    private static final String AUTH_HEADER = "Authorization";

    @Override
    public Response intercept(Interceptor.Chain chain) throws IOException {
        Request request = chain.request();
        final TwitterOauthHelper oauthHelper = TwitterOauthHelper.get();
        try {
            final String authHeaderString =
                    oauthHelper.getAuthorizationHeaderString(request);
            request = request.newBuilder()
                    .addHeader(AUTH_HEADER, authHeaderString)
                    .build();
        } catch (InvalidKeyException | NoSuchAlgorithmException e) {
            Log.e(TAG, "Failed to get auth header string", e);
        }
        return chain.proceed(request);
    }
}
