package com.bignerdranch.android.animals.model;

import com.google.gson.annotations.SerializedName;

import java.io.Serializable;

public class GalleryItem implements Serializable {
    @SerializedName("title") String mCaption;
    @SerializedName("id") String mId;
    @SerializedName("url_s") String mUrl;

    public String getCaption() {
        return mCaption;
    }

    public void setCaption(String caption) {
        mCaption = caption;
    }

    public String getId() {
        return mId;
    }

    public void setId(String id) {
        mId = id;
    }

    public String getUrl() {
        return mUrl;
    }

    public void setUrl(String url) {
        mUrl = url;
    }

    @Override
    public String toString() {
        return mCaption;
    }
}
