package com.young.timber.permissions;

import android.app.Activity;
import android.content.Context;
import android.content.pm.PackageManager;

public class Young {

    private static Context context;

    public static boolean checkPermission(String permissionName){
        if(context == null){
            throw new RuntimeException("Before comparing permissions you need to call Young.init(context)");
        }
        return PackageManager.PERMISSION_GRANTED == context.checkSelfPermission(permissionName);
    }

    public static boolean shouldShowRequestPermissionRationale(Activity activity,String permission){
        return activity.shouldShowRequestPermissionRationale(permission);
    }

    public static void askForPermission(Activity activity,String[] permissions,PermissionCallback permissionCallback){
        if(permissionCallback == null){
            return;
        }
        if(hasPermission(activity,permissions)){
            permissionCallback.permissionGranted();
            return;
        }


    }

    public static boolean hasPermission(Activity activity,String[] permissions){
        for(String permission:permissions){
            if(activity.checkSelfPermission(permission)!=PackageManager.PERMISSION_GRANTED){
                return false;
            }
        }
        return true;
    }
}
