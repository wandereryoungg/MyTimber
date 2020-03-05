package com.young.timber.utils;

import android.os.Build;

public class TimberUtils {

    public static boolean isMarshmallow(){
        return Build.VERSION.SDK_INT >= Build.VERSION_CODES.M;
    }

}
