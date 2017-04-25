package com.bignerdranch.android.nerdfinder.web;

import android.content.Context;
import android.net.Uri;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.util.Log;

import com.bignerdranch.android.nerdfinder.exception.UnauthorizedException;
import com.bignerdranch.android.nerdfinder.helper.NerdFinderSQLiteOpenHelper;
import com.bignerdranch.android.nerdfinder.listener.VenueCheckInListener;
import com.bignerdranch.android.nerdfinder.listener.VenueSearchListener;
import com.bignerdranch.android.nerdfinder.model.TokenStore;
import com.bignerdranch.android.nerdfinder.model.Venue;
import com.bignerdranch.android.nerdfinder.model.VenueSearchResponse;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

import java.net.HttpURLConnection;
import java.util.ArrayList;
import java.util.Date;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

import okhttp3.HttpUrl;
import okhttp3.Interceptor;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.logging.HttpLoggingInterceptor;
import retrofit2.Retrofit;
import retrofit2.adapter.rxjava.RxJavaCallAdapterFactory;
import retrofit2.converter.gson.GsonConverterFactory;
import rx.Observable;
import rx.Scheduler;
import rx.android.schedulers.AndroidSchedulers;
import rx.schedulers.Schedulers;

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

    private static final String SWARM_MODE = "swarm";

    private static final String TEST_LAT_LNG = "33.759,-84.332";

    private static final String OAUTH_ENDPOINT
            = "https://foursquare.com/oauth2/authenticate";

    public static final String OAUTH_REDIRECT_URI
            = "http://www.bignerdranch.com";

    protected static DataManager sDataManager;

    private static TokenStore sTokenStore;

    private Map<String, Venue> mVenues;

    private final List<VenueSearchListener> mSearchListenerList;

    private final ArrayList<VenueCheckInListener> mCheckInListenerList;

    private final Scheduler mSubscribeOnScheduler;

    private final Scheduler mObserveOnScheduler;

    private final Retrofit mRetrofit;

    private final Retrofit mAuthenticatedRetrofit;

    private final NerdFinderSQLiteOpenHelper mSQLiteOpenHelper;

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
                    .addCallAdapterFactory(RxJavaCallAdapterFactory.create())
                    .client(client)
                    .build();

            final OkHttpClient authenticatedClient = new OkHttpClient.Builder()
                    .addInterceptor(loggingInterceptor)
                    .addInterceptor(sAuthorizationInterceptor)
                    .addInterceptor(sAuthenticatedRequestInterceptor)
                    .build();

            final Retrofit authenticatedRetrofit = new Retrofit.Builder()
                    .baseUrl(FOURSQUARE_ENDPOINT)
                    .client(authenticatedClient)
                    .addConverterFactory(GsonConverterFactory.create(gson))
                    .addCallAdapterFactory(RxJavaCallAdapterFactory.create())
                    .build();

            final TokenStore tokenStore = TokenStore.get(context);
            sDataManager = new DataManager(context, tokenStore, retrofit, authenticatedRetrofit);
        }
        return sDataManager;
    }

    DataManager(
            final @NonNull Context context,
            final @NonNull TokenStore tokenStore,
            final @NonNull Retrofit retrofit,
            final @NonNull Retrofit authenticatedRetrofit) {
        sTokenStore = tokenStore;
        mRetrofit = retrofit;
        mAuthenticatedRetrofit = authenticatedRetrofit;
        mSearchListenerList = new ArrayList<>();
        mCheckInListenerList = new ArrayList<>();
        mSubscribeOnScheduler = getSubscribeOnScheduler();
        mObserveOnScheduler = getObserveOnScheduler();
        mSQLiteOpenHelper = new NerdFinderSQLiteOpenHelper(context);
    }

    public void fetchVenueSearch(final @NonNull String query) {
        innerFetchVenueSearch(mRetrofit.create(VenueInterface.class).venueSearchNear(query));
    }

    public void fetchVenueSearch() {
        innerFetchVenueSearch(mRetrofit.create(VenueInterface.class).venueSearch(TEST_LAT_LNG));
    }

    private void innerFetchVenueSearch(final @NonNull Observable<VenueSearchResponse> source) {
        source
                .subscribeOn(mSubscribeOnScheduler)
                .observeOn(mObserveOnScheduler)
                .subscribe(
                        result -> {
                            mVenues = new LinkedHashMap<>();
                            for (final Venue venue : result.getVenueList()) {
                                mVenues.put(venue.getId(), venue);
                            }
                            notifySearchListeners();
                        },
                        error -> Log.e(TAG, "Failed to fetch venue search", error)
                );
    }

    public void checkInToVenue(final @NonNull String venueId) {
        final VenueInterface venueInterface =
                mAuthenticatedRetrofit.create(VenueInterface.class);
        venueInterface.venueCheckIn(venueId)
                .subscribeOn(mSubscribeOnScheduler)
                .observeOn(mObserveOnScheduler)
                .doOnNext(result -> {
                    mSQLiteOpenHelper.registerCheckIn(venueId);
                })
                .subscribe(
                        result -> notifyCheckInListeners(),
                        this::handleCheckInException
                );
    }

    @Nullable
    public Date getLatestCheckIn(final @NonNull String venueId) {
        return mSQLiteOpenHelper.getLatestCheckIn(venueId);
    }

    private void notifyCheckInListenersTokenExpired() {
        for (VenueCheckInListener listener : mCheckInListenerList) {
            listener.onTokenExpired();
        }
    }

    public void addVenueSearchListener(VenueSearchListener listener) {
        mSearchListenerList.add(listener);
    }

    public void removeVenueSearchListener(VenueSearchListener listener) {
        mSearchListenerList.remove(listener);
    }

    private void notifySearchListeners() {
        final List<Venue> venues = new ArrayList<>(mVenues.values());
        for (VenueSearchListener listener : mSearchListenerList) {
            listener.onVenueSearchFinished(venues);
        }
    }

    public void addVenueCheckInListener(VenueCheckInListener listener) {
        mCheckInListenerList.add(listener);
    }

    public void removeVenueCheckInListener(VenueCheckInListener listener) {
        mCheckInListenerList.remove(listener);
    }

    private void notifyCheckInListeners() {
        for (VenueCheckInListener listener : mCheckInListenerList) {
            listener.onVenueCheckInFinished();
        }
    }

    private static Interceptor sRequestInterceptor = chain -> {
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
    };

    private static Interceptor sAuthenticatedRequestInterceptor = chain -> {
        HttpUrl url = chain.request().url().newBuilder()
                .addQueryParameter("oauth_token", sTokenStore.getAccessToken())
                .addQueryParameter("v", FOURSQUARE_VERSION)
                .addQueryParameter("m", SWARM_MODE)
                .build();
        Request request = chain.request().newBuilder()
                .url(url)
                .build();
        return chain.proceed(request);
    };

    public static Interceptor sAuthorizationInterceptor = chain -> {
        okhttp3.Response response = chain.proceed(chain.request());
        if (response.code() == HttpURLConnection.HTTP_UNAUTHORIZED) {
            throw new UnauthorizedException();
        }
        return response;
    };

    public String getAuthenticationUrl() {
        return Uri.parse(OAUTH_ENDPOINT).buildUpon()
                .appendQueryParameter("client_id", CLIENT_ID)
                .appendQueryParameter("response_type", "token")
                .appendQueryParameter("redirect_uri", OAUTH_REDIRECT_URI)
                .build()
                .toString();
    }

    public Venue getVenue(final @NonNull String venueId) {
        return mVenues.containsKey(venueId) ? mVenues.get(venueId) : null;
    }

    public boolean isLoggedIn() {
        return sTokenStore.getAccessToken() != null;
    }

    private void handleCheckInException(Throwable error) {
        if (error instanceof UnauthorizedException) {
            sTokenStore.setAccessToken(null);
            notifyCheckInListenersTokenExpired();
        }
    }

    Scheduler getSubscribeOnScheduler() {
        return Schedulers.io();
    }

    Scheduler getObserveOnScheduler() {
        return AndroidSchedulers.mainThread();
    }
}
