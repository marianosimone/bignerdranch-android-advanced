package com.bignerdranch.android.nerdfinder.view;

import android.content.Context;
import android.support.annotation.Nullable;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.bignerdranch.android.nerdfinder.R;
import com.bignerdranch.android.nerdfinder.model.Category;
import com.squareup.picasso.Picasso;

public class VenueView extends LinearLayout {
    private TextView mTitleTextView;
    private TextView mAddressTextView;
    private ImageView mCategoryIcon;
    private int mCategoryIconSize;

    public VenueView(Context context) {
        this(context, null);
    }

    public VenueView(Context context, AttributeSet attrs) {
        super(context, attrs);
        setOrientation(VERTICAL);
        final LinearLayout.LayoutParams params = new LayoutParams(
                ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT);
        params.setMargins(0, 0, 0, 16);
        setLayoutParams(params);
        mCategoryIconSize = (int) getResources().getDimension(R.dimen.view_venue_list_VenueIconSize);

        final LayoutInflater inflater = LayoutInflater.from(context);
        final VenueView view = (VenueView) inflater.inflate(
                R.layout.view_venue, this, true);
        mTitleTextView = (TextView) view.findViewById(R.id.view_venue_list_VenueTitleTextView);
        mAddressTextView = (TextView) view.findViewById(R.id.view_venue_list_VenueLocationTextView);
        mCategoryIcon = (ImageView) view.findViewById(R.id.view_venue_list_VenueIconImageView);
    }

    public void setVenueTitle(String title) {
        mTitleTextView.setText(title);
    }

    public void setVenueAddress(String address) {
        mAddressTextView.setText(address);
    }

    public void setCategory(final @Nullable Category category) {
        // TODO: Tint icon (http://developerbits.blogspot.com/2015/05/android-dynamically-coloring-images.html ?)
        // TODO: Use a default image when non available
        if (category != null) {
            mCategoryIcon.setContentDescription(category.getName());
            Picasso.with(getContext())
                    .load(category.getIcon().getUrl())
                    .resize(mCategoryIconSize, mCategoryIconSize)
                    .into(mCategoryIcon);
            mCategoryIcon.setVisibility(VISIBLE);
        } else {
            mCategoryIcon.setVisibility(INVISIBLE);
        }
    }
}
