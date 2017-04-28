package com.bignerdranch.android.animals.model;

import com.google.gson.annotations.SerializedName;

import java.util.List;

public class GalleryItemList {

    @SerializedName("photo") List<GalleryItem> mGalleryItems;

    public List<GalleryItem> getGalleryItems() {
        return mGalleryItems;
    }
}
