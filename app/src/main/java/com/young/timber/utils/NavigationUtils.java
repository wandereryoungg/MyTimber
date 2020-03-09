package com.young.timber.utils;

import android.app.Activity;
import android.content.Intent;

import com.young.timber.activities.NowPlayingActivity;

public class NavigationUtils {

    public static void navigateToNowplaying(Activity context,boolean withAnimations){
        Intent intent = new Intent(context, NowPlayingActivity.class);
        context.startActivity(intent);
    }

}
