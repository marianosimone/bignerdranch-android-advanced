package com.bignerdranch.android.nerdtweet.controller;


import android.accounts.Account;
import android.accounts.AccountManager;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v4.app.Fragment;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.bignerdranch.android.nerdtweet.R;
import com.bignerdranch.android.nerdtweet.account.Authenticator;
import com.bignerdranch.android.nerdtweet.contentprovider.DatabaseContract;
import com.bignerdranch.android.nerdtweet.model.Tweet;
import com.bumptech.glide.Glide;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.Callable;

import rx.Observable;
import rx.android.schedulers.AndroidSchedulers;
import rx.schedulers.Schedulers;


public class TweetListFragment extends Fragment {

    private static final String TAG = "TweetListFragment";

    private String mAccessToken;
    private Account mAccount;

    private RecyclerView mRecyclerView;
    private TweetAdapter mTweetAdapter;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_tweet_list, container, false);
        mRecyclerView = (RecyclerView)
                view.findViewById(R.id.fragment_tweet_list_recycler_view);
        mRecyclerView.setLayoutManager(new LinearLayoutManager(getContext()));
        mTweetAdapter = new TweetAdapter(new ArrayList<>());
        mRecyclerView.setAdapter(mTweetAdapter);
        return view;
    }

    @Override
    public void onStart() {
        super.onStart();
        fetchAccessToken();
    }

    private void fetchAccessToken() {
        final AccountManager accountManager = AccountManager.get(getContext());
        mAccount = new Account(Authenticator.ACCOUNT_NAME, Authenticator.ACCOUNT_TYPE);
        accountManager.getAuthToken(
                mAccount, Authenticator.AUTH_TOKEN_TYPE, null, getActivity(),
                future -> Observable.fromCallable(future::getResult)
                        .subscribeOn(Schedulers.io())
                        .observeOn(AndroidSchedulers.mainThread())
                        .subscribe(
                                bundle -> {
                                    mAccessToken = bundle
                                            .getString(AccountManager.KEY_AUTHTOKEN);
                                    Log.d(TAG, "Have access token: " + mAccessToken);

                                },
                                error -> Log.e(TAG, "Got an exception", error)
                        ), null);
    }

    @Override
    public void onResume() {
        super.onResume();
        clearDb();
    }

    private void clearDb() {
        getContext().getContentResolver()
                .delete(DatabaseContract.User.CONTENT_URI, null, null);
        getContext().getContentResolver()
                .delete(DatabaseContract.Tweet.CONTENT_URI, null, null);
    }

    private class TweetAdapter extends RecyclerView.Adapter<TweetHolder> {
        private List<Tweet> mTweetList;

        public TweetAdapter(final @NonNull List<Tweet> tweetList) {
            mTweetList = tweetList;
        }

        public void setTweetList(List<Tweet> tweetList) {
            mTweetList = tweetList;
            notifyDataSetChanged();
        }

        @Override
        public TweetHolder onCreateViewHolder(ViewGroup parent, int viewType) {
            LayoutInflater inflater = LayoutInflater.from(getContext());
            View view = inflater.inflate(R.layout.list_item_tweet, parent, false);
            return new TweetHolder(view);
        }

        @Override
        public void onBindViewHolder(TweetHolder holder, int position) {
            Tweet tweet = mTweetList.get(position);
            holder.bindTweet(tweet);
        }

        @Override
        public int getItemCount() {
            return mTweetList.size();
        }
    }

    private class TweetHolder extends RecyclerView.ViewHolder {
        private ImageView mProfileImageView;
        private TextView mTweetTextView;
        private TextView mScreenNameTextView;

        public TweetHolder(View itemView) {
            super(itemView);
            mProfileImageView = (ImageView) itemView
                    .findViewById(R.id.list_item_tweet_user_profile_image);
            mTweetTextView = (TextView) itemView
                    .findViewById(R.id.list_item_tweet_tweet_text_view);
            mScreenNameTextView = (TextView) itemView
                    .findViewById(R.id.list_item_tweet_user_screen_name_text_view);
        }

        public void bindTweet(Tweet tweet) {
            mTweetTextView.setText(tweet.getText());
            if (tweet.getUser() != null) {
                mScreenNameTextView.setText(tweet.getUser().getScreenName());
                Glide.with(getContext())
                        .load(tweet.getUser().getPhotoUrl()).into(mProfileImageView);
            }
        }
    }
}
