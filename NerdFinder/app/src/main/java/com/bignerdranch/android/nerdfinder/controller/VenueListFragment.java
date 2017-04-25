package com.bignerdranch.android.nerdfinder.controller;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v7.app.AlertDialog;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;

import com.bignerdranch.android.nerdfinder.R;
import com.bignerdranch.android.nerdfinder.listener.VenueSearchListener;
import com.bignerdranch.android.nerdfinder.model.TokenStore;
import com.bignerdranch.android.nerdfinder.model.Venue;
import com.bignerdranch.android.nerdfinder.view.VenueListAdapter;
import com.bignerdranch.android.nerdfinder.web.DataManager;

import java.util.Collections;
import java.util.List;

import static android.view.View.GONE;
import static android.view.View.VISIBLE;

public class VenueListFragment extends Fragment implements VenueSearchListener {
    private static final int AUTHENTICATION_ACTIVITY_REQUEST = 0;
    private static final String BUNDLE_SEARCH_QUERY = "VenueListFragment.SEARCH_QUERY";

    private VenueListAdapter mVenueListAdapter;
    private TokenStore mTokenStore;
    private DataManager mDataManager;
    private String mSearchQuery;

    public static VenueListFragment newInstance() {
        return new VenueListFragment();
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setHasOptionsMenu(true);
        mTokenStore = TokenStore.get(getContext());
    }

    @Override
    public View onCreateView(
            LayoutInflater inflater, @Nullable ViewGroup container,
            @Nullable Bundle savedInstanceState) {
        final View view = inflater.inflate(R.layout.fragment_venue_list, container, false);
        final RecyclerView recyclerView = (RecyclerView) view.findViewById(R.id.venueListRecyclerView);
        LinearLayoutManager layoutManager = new LinearLayoutManager(getContext());
        recyclerView.setLayoutManager(layoutManager);
        mVenueListAdapter = new VenueListAdapter(Collections.emptyList());
        recyclerView.setAdapter(mVenueListAdapter);
        return view;
    }

    @Override
    public void onStart() {
        super.onStart();
        mDataManager = DataManager.get(getContext());
        mDataManager.addVenueSearchListener(this);
        performSearch(mSearchQuery);
    }

    @Override
    public void onSaveInstanceState(final @NonNull Bundle outState) {
        super.onSaveInstanceState(outState);
        outState.putString(BUNDLE_SEARCH_QUERY, mSearchQuery);
    }

    @Override
    public void onViewStateRestored(final @Nullable Bundle savedInstanceState) {
        super.onViewStateRestored(savedInstanceState);
        if (savedInstanceState != null) {
            mSearchQuery = savedInstanceState.getString(BUNDLE_SEARCH_QUERY);
        }
    }

    private void performSearch(final @Nullable String query) {
        mSearchQuery = query;
        final View progressBar = getProgressBar();
        if (progressBar != null) {
            progressBar.setVisibility(VISIBLE);
        }
        if (TextUtils.isEmpty(mSearchQuery)) {
            mDataManager.fetchVenueSearch();
        } else {
            mDataManager.fetchVenueSearch(mSearchQuery);
        }
    }

    @Override
    public void onStop() {
        super.onStop();
        mDataManager.removeVenueSearchListener(this);
    }

    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        inflater.inflate(R.menu.menu_search_near, menu);
        if (mTokenStore.getAccessToken() == null) {
            inflater.inflate(R.menu.menu_sign_in, menu);
        } else {
            inflater.inflate(R.menu.menu_sign_out, menu);
        }
    }

    @Override
    public boolean onOptionsItemSelected(final @NonNull MenuItem item) {
        switch (item.getItemId()) {
            case R.id.sign_in:
                final Intent authenticationIntent = AuthenticationActivity
                        .newIntent(getContext());
                startActivityForResult(authenticationIntent,
                        AUTHENTICATION_ACTIVITY_REQUEST);
                return true;
            case R.id.sign_out:
                mTokenStore.setAccessToken(null);
                getActivity().invalidateOptionsMenu();
                return true;
            case R.id.search_near:
                showSearchDialog();
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }

    private void showSearchDialog() {
        final EditText queryTextField = new EditText(getContext());
        queryTextField.setHint(R.string.searchNear_hint);

        new AlertDialog.Builder(getContext())
                .setTitle(R.string.searchNear_title)
                .setMessage(R.string.searchNear_message)
                .setView(queryTextField)
                .setPositiveButton(R.string.searchNear_button, (dialog, whichButton) -> {
                    final String query = queryTextField.getText().toString();
                    if (TextUtils.isEmpty(query)) {
                        dialog.dismiss();
                    } else {
                        performSearch(query);
                    }
                })
                .setNegativeButton(
                        R.string.searchNear_cancel, (dialog, whichButton) -> dialog.dismiss())
                .show();
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, final @NonNull Intent data) {
        if (requestCode == AUTHENTICATION_ACTIVITY_REQUEST) {
            getActivity().invalidateOptionsMenu();
        }
        super.onActivityResult(requestCode, resultCode, data);
    }

    @Override
    public void onVenueSearchFinished(final @NonNull List<Venue> venues) {
        mVenueListAdapter.setVenueList(venues);
        final View progressBar = getProgressBar();
        if (progressBar != null) {
            progressBar.setVisibility(GONE);
        }
    }

    @Nullable
    private View getProgressBar() {
        return getView() != null ? getView().findViewById(R.id.venueListProgressBar) : null;
    }
}
