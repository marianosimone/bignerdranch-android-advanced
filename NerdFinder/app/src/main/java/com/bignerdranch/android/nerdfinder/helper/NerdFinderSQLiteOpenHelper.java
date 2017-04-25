package com.bignerdranch.android.nerdfinder.helper;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.util.Log;

import java.util.Date;


public class NerdFinderSQLiteOpenHelper extends SQLiteOpenHelper {

    private static final int DATABASE_VERSION = 1;
    private static final String CHECKINS_TABLE_NAME = "checkins";
    private static final String CHECK_IN_VENUE_ID = "venue_id";
    private static final String CHECK_IN_LATEST_DATE = "latest_date";
    private static final String CHECK_INS_TABLE_CREATE =
            "CREATE TABLE " + CHECKINS_TABLE_NAME + " (" +
                    CHECK_IN_VENUE_ID + " TEXT PRIMARY KEY, " +
                    CHECK_IN_LATEST_DATE + " INT);";
    private static final String DATABASE_NAME = "db.sql";

    public NerdFinderSQLiteOpenHelper(final @NonNull Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        db.execSQL(CHECK_INS_TABLE_CREATE);
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {

    }

    public void registerCheckIn(final @NonNull String venueId) {
        final ContentValues values = new ContentValues();
        values.put(CHECK_IN_VENUE_ID, venueId);
        values.put(CHECK_IN_LATEST_DATE, new Date().getTime());
        getWritableDatabase().insert(CHECKINS_TABLE_NAME, null, values);

    }

    @Nullable
    public Date getLatestCheckIn(final @NonNull String venueId) {
        final Cursor cursor = getReadableDatabase().query(CHECKINS_TABLE_NAME, new String[]{CHECK_IN_LATEST_DATE}, CHECK_IN_VENUE_ID + " = ?", new String[]{venueId}, null, null, null);
        try {

            if (cursor != null) {
                cursor.moveToFirst();
                final long value = cursor.getLong(0);
                return new Date(value);
            }
        } catch (final Exception e) {
            Log.e("DB", "Problem reading last date for " + venueId, e);
        } finally {
            if (cursor != null) {
                cursor.close();
            }
        }
        return null;
    }
}
