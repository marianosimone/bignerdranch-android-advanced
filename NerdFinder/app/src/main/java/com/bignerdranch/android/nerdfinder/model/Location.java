package com.bignerdranch.android.nerdfinder.model;

import java.util.List;

public class Location {
    private double mLatitude;
    private double mLongitude;
    private List<String> mFormattedAddress;

    public String getFormattedAddress() {
        String formattedAddress = "";
        for (String addressPart : mFormattedAddress) {
            formattedAddress += addressPart;
            if (mFormattedAddress.indexOf(addressPart) !=
                    (mFormattedAddress.size() - 1)) {
                formattedAddress += " ";
            }
        }
        return formattedAddress;
    }
}
