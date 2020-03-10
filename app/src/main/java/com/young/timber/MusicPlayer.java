package com.young.timber;

import android.app.Activity;
import android.content.ComponentName;
import android.content.ContentValues;
import android.content.Context;
import android.content.ContextWrapper;
import android.content.Intent;
import android.content.ServiceConnection;
import android.os.IBinder;

import com.young.timber.utils.TimberUtils;

import java.util.WeakHashMap;

public class MusicPlayer {

    private static final WeakHashMap<Context, ServiceBinder> mConnectionMap;
    private static final long[] sEmptyList;
    private static ITimberService mService = null;
    private static ContentValues mContentValuesCache = null;


    static {
        mConnectionMap = new WeakHashMap<>();
        sEmptyList = new long[0];
    }

    public static final ServiceToken bindToService(final Context context,
                                                   final ServiceConnection callback) {
        Activity realActivity = ((Activity) context).getParent();
        if (realActivity == null) {
            realActivity = (Activity) context;
        }
        final ContextWrapper contextWrapper = new ContextWrapper(realActivity);
        contextWrapper.startService(new Intent(contextWrapper, MusicService.class));
        final ServiceBinder binder = new ServiceBinder(callback,contextWrapper.getApplicationContext());
        if(contextWrapper.bindService(new Intent().setClass(contextWrapper,MusicService.class),binder,Context.BIND_AUTO_CREATE)){
            mConnectionMap.put(contextWrapper,binder);
            return new ServiceToken(contextWrapper);
        }
        return null;

    }


    public static void playAll(final Context context, final long[] list, int position,
                               final long sourceId, final TimberUtils.IdType sourceType,
                               final boolean forceShuffle) {

    }

    public static final long getCurrentAudioId() {
        return 0;

    }

    public static final class ServiceBinder implements ServiceConnection {

        private final ServiceConnection mCallback;
        private final Context mContext;

        public ServiceBinder(ServiceConnection mCallback, Context context) {
            this.mCallback = mCallback;
            this.mContext = context;
        }

        @Override
        public void onServiceConnected(ComponentName name, IBinder service) {
            mService = ITimberService.Stub.asInterface(service);
            if (mCallback != null) {
                mCallback.onServiceConnected(name, service);
            }
            initPlaybackServiceWithSettings(mContext);
        }

        @Override
        public void onServiceDisconnected(ComponentName name) {
            if (mCallback != null) {
                mCallback.onServiceDisconnected(name);
            }
            mService = null;
        }
    }

    public static void initPlaybackServiceWithSettings(final Context context) {

    }

    public static final class ServiceToken {
        public ContextWrapper mWrappedContext;

        public ServiceToken(ContextWrapper context) {
            this.mWrappedContext = context;
        }
    }
}
