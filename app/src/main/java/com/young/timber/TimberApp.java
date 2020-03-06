package com.young.timber;

import androidx.multidex.MultiDexApplication;

import com.young.timber.permissions.Young;

public class TimberApp extends MultiDexApplication {

    @Override
    public void onCreate() {
        super.onCreate();
        Young.init(this);
    }
}
