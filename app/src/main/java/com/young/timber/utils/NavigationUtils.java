package com.young.timber.utils;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;

import com.young.timber.activities.MainActivity;
import com.young.timber.activities.NowPlayingActivity;

public class NavigationUtils {

    public static void navigateToNowplaying(Activity context, boolean withAnimations) {
        Intent intent = new Intent(context, NowPlayingActivity.class);
        context.startActivity(intent);
    }

    public static void goToAlbum(Context context, long albumId) {
        Intent intent = new Intent(context, MainActivity.class);
        intent.setAction(Constants.NAVIGATE_ALBUM);
        intent.putExtra(Constants.ALBUM_ID, albumId);
        context.startActivity(intent);
    }

    public static void goToArtist(Context context, long artistId) {
        Intent intent = new Intent(context, MainActivity.class);
        intent.setAction(Constants.NAVIGATE_ARTIST);
        intent.putExtra(Constants.ARTIST_ID, artistId);
        context.startActivity(intent);
    }


}
