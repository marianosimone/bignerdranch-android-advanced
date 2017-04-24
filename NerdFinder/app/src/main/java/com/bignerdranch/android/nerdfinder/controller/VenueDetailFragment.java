package com.bignerdranch.android.nerdfinder.controller;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import com.bignerdranch.android.nerdfinder.R;
import com.bignerdranch.android.nerdfinder.listener.VenueCheckInListener;
import com.bignerdranch.android.nerdfinder.model.Venue;
import com.bignerdranch.android.nerdfinder.web.DataManager;

import static android.view.View.GONE;
import static android.view.View.VISIBLE;

public class VenueDetailFragment extends Fragment implements VenueCheckInListener {
    private static final String ARG_VENUE_ID = "VenueDetailFragment.VenueId";
    private static final String EXPIRED_DIALOG = "expired_dialog";

    private DataManager mDataManager;
    private String mVenueId;
    private Venue mVenue;

    private TextView mVenueNameTextView;
    private TextView mVenueAddressTextView;
    private TextView mVenueCategoriesTextView;
    private TextView mNumberOfCheckInsTextView;
    private Button mCheckInButton;
    private View mCheckInButtonProgressBar;

    public static VenueDetailFragment newInstance(String venueId) {
        VenueDetailFragment fragment = new VenueDetailFragment();

        Bundle args = new Bundle();
        args.putString(ARG_VENUE_ID, venueId);
        fragment.setArguments(args);

        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_venue_detail, container, false);
        mVenueNameTextView = (TextView) view.findViewById(R.id.fragment_venue_detail_venue_name_text_view);
        mVenueAddressTextView = (TextView) view.findViewById(R.id.fragment_venue_detail_venue_address_text_view);
        mVenueCategoriesTextView = (TextView) view.findViewById(R.id.fragment_venue_detail_venue_categories);
        mNumberOfCheckInsTextView = (TextView) view.findViewById(R.id.fragment_venue_detail_number_of_check_ins);
        mCheckInButton = (Button) view.findViewById(R.id.fragment_venue_detail_check_in_button);
        mCheckInButtonProgressBar = view.findViewById(R.id.fragment_venue_detail_check_in_button_progress);
        return view;
    }

    @Override
    public void onStart() {
        super.onStart();
        mVenueId = getArguments().getString(ARG_VENUE_ID);
        mDataManager = DataManager.get(getContext());
        mDataManager.addVenueCheckInListener(this);
        mVenue = mDataManager.getVenue(mVenueId);
    }

    @Override
    public void onResume() {
        super.onResume();
        mVenueNameTextView.setText(mVenue.getName());
        mVenueAddressTextView.setText(mVenue.getFormattedAddress());
        if (!mVenue.getCategories().isEmpty()) {
            mVenueCategoriesTextView.setText(TextUtils.join(", ", mVenue.getCategories()));
            mVenueCategoriesTextView.setVisibility(VISIBLE);
        } else {
            mVenueCategoriesTextView.setVisibility(GONE);
        }
        mNumberOfCheckInsTextView.setText(
                getResources().getQuantityString(
                        R.plurals.venue_numberOfCheckIns,
                        mVenue.getStats().getCheckInsCount(),
                        mVenue.getStats().getCheckInsCount()
                )
        );
        mCheckInButton.setOnClickListener(mCheckInClickListener);
        mCheckInButton.setVisibility(mDataManager.isLoggedIn() ? VISIBLE : GONE);
    }

    @Override
    public void onStop() {
        super.onStop();
        mDataManager.removeVenueCheckInListener(this);
    }

    private View.OnClickListener mCheckInClickListener = new View.OnClickListener() {
        @Override
        public void onClick(View view) {
            mCheckInButton.setVisibility(GONE);
            mCheckInButtonProgressBar.setVisibility(VISIBLE);
            mDataManager.checkInToVenue(mVenueId);
        }
    };

    @Override
    public void onVenueCheckInFinished() {
        Toast
                .makeText(getContext(), R.string.successful_check_in_message, Toast.LENGTH_SHORT)
                .show();
        mCheckInButton.setVisibility(GONE);
        mCheckInButtonProgressBar.setVisibility(GONE);
    }

    @Override
    public void onTokenExpired() {
        mCheckInButton.setVisibility(GONE);
        mCheckInButtonProgressBar.setVisibility(GONE);
        final ExpiredTokenDialogFragment dialogFragment = new ExpiredTokenDialogFragment();
        dialogFragment.show(getFragmentManager(), EXPIRED_DIALOG);
    }
}
