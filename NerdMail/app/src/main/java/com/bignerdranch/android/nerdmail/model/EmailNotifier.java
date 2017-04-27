package com.bignerdranch.android.nerdmail.model;

import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.support.annotation.NonNull;
import android.support.v4.app.NotificationCompat;

import com.bignerdranch.android.nerdmail.R;
import com.bignerdranch.android.nerdmail.controller.DrawerActivity;
import com.bignerdranch.android.nerdmailservice.Email;

import java.util.List;

import javax.inject.Inject;
import javax.inject.Singleton;

@Singleton
public class EmailNotifier {

    private static final int EMAIL_NOTIFICATION_ID = 0;

    private final Context mContext;

    @Inject
    EmailNotifier(final @NonNull Context context) {
        mContext = context.getApplicationContext();
    }

    public void notifyOfEmails(final @NonNull List<Email> emails) {
        if (emails.size() == 0) {
            return;
        }
        final Notification notification = emails.size() == 1
                ? createSingleEmailNotification(emails.get(0))
                : createMultipleEmailNotification(emails);
        final NotificationManager manager = (NotificationManager) mContext
                .getSystemService(Context.NOTIFICATION_SERVICE);
        manager.notify(EMAIL_NOTIFICATION_ID, notification);
    }

    public void clearNotifications() {
        final NotificationManager manager = (NotificationManager) mContext
                .getSystemService(Context.NOTIFICATION_SERVICE);
        manager.cancel(EMAIL_NOTIFICATION_ID);
    }

    private Notification createSingleEmailNotification(Email email) {
        final Intent intent = new Intent(mContext, DrawerActivity.class);
        final PendingIntent pi = PendingIntent.getActivity(mContext, 0, intent, 0);
        return new NotificationCompat.Builder(mContext)
                .setSmallIcon(R.mipmap.ic_launcher)
                .setContentTitle(email.getSenderAddress())
                .setContentText(email.getSubject())
                .setSubText(email.getBody())
                .setContentIntent(pi)
                .setAutoCancel(true)
                .build();
    }

    private Notification createMultipleEmailNotification(final @NonNull List<Email> emails) {
        final NotificationCompat.InboxStyle style = createInboxStyle(emails);
        final Intent intent = new Intent(mContext, DrawerActivity.class);
        final PendingIntent pi = PendingIntent.getActivity(mContext, 0, intent, 0);
        final String contentTitle
                = mContext.getString(R.string.multiple_emails_title, emails.size());
        final NotificationCompat.Builder builder = new NotificationCompat.Builder(mContext)
                .setSmallIcon(R.mipmap.ic_launcher)
                .setContentTitle(contentTitle)
                .setContentIntent(pi)
                .setAutoCancel(true)
                .setStyle(style);
        return builder.build();
    }

    private NotificationCompat.InboxStyle createInboxStyle(final @NonNull List<Email> emails) {
        final NotificationCompat.InboxStyle inboxStyle
                = new NotificationCompat.InboxStyle();
        final String bigTitle
                = mContext.getString(R.string.multiple_emails_title, emails.size());
        inboxStyle.setBigContentTitle(bigTitle);
        final int notificationCount = emails.size() > 5 ? 5 : emails.size();
        for (int i = 0; i < notificationCount; i++) {
            final Email email = emails.get(i);
            String text = email.getSenderAddress() + " " + email.getSubject();
            inboxStyle.addLine(text);
        }
        return inboxStyle;
    }
}
