package com.bignerdranch.android.nerdfinder.web.interceptor;

import com.bignerdranch.android.nerdfinder.exception.UnauthorizedException;

import java.io.IOException;
import java.net.HttpURLConnection;

import okhttp3.Interceptor;
import okhttp3.Response;

/**
 * Interceptor that checks that the Response does not have a 401 as status
 */
public class AuthorizationInterceptor implements Interceptor {

    @Override
    public Response intercept(Chain chain) throws IOException {
        final Response response = chain.proceed(chain.request());
        if (response.code() == HttpURLConnection.HTTP_UNAUTHORIZED) {
            throw new UnauthorizedException();
        }
        return response;
    }
}
