package com.bignerdranch.android.nerdmart.service;

import com.bignerdranch.android.nerdmart.BuildConfig;
import com.bignerdranch.android.nerdmart.TestNerdMartApplication;
import com.bignerdranch.android.nerdmart.inject.DaggerTestNerdMartComponent;
import com.bignerdranch.android.nerdmart.inject.NerdMartApplicationModule;
import com.bignerdranch.android.nerdmart.inject.NerdMartGraph;
import com.bignerdranch.android.nerdmart.inject.NerdMartServiceModule;
import com.bignerdranch.android.nerdmart.inject.TestInjector;
import com.bignerdranch.android.nerdmart.inject.TestNerdMartServiceModule;
import com.bignerdranch.android.nerdmart.model.DataStore;
import com.bignerdranch.android.nerdmart.model.service.NerdMartServiceManager;
import com.bignerdranch.android.nerdmartservice.model.NerdMartDataSourceInterface;
import com.bignerdranch.android.nerdmartservice.service.payload.Cart;
import com.bignerdranch.android.nerdmartservice.service.payload.Product;

import org.assertj.core.util.Lists;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.robolectric.RobolectricTestRunner;
import org.robolectric.RuntimeEnvironment;
import org.robolectric.annotation.Config;

import java.util.ArrayList;
import java.util.List;

import javax.inject.Inject;

import rx.observers.TestSubscriber;

import static org.assertj.core.api.Assertions.assertThat;

@RunWith(RobolectricTestRunner.class)
@Config(constants = BuildConfig.class, sdk = 23)
public class NerdMartServiceManagerTest {

    private static final String KNOWN_USER_NAME = "johnnydoe";

    private static final String KNOWN_USER_PASSWORD = "pizza";

    @Inject
    DataStore mDataStore;

    @Inject
    NerdMartServiceManager mNerdMartServiceManager;

    @Inject
    NerdMartDataSourceInterface mNerdMartDataSourceInterface;

    @Before
    public void setup() {
        final TestNerdMartApplication application
                = (TestNerdMartApplication) RuntimeEnvironment.application;
        final NerdMartApplicationModule applicationModule
                = new NerdMartApplicationModule(application);
        final NerdMartServiceModule serviceModule = new TestNerdMartServiceModule();
        final NerdMartGraph component = DaggerTestNerdMartComponent.builder()
                .nerdMartApplicationModule(applicationModule)
                .nerdMartServiceModule(serviceModule)
                .build();
        application.setComponent(component);
        TestInjector.obtain(application).inject(this);
    }

    @Test
    public void testAuthenticateMethodReturnsFalseWithInvalidCredentials() {
        final TestSubscriber<Boolean> subscriber = new TestSubscriber<>();
        mNerdMartServiceManager.authenticate(KNOWN_USER_NAME, KNOWN_USER_PASSWORD + "bad")
                .subscribe(subscriber);
        subscriber.awaitTerminalEvent();
        assertThat(subscriber.getOnNextEvents().get(0)).isEqualTo(false);
        assertThat(mDataStore.getCachedUser()).isEqualTo(null);
    }

    @Test
    public void testAuthenticateMethodReturnsTrueWithValidCredentials() {
        final TestSubscriber<Boolean> subscriber = new TestSubscriber<>();
        mNerdMartServiceManager.authenticate(KNOWN_USER_NAME, KNOWN_USER_PASSWORD)
                .subscribe(subscriber);
        subscriber.awaitTerminalEvent();
        assertThat(subscriber.getOnNextEvents().get(0)).isEqualTo(true);
        assertThat(mDataStore.getCachedUser())
                .isEqualTo(mNerdMartDataSourceInterface.getUser());
    }

    @Test
    public void testSignOutRemovesUserObjects() {
        final TestSubscriber<Boolean> subscriber = new TestSubscriber<>();
        mNerdMartServiceManager.authenticate(KNOWN_USER_NAME, KNOWN_USER_PASSWORD)
                .subscribe(subscriber);
        subscriber.awaitTerminalEvent();
        final TestSubscriber<Boolean> signOutSubscriber = new TestSubscriber<>();
        mNerdMartServiceManager.signOut().subscribe(signOutSubscriber);
        signOutSubscriber.awaitTerminalEvent();
        assertThat(mDataStore.getCachedUser()).isEqualTo(null);
        assertThat(mDataStore.getCachedCart()).isEqualTo(null);
    }

    @Test
    public void testGetProductsReturnsExpectedProductListings() {
        mDataStore.setCachedUser(mNerdMartDataSourceInterface.getUser());
        final TestSubscriber<List<Product>> subscriber = new TestSubscriber<>();
        mNerdMartServiceManager.getProducts().toList().subscribe(subscriber);
        subscriber.awaitTerminalEvent();
        assertThat(subscriber.getOnNextEvents()
                .get(0)).containsAll(mNerdMartDataSourceInterface.getProducts());
    }

    @Test
    public void testGetCartReturnsCartAndCachesCartInDataStore() {
        mDataStore.setCachedUser(mNerdMartDataSourceInterface.getUser());
        final TestSubscriber<Cart> subscriber = new TestSubscriber<>();
        mNerdMartServiceManager.getCart().subscribe(subscriber);
        subscriber.awaitTerminalEvent();
        final Cart actual = subscriber.getOnNextEvents().get(0);
        assertThat(actual).isNotEqualTo(null);
        assertThat(mDataStore.getCachedCart()).isEqualTo(actual);
        assertThat(mDataStore.getCachedCart().getProducts()).hasSize(0);
    }

    @Test
    public void testPostProductToCartAddsProductsToUserCart() {
        mDataStore.setCachedUser(mNerdMartDataSourceInterface.getUser());
        final ArrayList<Product> products = Lists.newArrayList();
        final TestSubscriber<Boolean> postProductsSubscriber = new TestSubscriber<>();
        products.addAll(mNerdMartDataSourceInterface.getProducts());
        mNerdMartServiceManager.postProductToCart(products.get(0))
                .subscribe(postProductsSubscriber);
        postProductsSubscriber.awaitTerminalEvent();
        assertThat(postProductsSubscriber.getOnNextEvents().get(0)).isEqualTo(true);
        final TestSubscriber<Cart> cartSubscriber = new TestSubscriber<>();
        mNerdMartServiceManager.getCart().subscribe(cartSubscriber);
        cartSubscriber.awaitTerminalEvent();
        Cart cart = cartSubscriber.getOnNextEvents().get(0);
        assertThat(cart.getProducts()).hasSize(1);
    }
}
