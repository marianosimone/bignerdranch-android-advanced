package com.bignerdranch.android.nerdtweet.contentprovider;

import android.database.Cursor;
import android.database.CursorWrapper;

import com.bignerdranch.android.nerdtweet.model.User;

public class UserCursorWrapper extends CursorWrapper {
    public UserCursorWrapper(Cursor cursor) {
        super(cursor);
    }

    public User getUser() {
        String serverId = getString(getColumnIndex(
                DatabaseContract.User.SERVER_ID));
        String screenName = getString(getColumnIndex(
                DatabaseContract.User.SCREEN_NAME));
        String photoUrl = getString(getColumnIndex(
                DatabaseContract.User.PHOTO_URL));
        return new User(serverId, screenName, photoUrl);
    }
}