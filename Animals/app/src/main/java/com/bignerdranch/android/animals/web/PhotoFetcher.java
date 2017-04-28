package com.bignerdranch.android.animals.web;

import android.text.TextUtils;
import android.util.Log;

import com.bignerdranch.android.animals.listener.PhotoSearchListener;
import com.bignerdranch.android.animals.model.GalleryItem;
import com.bignerdranch.android.animals.model.GalleryItemList;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

import java.util.ArrayList;
import java.util.List;

import okhttp3.OkHttpClient;
import okhttp3.logging.HttpLoggingInterceptor;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

public class PhotoFetcher {
    private static final String TAG = "PhotoFetcher";
    private static final String API_KEY = "d4049e2da567934e35d1e734469cdde0";
    private static final String ENDPOINT = "https://api.flickr.com/";

    private static PhotoFetcher sPhotoFetcher;

    private Retrofit mRetrofit;
    private List<PhotoSearchListener> mPhotoSearchListeners;
    private List<GalleryItem> mGalleryItems;

    public static PhotoFetcher get() {
        if (sPhotoFetcher == null) {
            sPhotoFetcher = new PhotoFetcher();
        }
        return sPhotoFetcher;
    }

    private PhotoFetcher() {
        mPhotoSearchListeners = new ArrayList<>();
        mGalleryItems = new ArrayList<>();
        Gson gson = new GsonBuilder()
                .registerTypeAdapter(GalleryItemList.class,
                        new GalleryItemDeserializer())
                .create();

        HttpLoggingInterceptor loggingInterceptor = new HttpLoggingInterceptor();
        loggingInterceptor.setLevel(HttpLoggingInterceptor.Level.BODY);

        OkHttpClient client = new OkHttpClient.Builder()
                .addInterceptor(loggingInterceptor)
                .build();

        mRetrofit = new Retrofit.Builder()
                .baseUrl(ENDPOINT)
                .client(client)
                .addConverterFactory(GsonConverterFactory.create(gson))
                .build();
    }

    public List<GalleryItem> getGalleryItems() {
        return mGalleryItems;
    }

    public void searchCats() {
        FlickrInterface flickrInterface = mRetrofit.create(FlickrInterface.class);
        flickrInterface.photoSearch(API_KEY, "cat").enqueue(new Callback<GalleryItemList>() {
            @Override
            public void onResponse(Call<GalleryItemList> call, Response<GalleryItemList> response) {
                Log.d(TAG, "Downloaded gallery item list");
                setupGalleryItems(response.body());
                notifyPhotoSearchListeners();
            }

            @Override
            public void onFailure(Call<GalleryItemList> call, Throwable t) {
                Log.e(TAG, "Failed to download cats", t);
            }
        });
    }

    public void searchDogs() {
        FlickrInterface flickrInterface = mRetrofit.create(FlickrInterface.class);
        flickrInterface.photoSearch(API_KEY, "dog").enqueue(new Callback<GalleryItemList>() {
            @Override
            public void onResponse(Call<GalleryItemList> call, Response<GalleryItemList> response) {
                Log.d(TAG, "Downloaded gallery item list");
                setupGalleryItems(response.body());
                notifyPhotoSearchListeners();
            }

            @Override
            public void onFailure(Call<GalleryItemList> call, Throwable t) {
                Log.e(TAG, "Failed to download cats", t);
            }
        });
    }

    private void setupGalleryItems(GalleryItemList galleryItemList) {
        mGalleryItems.clear();
        for (GalleryItem item : galleryItemList.getGalleryItems()) {
            if (!TextUtils.isEmpty(item.getUrl())) {
                mGalleryItems.add(item);
            }
        }
    }

    public void registerPhotoSearchListener(PhotoSearchListener photoSearchListener) {
        mPhotoSearchListeners.add(photoSearchListener);
    }

    public void unregisterPhotoSearchListener(
            PhotoSearchListener photoSearchListener) {
        mPhotoSearchListeners.remove(photoSearchListener);
    }

    private void notifyPhotoSearchListeners() {
        for (PhotoSearchListener listener : mPhotoSearchListeners) {
            listener.onPhotoSearchFinished();
        }
    }
}
