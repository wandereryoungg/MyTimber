package com.young.timber.utils;

import android.content.Context;

import static com.young.timber.utils.Constants.PREFERENCES_NAME;

public class Helpers {

    public static String getATEKey(Context context) {
        return context.getSharedPreferences(PREFERENCES_NAME, Context.MODE_PRIVATE)
                .getBoolean("dark_theme", false) ? "dark_theme" : "light_theme";
    }
}
