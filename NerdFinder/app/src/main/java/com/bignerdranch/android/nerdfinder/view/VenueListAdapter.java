package com.bignerdranch.android.nerdfinder.view;

import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.view.ViewGroup;

import com.bignerdranch.android.nerdfinder.model.Venue;

import java.util.List;

public class VenueListAdapter extends RecyclerView.Adapter<VenueHolder> {

    @NonNull
    private List<Venue> mVenueList;

    public VenueListAdapter(final @NonNull List<Venue> venueList) {
        mVenueList = venueList;
    }

    public void setVenueList(final @NonNull List<Venue> venueList) {
        mVenueList = venueList;
        notifyDataSetChanged();
    }

    @Override
    public VenueHolder onCreateViewHolder(final @NonNull ViewGroup viewGroup, final int i) {
        VenueView venueView = new VenueView(viewGroup.getContext());
        return new VenueHolder(venueView);
    }

    @Override
    public void onBindViewHolder(final @NonNull VenueHolder venueHolder, final int position) {
        venueHolder.bindVenue(mVenueList.get(position));
    }

    @Override
    public int getItemCount() {
        return mVenueList.size();
    }
}
