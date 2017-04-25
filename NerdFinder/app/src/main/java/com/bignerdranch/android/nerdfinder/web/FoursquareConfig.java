package com.bignerdranch.android.nerdfinder.web;

public interface FoursquareConfig {
    String FOURSQUARE_ENDPOINT = "https://api.foursquare.com/v2/";

    String CLIENT_ID
            = "JKP02TJ5V35Y501TNE0XIKB4DSAR1TIDCOJJQAGLUMJDG1DS";

    // Not that secret... but there's not much you can do with it, so... here it is :P
    String CLIENT_SECRET
            = "3Z3NISRYLLQJTJXIXLJCRTXA41KHAY35ZHWM40NK0U1HHBLN";

    String FOURSQUARE_VERSION = "20150406";

    String FOURSQUARE_MODE = "foursquare";

    String SWARM_MODE = "swarm";
}
