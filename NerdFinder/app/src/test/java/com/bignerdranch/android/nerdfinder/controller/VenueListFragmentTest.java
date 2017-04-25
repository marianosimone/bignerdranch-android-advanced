package com.bignerdranch.android.nerdfinder.controller;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.TextView;

import com.bignerdranch.android.nerdfinder.BuildConfig;
import com.bignerdranch.android.nerdfinder.R;
import com.bignerdranch.android.nerdfinder.SynchronousExecutorService;
import com.bignerdranch.android.nerdfinder.model.TokenStore;
import com.bignerdranch.android.nerdfinder.model.VenueSearchResponse;
import com.bignerdranch.android.nerdfinder.web.DataManager;
import com.bignerdranch.android.nerdfinder.web.TestDataManager;
import com.bignerdranch.android.nerdfinder.web.VenueListDeserializer;
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

import java.util.concurrent.ExecutorService;

import okhttp3.Dispatcher;
import okhttp3.OkHttpClient;
import retrofit2.Retrofit;
import retrofit2.adapter.rxjava.RxJavaCallAdapterFactory;
import retrofit2.converter.gson.GsonConverterFactory;

import static com.github.tomakehurst.wiremock.client.WireMock.aResponse;
import static com.github.tomakehurst.wiremock.client.WireMock.get;
import static com.github.tomakehurst.wiremock.client.WireMock.stubFor;
import static com.github.tomakehurst.wiremock.client.WireMock.urlMatching;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.core.Is.is;
import static org.hamcrest.core.IsNull.notNullValue;

@RunWith(RobolectricTestRunner.class)
@Config(sdk = 23, constants = BuildConfig.class)
public class VenueListFragmentTest {

    @Mock
    private Context mContext;

    @Rule
    public WireMockRule mWireMockRule;
    private String mEndpoint = "http://localhost:1111/";
    private DataManager mDataManager;
    private VenueListActivity mVenueListActivity;
    private VenueListFragment mVenueListFragment;

    public VenueListFragmentTest() {
        mWireMockRule = new WireMockRule(1111);
    }

    @Before
    public void setUp() {
        MockitoAnnotations.initMocks(this);
        final Gson gson = new GsonBuilder()
                .registerTypeAdapter(VenueSearchResponse.class, new VenueListDeserializer())
                .create();
        final ExecutorService executorService = new SynchronousExecutorService();
        final OkHttpClient client = new OkHttpClient.Builder()
                .dispatcher(new Dispatcher(executorService))
                .build();

        final Retrofit retrofit = new Retrofit.Builder()
                .baseUrl(mEndpoint)
                .client(client)
                .addConverterFactory(GsonConverterFactory.create(gson))
                .addCallAdapterFactory(RxJavaCallAdapterFactory.create())
                .build();
        final TokenStore tokenStore = TokenStore.get(RuntimeEnvironment.application);
        mDataManager = new TestDataManager(mContext, tokenStore, retrofit, retrofit);
        stubFor(get(urlMatching("/venues/search.*"))
                .willReturn(aResponse()
                        .withStatus(200)
                        .withBodyFile("search.json")));
        mVenueListActivity = Robolectric.buildActivity(VenueListActivity.class)
                .create().start().resume().get();
        mVenueListFragment = (VenueListFragment) mVenueListActivity
                .getSupportFragmentManager()
                .findFragmentById(R.id.fragmentContainer);
    }

    @Test
    public void activityListsVenuesReturnedFromSearch() {
        assertThat(mVenueListFragment, is(notNullValue()));
        final RecyclerView venueRecyclerView = (RecyclerView) mVenueListFragment.getView()
                .findViewById(R.id.venueListRecyclerView);
        assertThat(venueRecyclerView, is(notNullValue()));
        assertThat(venueRecyclerView.getAdapter().getItemCount(), is(2));
        venueRecyclerView.measure(0, 0);
        venueRecyclerView.layout(0, 0, 100, 1000);

        final String bnrTitle = "BNR Intergalactic Headquarters";
        final String rndTitle = "Ration and Dram";
        final View firstVenueView = venueRecyclerView.getChildAt(0);
        final TextView venueTitleTextView = (TextView) firstVenueView
                .findViewById(R.id.view_venue_list_VenueTitleTextView);
        assertThat(venueTitleTextView.getText(), is(bnrTitle));
        final View secondVenueView = venueRecyclerView.getChildAt(1);
        final TextView venueTitleTextView2 = (TextView) secondVenueView
                .findViewById(R.id.view_venue_list_VenueTitleTextView);
        assertThat(venueTitleTextView2.getText(), is(rndTitle));
    }
}
