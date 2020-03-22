package com.young.timber.utils;

import android.app.Activity;
import android.content.res.ColorStateList;
import android.graphics.Color;
import android.os.Build;
import android.view.Window;

import com.afollestad.appthemeengine.Config;
import com.google.android.material.floatingactionbutton.FloatingActionButton;

public class ATEUtils {

    public static void setStatusBarColor(Activity activity, String key, int color) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            final Window window = activity.getWindow();
            if (Config.coloredStatusBar(activity, key)) {
                window.setStatusBarColor(getStatusBarColor(color));
            } else {
                window.setStatusBarColor(Color.BLACK);
            }
            if (Config.coloredNavigationBar(activity, key)) {
                window.setNavigationBarColor(color);
            } else {
                window.setNavigationBarColor(Color.BLACK);
            }

        }


    }

    public static void setFabBackgroundTint(FloatingActionButton fab, int color) {
        ColorStateList fabColorStateList = new ColorStateList(
                new int[][]{
                        new int[]{}
                }, new int[]{
                color
        }
        );
        fab.setBackgroundTintList(fabColorStateList);
    }

    private static void applyTaskDescription(Activity activity, String key, int color) {

    }

    public static int getStatusBarColor(int primaryColor) {
        float[] arrayOfFloat = new float[3];
        Color.colorToHSV(primaryColor, arrayOfFloat);
        arrayOfFloat[2] *= 0.9F;
        return Color.HSVToColor(arrayOfFloat);
    }

}
