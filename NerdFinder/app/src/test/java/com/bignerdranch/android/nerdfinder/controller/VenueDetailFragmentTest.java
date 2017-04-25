package com.bignerdranch.android.nerdfinder.controller;

import android.app.AlertDialog;
import android.content.Intent;
import android.widget.Button;

import com.bignerdranch.android.nerdfinder.BuildConfig;
import com.bignerdranch.android.nerdfinder.R;
import com.bignerdranch.android.nerdfinder.SynchronousExecutorService;
import com.bignerdranch.android.nerdfinder.helper.NerdFinderSQLiteOpenHelper;
import com.bignerdranch.android.nerdfinder.model.TokenStore;
import com.bignerdranch.android.nerdfinder.model.VenueSearchResponse;
import com.bignerdranch.android.nerdfinder.web.DataManager;
import com.bignerdranch.android.nerdfinder.web.TestDataManager;
import com.bignerdranch.android.nerdfinder.web.VenueListDeserializer;
import com.bignerdranch.android.nerdfinder.web.interceptor.AuthorizationInterceptor;
import com.github.tomakehurst.wiremock.junit.WireMockRule;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.robolectric.Robolectric;
import org.robolectric.RobolectricTestRunner;
import org.robolectric.RuntimeEnvironment;
import org.robolectric.annotation.Config;
import org.robolectric.shadows.ShadowAlertDialog;
import org.robolectric.shadows.ShadowLooper;
import org.robolectric.shadows.ShadowToast;

import java.util.concurrent.ExecutorService;

import okhttp3.Dispatcher;
import okhttp3.OkHttpClient;
import retrofit2.Retrofit;
import retrofit2.adapter.rxjava.RxJavaCallAdapterFactory;
import retrofit2.converter.gson.GsonConverterFactory;

import static com.github.tomakehurst.wiremock.client.WireMock.aResponse;
import static com.github.tomakehurst.wiremock.client.WireMock.get;
import static com.github.tomakehurst.wiremock.client.WireMock.post;
import static com.github.tomakehurst.wiremock.client.WireMock.stubFor;
import static com.github.tomakehurst.wiremock.client.WireMock.urlMatching;
import static org.hamcrest.CoreMatchers.nullValue;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.core.Is.is;

@RunWith(RobolectricTestRunner.class)
@Config(sdk = 23, constants = BuildConfig.class)
public class VenueDetailFragmentTest {

    private static final String ENDPOINT = "http://localhost:1111/";

    @Mock
    private NerdFinderSQLiteOpenHelper mSQLiteOpenHelper;

    @Rule
    public WireMockRule mWireMockRule = new WireMockRule(1111);

    private VenueDetailActivity mVenueDetailActivity;

    private VenueDetailFragment mVenueDetailFragment;

    @Before
    public void setUp() {
        MockitoAnnotations.initMocks(this);
        final Gson gson = new GsonBuilder()
                .registerTypeAdapter(VenueSearchResponse.class,
                        new VenueListDeserializer())
                .create();
        final ExecutorService executorService = new SynchronousExecutorService();
        final OkHttpClient client = new OkHttpClient.Builder()
                .dispatcher(new Dispatcher(executorService))
                .build();
        final Retrofit basicRetrofit = new Retrofit.Builder()
                .baseUrl(ENDPOINT)
                .client(client)
                .addConverterFactory(GsonConverterFactory.create(gson))
                .addCallAdapterFactory(RxJavaCallAdapterFactory.create())
                .build();
        final OkHttpClient authenticatedClient = new OkHttpClient.Builder()
                .dispatcher(new Dispatcher(executorService))
                .addInterceptor(new AuthorizationInterceptor())
                .build();
        final Retrofit authenticatedRetrofit = new Retrofit.Builder()
                .baseUrl(ENDPOINT)
                .client(authenticatedClient)
                .addConverterFactory(GsonConverterFactory.create(gson))
                .addCallAdapterFactory(RxJavaCallAdapterFactory.create())
                .build();
        final TokenStore tokenStore = TokenStore.get(RuntimeEnvironment.application);
        tokenStore.setAccessToken("bogus token for testing");
        final DataManager dataManager = new TestDataManager(
                tokenStore, basicRetrofit, authenticatedRetrofit, mSQLiteOpenHelper);
        stubFor(get(urlMatching("/venues/search.*"))
                .willReturn(aResponse()
                        .withStatus(200)
                        .withBodyFile("search.json")));
        dataManager.fetchVenueSearch();
    }

    @Test
    public void toastShownOnSuccessfulCheckIn() {
        stubFor(post(urlMatching("/checkins/add.*"))
                .willReturn(aResponse()
                        .withStatus(200)
                        .withBody("{}")));
        final String bnrVenueId = "527c1d4f11d20f41ba39fc01";
        final Intent detailIntent = VenueDetailActivity
                .newIntent(RuntimeEnvironment.application, bnrVenueId);
        mVenueDetailActivity = Robolectric.buildActivity(VenueDetailActivity.class)
                .withIntent(detailIntent)
                .create().start().resume().get();
        mVenueDetailFragment = (VenueDetailFragment) mVenueDetailActivity
                .getSupportFragmentManager()
                .findFragmentById(R.id.fragment_container);
        final Button checkInButton = (Button) mVenueDetailFragment.getView()
                .findViewById(R.id.fragment_venue_detail_check_in_button);
        checkInButton.performClick();
        final String expectedToastText = mVenueDetailActivity
                .getString(R.string.successful_check_in_message);
        assertThat(ShadowToast.getTextOfLatestToast(), is(expectedToastText));
    }

    @Test
    public void errorDialogNotShownOnDifferentException() {
        stubFor(post(urlMatching("/checkins/add.*"))
                .willReturn(aResponse()
                        .withStatus(500)));
        final String bnrVenueId = "527c1d4f11d20f41ba39fc01";
        final Intent detailIntent = VenueDetailActivity
                .newIntent(RuntimeEnvironment.application, bnrVenueId);
        mVenueDetailActivity = Robolectric.buildActivity(VenueDetailActivity.class)
                .withIntent(detailIntent)
                .create().start().resume().get();
        mVenueDetailFragment = (VenueDetailFragment) mVenueDetailActivity
                .getSupportFragmentManager()
                .findFragmentById(R.id.fragment_container);
        final Button checkInButton = (Button) mVenueDetailFragment.getView()
                .findViewById(R.id.fragment_venue_detail_check_in_button);
        checkInButton.performClick();
        ShadowLooper.idleMainLooper();
        final AlertDialog errorDialog = ShadowAlertDialog.getLatestAlertDialog();
        assertThat(errorDialog, is(nullValue()));
    }
}
