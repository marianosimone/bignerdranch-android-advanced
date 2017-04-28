package com.bignerdranch.android.animals.web;

import com.bignerdranch.android.animals.model.GalleryItemList;
import com.google.gson.Gson;
import com.google.gson.JsonDeserializationContext;
import com.google.gson.JsonDeserializer;
import com.google.gson.JsonElement;
import com.google.gson.JsonParseException;

import java.lang.reflect.Type;

public class GalleryItemDeserializer implements JsonDeserializer<GalleryItemList> {
    @Override
    public GalleryItemList deserialize(JsonElement json, Type typeOfT,
                                       JsonDeserializationContext context)
            throws JsonParseException {
        JsonElement element = json.getAsJsonObject().get("photos");
        return new Gson().fromJson(element, GalleryItemList.class);
    }
}
