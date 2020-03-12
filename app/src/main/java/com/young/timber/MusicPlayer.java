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

import static com.young.timber.MusicService.PREVIOUS_ACTION;
import static com.young.timber.MusicService.PREVIOUS_FORCE_ACTION;

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
        final ServiceBinder binder = new ServiceBinder(callback, contextWrapper.getApplicationContext());
        if (contextWrapper.bindService(new Intent().setClass(contextWrapper, MusicService.class), binder, 0)) {
            mConnectionMap.put(contextWrapper, binder);
            return new ServiceToken(contextWrapper);
        }
        return null;

    }

    public static void unbindFromService(final ServiceToken token) {
        if (token == null) {
            return;
        }
        final ContextWrapper mContextWrapper = token.mWrappedContext;
        final ServiceBinder mBinder = mConnectionMap.remove(mContextWrapper);
        if (mBinder == null) {
            return;
        }
        mContextWrapper.unbindService(mBinder);
        if (mConnectionMap == null) {
            mService = null;
        }
    }

    public static final boolean isPlaybackServiceConnected() {
        return mService != null;
    }

    public static void next() {
        try {
            if (mService != null) {
                mService.next();
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public static void previous(final Context context, final boolean force) {
        final Intent previous = new Intent(context, MusicService.class);
        if (force) {
            previous.setAction(PREVIOUS_FORCE_ACTION);
        } else {
            previous.setAction(PREVIOUS_ACTION);
        }
        context.startService(previous);
    }

    public static void playOrPause() {
        try {
            if (mService != null) {
                if (mService.isPlaying()) {
                    mService.pause();
                } else {
                    mService.play();
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public static void playAll(final Context context, final long[] list, int position,
                               final long sourceId, final TimberUtils.IdType sourceType,
                               final boolean forceShuffle) {

    }

    public static final long getCurrentAudioId() {
        try {
            if (mService != null) {
                return mService.getAudioId();
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return -1;
    }

    public static final boolean isPlaying(){
        try {
            if (mService!=null){
                return mService.isPlaying();
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return false;
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
