package com.bignerdranch.android.nerdfinder.web;

import android.content.Context;

import com.bignerdranch.android.nerdfinder.exception.UnauthorizedException;
import com.bignerdranch.android.nerdfinder.listener.VenueCheckInListener;
import com.bignerdranch.android.nerdfinder.listener.VenueSearchListener;
import com.bignerdranch.android.nerdfinder.model.TokenStore;
import com.bignerdranch.android.nerdfinder.model.Venue;
import com.bignerdranch.android.nerdfinder.model.VenueSearchResponse;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.JUnit4;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.MockitoAnnotations;

import java.util.Arrays;
import java.util.List;

import retrofit2.Retrofit;
import rx.Observable;

import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@RunWith(JUnit4.class)
public class DataManagerTest {

    private DataManager mDataManager;

    @Mock
    private Context mContext;

    @Mock
    private Retrofit mRetrofit;

    @Mock
    private Retrofit mAuthenticatedRetrofit;

    @Mock
    private TokenStore mTokenStore;

    @Mock
    private VenueInterface mVenueInterface;

    @Mock
    private VenueSearchListener mVenueSearchListener;

    @Mock
    private Venue firstVenue;

    @Mock
    private Venue secondVenue;

    @Mock
    private static VenueCheckInListener mVenueCheckInListener;

    @Before
    public void setUp() {
        MockitoAnnotations.initMocks(this);
        mDataManager = new TestDataManager(mContext, mTokenStore, mRetrofit, mAuthenticatedRetrofit);
        when(mRetrofit.create(VenueInterface.class))
                .thenReturn(mVenueInterface);
        when(mAuthenticatedRetrofit.create(VenueInterface.class))
                .thenReturn(mVenueInterface);
        when(firstVenue.getId()).thenReturn("1");
        when(secondVenue.getId()).thenReturn("2");
        mDataManager.addVenueSearchListener(mVenueSearchListener);
        mDataManager.addVenueCheckInListener(mVenueCheckInListener);
    }

    @Test
    public void searchListenerTriggeredOnSuccessfulSearch() {
        final VenueSearchResponse response = Mockito.mock(VenueSearchResponse.class);
        final List<Venue> venues = Arrays.asList(firstVenue, secondVenue);
        when(response.getVenueList()).thenReturn(venues);
        when(mVenueInterface.venueSearch(anyString())).thenReturn(Observable.just(response));
        mDataManager.fetchVenueSearch();
        verify(mVenueSearchListener).onVenueSearchFinished(venues);
    }

    @Test
    public void checkInListenerTriggeredOnSuccessfulCheckIn() {
        final Observable<Object> successObservable = Observable.just(new Object());
        when(mVenueInterface.venueCheckIn(anyString())).thenReturn(successObservable);
        final String fakeVenueId = "fakeVenueId";
        mDataManager.checkInToVenue(fakeVenueId);
        verify(mVenueCheckInListener).onVenueCheckInFinished();
    }

    @Test
    public void checkInListenerNotifiesTokenExpiredOnUnauthorizedException() {
        final Observable<Object> unauthorizedObservable =
                Observable.error(new UnauthorizedException());
        when(mVenueInterface.venueCheckIn(anyString()))
                .thenReturn(unauthorizedObservable);
        final String fakeVenueId = "fakeVenueId";
        mDataManager.checkInToVenue(fakeVenueId);
        verify(mVenueCheckInListener).onTokenExpired();
    }

    @Test
    public void checkInListenerDoesNotNotifyTokenExpiredOnPlainException() {
        final Observable<Object> runtimeObservable =
                Observable.error(new RuntimeException());
        when(mVenueInterface.venueCheckIn(anyString())).thenReturn(runtimeObservable);
        final String fakeVenueId = "fakeVenueId";
        mDataManager.checkInToVenue(fakeVenueId);
        verify(mVenueCheckInListener, never()).onTokenExpired();
    }
}
