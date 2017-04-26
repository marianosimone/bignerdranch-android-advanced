package com.bignerdranch.android.nerdtweet.account;

import android.accounts.AbstractAccountAuthenticator;
import android.accounts.Account;
import android.accounts.AccountAuthenticatorResponse;
import android.accounts.AccountManager;
import android.accounts.NetworkErrorException;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.text.TextUtils;

import com.bignerdranch.android.nerdtweet.controller.AuthenticationActivity;

public class Authenticator extends AbstractAccountAuthenticator {

    public static final String ACCOUNT_NAME = "NerdTweet";
    public static final String ACCOUNT_TYPE =
            "com.bignerdranch.android.nerdtweet.USER_ACCOUNT";
    public static final String AUTH_TOKEN_TYPE =
            "com.bignerdranch.android.nerdtweet.FULL_ACCESS";

    private final Context mContext;

    public Authenticator(final @NonNull Context context) {
        super(context);
        mContext = context;
    }

    @Override
    public Bundle getAuthToken(
            final @NonNull AccountAuthenticatorResponse response,
            final @NonNull Account account,
            final @NonNull String authTokenType,
            final @NonNull Bundle bundle) throws NetworkErrorException {
        final AccountManager accountManager = AccountManager.get(mContext);
        final String authToken = accountManager.peekAuthToken(account, authTokenType);
        final Bundle result = new Bundle();
        if (TextUtils.isEmpty(authToken)) {
            final Intent intent = AuthenticationActivity.newIntent(
                    mContext, account.type, authTokenType);
            result.putParcelable(AccountManager.KEY_INTENT, intent);
        } else {
            result.putString(AccountManager.KEY_ACCOUNT_NAME, account.name);
            result.putString(AccountManager.KEY_ACCOUNT_TYPE, account.type);
            result.putString(AccountManager.KEY_AUTHTOKEN, authToken);
        }
        return result;
    }

    @Override
    public Bundle addAccount(
            final @NonNull AccountAuthenticatorResponse response,
            final @NonNull String accountType,
            final @NonNull String authTokenType,
            final @NonNull String[] requiredFeatures,
            final @NonNull Bundle options) throws NetworkErrorException {
        final Intent intent = AuthenticationActivity.newIntent(
                mContext, accountType, authTokenType);
        Bundle bundle = new Bundle();
        bundle.putParcelable(AccountManager.KEY_INTENT, intent);
        return bundle;
    }

    @Override
    public Bundle editProperties(
            AccountAuthenticatorResponse response, String accountType) {
        return null;
    }

    @Override
    public Bundle confirmCredentials(
            AccountAuthenticatorResponse response, Account account,
            Bundle bundle) throws NetworkErrorException {
        return null;
    }

    @Override
    public String getAuthTokenLabel(String authTokenType) {
        return null;
    }

    @Override
    public Bundle updateCredentials(
            AccountAuthenticatorResponse response, Account account,
            String authTokenType, Bundle bundle) throws NetworkErrorException {
        return null;
    }

    @Override
    public Bundle hasFeatures(
            AccountAuthenticatorResponse response, Account account,
            String[] features) throws NetworkErrorException {
        return null;
    }
}