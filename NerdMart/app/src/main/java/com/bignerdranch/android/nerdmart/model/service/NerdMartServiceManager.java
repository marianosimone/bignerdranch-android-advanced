package com.bignerdranch.android.nerdmart.model.service;

import android.support.annotation.NonNull;

import com.bignerdranch.android.nerdmart.model.DataStore;
import com.bignerdranch.android.nerdmartservice.service.NerdMartServiceInterface;
import com.bignerdranch.android.nerdmartservice.service.payload.Cart;
import com.bignerdranch.android.nerdmartservice.service.payload.Product;

import java.util.UUID;

import rx.Observable;
import rx.android.schedulers.AndroidSchedulers;
import rx.schedulers.Schedulers;

public class NerdMartServiceManager {

    @NonNull
    private final NerdMartServiceInterface mServiceInterface;

    @NonNull
    private final DataStore mDataStore;

    public NerdMartServiceManager(
            final @NonNull NerdMartServiceInterface serviceInterface,
            final @NonNull DataStore dataStore) {
        mServiceInterface = serviceInterface;
        mDataStore = dataStore;
    }

    private final Observable.Transformer<Observable, Observable>
            mSchedulersTransformer = observable ->
            observable.subscribeOn(Schedulers.newThread())
                    .observeOn(AndroidSchedulers.mainThread());

    @SuppressWarnings("unchecked")
    private <T> Observable.Transformer<T, T> applySchedulers() {
        return (Observable.Transformer<T, T>) mSchedulersTransformer;
    }

    public Observable<Boolean> authenticate(
            final @NonNull String username, final @NonNull String password) {
        return mServiceInterface.authenticate(username, password)
                .doOnNext(mDataStore::setCachedUser)
                .map(user -> user != null)
                .compose(applySchedulers());
    }

    private Observable<UUID> getToken() {
        return Observable.just(mDataStore.getCachedAuthToken());
    }

    public Observable<Product> getProducts() {
        return getToken().flatMap(mServiceInterface::requestProducts)
                .doOnNext(mDataStore::setCachedProducts)
                .flatMap(Observable::from)
                .compose(applySchedulers());
    }

    public Observable<Cart> getCart() {
        return getToken().flatMap(mServiceInterface::fetchUserCart)
                .doOnNext(mDataStore::setCachedCart)
                .compose(applySchedulers());
    }

    public Observable<Boolean> postProductToCart(final @NonNull Product product) {
        return getToken()
                .flatMap(uuid -> mServiceInterface.addProductToCart(uuid, product))
                .compose(applySchedulers());
    }

    public Observable<Boolean> signOut() {
        mDataStore.clearCache();
        return mServiceInterface.signout();
    }
}
