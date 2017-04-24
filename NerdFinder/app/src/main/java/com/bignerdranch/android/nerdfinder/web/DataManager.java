package com.bignerdranch.android.nerdfinder.web;

import android.content.Context;
import android.net.Uri;
import android.util.Log;

import com.bignerdranch.android.nerdfinder.listener.VenueSearchListener;
import com.bignerdranch.android.nerdfinder.model.TokenStore;
import com.bignerdranch.android.nerdfinder.model.Venue;
import com.bignerdranch.android.nerdfinder.model.VenueSearchResponse;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import okhttp3.HttpUrl;
import okhttp3.Interceptor;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.logging.HttpLoggingInterceptor;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

public class DataManager {

    private static final String TAG = "DataManager";

    private static final String FOURSQUARE_ENDPOINT = "https://api.foursquare.com/v2/";

    private static final String CLIENT_ID
            = "JKP02TJ5V35Y501TNE0XIKB4DSAR1TIDCOJJQAGLUMJDG1DS";

    // Not that secret... but there's not much you can do with it, so... here it is :P
    private static final String CLIENT_SECRET
            = "3Z3NISRYLLQJTJXIXLJCRTXA41KHAY35ZHWM40NK0U1HHBLN";

    private static final String FOURSQUARE_VERSION = "20150406";

    private static final String FOURSQUARE_MODE = "foursquare";

    private static final String TEST_LAT_LNG = "33.759,-84.332";

    private static final String OAUTH_ENDPOINT
            = "https://foursquare.com/oauth2/authenticate";

    public static final String OAUTH_REDIRECT_URI
            = "http://www.bignerdranch.com";

    private List<Venue> mVenueList;

    private final List<VenueSearchListener> mSearchListenerList;

    private static DataManager sDataManager;

    private final TokenStore mTokenStore;

    private final Retrofit mRetrofit;


    public static DataManager get(Context context) {
        if (sDataManager == null) {
            final Gson gson = new GsonBuilder()
                    .registerTypeAdapter(VenueSearchResponse.class, new VenueListDeserializer())
                    .create();

            final HttpLoggingInterceptor loggingInterceptor = new HttpLoggingInterceptor();
            loggingInterceptor.setLevel(HttpLoggingInterceptor.Level.BODY);

            final OkHttpClient client = new OkHttpClient.Builder()
                    .addInterceptor(sRequestInterceptor)
                    .addInterceptor(loggingInterceptor)
                    .build();

            final Retrofit retrofit = new Retrofit.Builder()
                    .baseUrl(FOURSQUARE_ENDPOINT)
                    .addConverterFactory(GsonConverterFactory.create(gson))
                    .client(client)
                    .build();

            final TokenStore tokenStore = TokenStore.get(context);
            sDataManager = new DataManager(tokenStore, retrofit);
        }
        return sDataManager;
    }

    private DataManager() {
        throw new InstantiationError("Default constructor called for singleton");
    }

    private DataManager(TokenStore tokenStore, Retrofit retrofit) {
        mTokenStore = tokenStore;
        mRetrofit = retrofit;
        mSearchListenerList = new ArrayList<>();
    }

    public void fetchVenueSearch() {
        final VenueInterface venueInterface = mRetrofit.create(VenueInterface.class);
        venueInterface.venueSearch(TEST_LAT_LNG)
                .enqueue(new Callback<VenueSearchResponse>() {
                    @Override
                    public void onResponse(
                            Call<VenueSearchResponse> call,
                            Response<VenueSearchResponse> response) {
                        mVenueList = response.body().getVenueList();
                        notifySearchListeners();
                    }

                    @Override
                    public void onFailure(Call<VenueSearchResponse> call, Throwable t) {
                        Log.e(TAG, "Failed to fetch venue search", t);
                    }
                });
    }

    public void addVenueSearchListener(VenueSearchListener listener) {
        mSearchListenerList.add(listener);
    }

    public void removeVenueSearchListener(VenueSearchListener listener) {
        mSearchListenerList.remove(listener);
    }

    private void notifySearchListeners() {
        for (VenueSearchListener listener : mSearchListenerList) {
            listener.onVenueSearchFinished(mVenueList);
        }
    }

    private static Interceptor sRequestInterceptor = new Interceptor() {
        @Override
        public okhttp3.Response intercept(Chain chain) throws IOException {
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
    };

    public String getAuthenticationUrl() {
        return Uri.parse(OAUTH_ENDPOINT).buildUpon()
                .appendQueryParameter("client_id", CLIENT_ID)
                .appendQueryParameter("response_type", "token")
                .appendQueryParameter("redirect_uri", OAUTH_REDIRECT_URI)
                .build()
                .toString();
    }
}
