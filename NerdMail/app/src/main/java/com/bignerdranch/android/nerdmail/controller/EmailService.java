package com.bignerdranch.android.nerdmail.controller;

import android.app.IntentService;
import android.content.Context;
import android.content.Intent;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.annotation.VisibleForTesting;

import com.bignerdranch.android.nerdmail.inject.Injector;
import com.bignerdranch.android.nerdmail.model.DataManager;
import com.bignerdranch.android.nerdmail.model.EmailNotifier;
import com.bignerdranch.android.nerdmailservice.Email;

import javax.inject.Inject;
import javax.inject.Singleton;

import timber.log.Timber;

@Singleton
public class EmailService extends IntentService {

    private static final String TAG
            = "com.bignerdranch.android.nerdmail.EmailService";

    public static final String EXTRA_EMAIL
            = "com.bignerdranch.android.nerdmail.EMAIL_EXTRA";

    public static final String EXTRA_CLEAR
            = "com.bignerdranch.android.nerdmail.CLEAR_EXTRA";

    @Inject
    DataManager mDataManager;

    @Inject
    EmailNotifier mNotifier;

    @VisibleForTesting
    public EmailService() {
        super(TAG);
    }

    @Override
    public void onCreate() {
        super.onCreate();
        Injector.obtain(getApplication().getBaseContext()).inject(this);
    }

    public static Intent getNotifyIntent(
            final @NonNull Context context, final @NonNull Email email) {
        final Intent intent = new Intent(context, EmailService.class);
        intent.putExtra(EXTRA_EMAIL, email);
        return intent;
    }

    public static Intent getClearIntent(final @NonNull Context context) {
        Intent intent = new Intent(context, EmailService.class);
        intent.putExtra(EXTRA_CLEAR, true);
        return intent;
    }

    @Override
    protected void onHandleIntent(final @Nullable Intent intent) {
        if (intent != null) {
            boolean shouldClear = intent.getBooleanExtra(EXTRA_CLEAR, false);
            if (shouldClear) {
                clearEmails();
            } else {
                final Email email = (Email) intent.getSerializableExtra(EXTRA_EMAIL);
                mDataManager.insertEmail(email);
                mDataManager.getNotificationEmails().doOnNext(emails -> {
                    mNotifier.notifyOfEmails(emails);
                }).subscribe();
            }
        }
    }

    private void clearEmails() {
        mDataManager.markEmailsAsNotified();
    }
}
