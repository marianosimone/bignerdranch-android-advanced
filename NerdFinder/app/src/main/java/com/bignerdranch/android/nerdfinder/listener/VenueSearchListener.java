package com.bignerdranch.android.nerdfinder.listener;

import android.support.annotation.NonNull;

import com.bignerdranch.android.nerdfinder.model.Venue;

import java.util.List;

public interface VenueSearchListener {
    void onVenueSearchFinished(final @NonNull List<Venue> venues);
}
