package com.bignerdranch.android.nerdtweet.sync;

import android.accounts.Account;
import android.accounts.AccountManager;
import android.content.AbstractThreadedSyncAdapter;
import android.content.ContentProviderClient;
import android.content.Context;
import android.content.SyncResult;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.util.Log;

import com.bignerdranch.android.nerdtweet.account.Authenticator;
import com.bignerdranch.android.nerdtweet.contentprovider.DatabaseContract;
import com.bignerdranch.android.nerdtweet.controller.AuthenticationActivity;
import com.bignerdranch.android.nerdtweet.model.Tweet;
import com.bignerdranch.android.nerdtweet.model.TweetSearchResponse;
import com.bignerdranch.android.nerdtweet.model.User;
import com.bignerdranch.android.nerdtweet.web.AuthorizationInterceptor;
import com.bignerdranch.android.nerdtweet.web.TweetInterface;
import com.google.gson.Gson;

import java.util.List;

import okhttp3.OkHttpClient;
import okhttp3.logging.HttpLoggingInterceptor;
import retrofit2.Retrofit;
import retrofit2.adapter.rxjava.RxJavaCallAdapterFactory;
import retrofit2.converter.gson.GsonConverterFactory;
import rx.Observable;
import rx.android.schedulers.AndroidSchedulers;
import rx.schedulers.Schedulers;

public class SyncAdapter extends AbstractThreadedSyncAdapter {
    private static final String TAG = "SYNC_ADAPTER";
    private static final String TWITTER_ENDPOINT = "https://api.twitter.com/1.1/";
    private static final String QUERY = "android";

    private String mAccessTokenSecret;
    private String mAccessToken;

    public SyncAdapter(Context context, boolean autoInitialize) {
        super(context, autoInitialize);
        final AccountManager accountManager = AccountManager.get(context);
        final Account account =
                new Account(Authenticator.ACCOUNT_NAME, Authenticator.ACCOUNT_TYPE);
        mAccessTokenSecret =
                accountManager.getUserData(account, AuthenticationActivity.OAUTH_TOKEN_SECRET_KEY);
        mAccessToken = accountManager.peekAuthToken(account, Authenticator.AUTH_TOKEN_TYPE);
    }

    @Override
    public void onPerformSync(Account account, Bundle bundle, String s,
                              ContentProviderClient contentProviderClient,
                              SyncResult syncResult) {
        fetchTweets().
                observeOn(AndroidSchedulers.mainThread())
                .subscribeOn(Schedulers.io())
                .subscribe(
                        this::insertTweetData,
                        error -> Log.e(TAG, "Failed to fetch tweets", error));
    }

    private Observable<List<Tweet>> fetchTweets() {
        final HttpLoggingInterceptor loggingInterceptor = new HttpLoggingInterceptor();
        loggingInterceptor.setLevel(HttpLoggingInterceptor.Level.BODY);
        final OkHttpClient client = new OkHttpClient.Builder()
                .addInterceptor(new AuthorizationInterceptor())
                .addInterceptor(loggingInterceptor)
                .build();
        final Retrofit retrofit = new Retrofit.Builder()
                .baseUrl(TWITTER_ENDPOINT)
                .client(client)
                .addConverterFactory(GsonConverterFactory.create(new Gson()))
                .addCallAdapterFactory(RxJavaCallAdapterFactory.create())
                .build();
        final TweetInterface tweetInterface = retrofit.create(TweetInterface.class);
        return tweetInterface.searchTweets(QUERY).map(TweetSearchResponse::getTweetList);
    }

    private void insertTweetData(final @NonNull List<Tweet> tweets) {
        for (Tweet tweet : tweets) {
            final User user = tweet.getUser();
            getContext().getContentResolver().insert(
                    DatabaseContract.User.CONTENT_URI, user.getContentValues());
            getContext().getContentResolver().insert(
                    DatabaseContract.Tweet.CONTENT_URI, tweet.getContentValues());
        }
    }
}