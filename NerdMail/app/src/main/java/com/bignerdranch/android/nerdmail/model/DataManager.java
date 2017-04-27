package com.bignerdranch.android.nerdmail.model;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.preference.PreferenceManager;
import android.support.annotation.NonNull;

import com.bignerdranch.android.nerdmailservice.Email;
import com.bignerdranch.android.nerdmailservice.NerdMailService;

import java.util.ArrayList;
import java.util.List;

import javax.inject.Inject;
import javax.inject.Singleton;

import rx.Observable;
import rx.android.schedulers.AndroidSchedulers;
import rx.schedulers.Schedulers;
import timber.log.Timber;

@Singleton
public class DataManager {

    private static final String FETCHED_EMAILS_KEY = "DataManager.FetchedEmails";

    private Context mContext;
    private EmailDatabaseHelper mEmailDatabaseHelper;
    private NerdMailService mNerdMailService;

    @Inject
    DataManager(final @NonNull Context context) {
        mContext = context;
        mEmailDatabaseHelper = new EmailDatabaseHelper(mContext);
        mNerdMailService = new NerdMailService();
    }

    public Observable<Email> getEmails() {
        return Observable.create((Observable.OnSubscribe<Email>) subscriber -> {
            if (!fetchedEmails()) {
                mNerdMailService.fetchEmails()
                        .subscribe(DataManager.this::insertEmail,
                                throwable -> {
                                },
                                DataManager.this::setFetchedEmails);
            }

            Cursor emailCursor = mEmailDatabaseHelper.getReadableDatabase()
                    .query(EmailDatabaseHelper.TABLE_NAME, null, null, null, null,
                            null, EmailDatabaseHelper.ID_COLUMN + " DESC");
            EmailCursorWrapper emailCursorWrapper =
                    new EmailCursorWrapper(emailCursor);

            try {
                emailCursorWrapper.moveToFirst();
                while (!emailCursorWrapper.isAfterLast()) {
                    subscriber.onNext(emailCursorWrapper.getEmail());
                    emailCursorWrapper.moveToNext();
                }
            } catch (final @NonNull Exception e) {
                Timber.e(e, "Got exception");
            } finally {
                emailCursor.close();
                emailCursorWrapper.close();
            }
            subscriber.onCompleted();
        })
                .onBackpressureBuffer()
                .subscribeOn(Schedulers.newThread())
                .observeOn(AndroidSchedulers.mainThread());
    }

    public Observable<List<Email>> getNotificationEmails() {
        return Observable.create((Observable.OnSubscribe<List<Email>>) subscriber -> {
            final Cursor emailCursor = mEmailDatabaseHelper.getReadableDatabase()
                    .query(EmailDatabaseHelper.TABLE_NAME, null,
                            "notified = 0 AND spam = 0", null,
                            null, null, null);
            final EmailCursorWrapper emailCursorWrapper = new EmailCursorWrapper(emailCursor);

            final List<Email> emails = new ArrayList<>();
            try {
                emailCursorWrapper.moveToFirst();
                while (!emailCursorWrapper.isAfterLast()) {
                    emails.add(emailCursorWrapper.getEmail());
                    emailCursorWrapper.moveToNext();
                }
            } catch (final Exception e) {
                Timber.e(e, "Got exception");
            } finally {
                emailCursor.close();
                emailCursorWrapper.close();
            }
            subscriber.onNext(emails);
            subscriber.onCompleted();
        }).filter(messages -> !messages.isEmpty())
                .onBackpressureBuffer()
                .subscribeOn(Schedulers.newThread())
                .observeOn(AndroidSchedulers.mainThread());
    }

    public void markEmailsAsNotified() {
        Observable.create((Observable.OnSubscribe<Void>) subscriber -> {
            Timber.d("Mark emails as notified");
            String[] notifiedValue = new String[1];
            notifiedValue[0] = "0";
            Cursor emailCursor = mEmailDatabaseHelper.getReadableDatabase()
                    .query(EmailDatabaseHelper.TABLE_NAME, null,
                            "notified = ? AND spam = 0", notifiedValue,
                            null, null, null);
            EmailCursorWrapper emailCursorWrapper =
                    new EmailCursorWrapper(emailCursor);

            List<Email> emails = new ArrayList<>();
            try {
                emailCursorWrapper.moveToFirst();
                while (!emailCursorWrapper.isAfterLast()) {
                    emails.add(emailCursorWrapper.getEmail());
                    emailCursorWrapper.moveToNext();
                }
            } catch (Exception e) {
                Timber.e(e, "Got exception");
            } finally {
                emailCursor.close();
                emailCursorWrapper.close();
            }
            for (Email email : emails) {
                email.setNotified(true);
                updateEmail(email);
            }
        })
                .onBackpressureBuffer()
                .subscribeOn(Schedulers.newThread())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe();
    }

    public void insertEmail(final @NonNull Email email) {
        ContentValues contentValues = getEmailContentValues(email);
        mEmailDatabaseHelper.getWritableDatabase()
                .insert(EmailDatabaseHelper.TABLE_NAME,
                        null, contentValues);
    }

    public void updateEmail(Email email) {
        ContentValues contentValues = getEmailContentValues(email);
        String[] emailId = new String[1];
        emailId[0] = "" + email.getId();
        mEmailDatabaseHelper.getWritableDatabase()
                .update(EmailDatabaseHelper.TABLE_NAME, contentValues,
                        "_id = ?", emailId);
    }

    public void startEmailNotifications(Context context) {
        mNerdMailService.startNotifications(context);
    }

    public void stopEmailNotifications() {
        mNerdMailService.stopNotifications();
    }

    private boolean fetchedEmails() {
        return PreferenceManager.getDefaultSharedPreferences(mContext)
                .getBoolean(FETCHED_EMAILS_KEY, false);
    }

    private void setFetchedEmails() {
        PreferenceManager.getDefaultSharedPreferences(mContext)
                .edit()
                .putBoolean(FETCHED_EMAILS_KEY, true)
                .apply();
    }

    private static ContentValues getEmailContentValues(Email email) {
        ContentValues cv = new ContentValues();
        cv.put(EmailDatabaseHelper.SENDER_COLUMN, email.getSenderAddress());
        cv.put(EmailDatabaseHelper.SUBJECT_COLUMN, email.getSubject());
        cv.put(EmailDatabaseHelper.BODY_COLUMN, email.getBody());
        cv.put(EmailDatabaseHelper.IMPORTANT_COLUMN, email.isImportant() ? 1 : 0);
        cv.put(EmailDatabaseHelper.SPAM_COLUMN, email.isSpam() ? 1 : 0);
        cv.put(EmailDatabaseHelper.NOTIFIED_COLUMN, email.isNotified() ? 1 : 0);

        return cv;
    }
}
