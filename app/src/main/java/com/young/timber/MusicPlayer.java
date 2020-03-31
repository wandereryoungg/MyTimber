package com.young.timber;

import android.app.Activity;
import android.content.ComponentName;
import android.content.ContentResolver;
import android.content.ContentValues;
import android.content.Context;
import android.content.ContextWrapper;
import android.content.Intent;
import android.content.ServiceConnection;
import android.database.Cursor;
import android.net.Uri;
import android.os.IBinder;
import android.os.RemoteException;
import android.provider.MediaStore;
import android.widget.Toast;

import com.young.timber.utils.TimberUtils;

import java.util.Arrays;
import java.util.WeakHashMap;

import static com.young.timber.MusicService.LAST;
import static com.young.timber.MusicService.NEXT;
import static com.young.timber.MusicService.PREVIOUS_ACTION;
import static com.young.timber.MusicService.PREVIOUS_FORCE_ACTION;

public class MusicPlayer {

    private static final WeakHashMap<Context, ServiceBinder> mConnectionMap;
    private static final long[] sEmptyList;
    private static ITimberService mService = null;
    private static ContentValues[] mContentValuesCache = null;


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
        if (list == null || list.length == 0 || context == null) {
            return;
        }
        try {
            if (forceShuffle) {
                mService.setShuffleMode(MusicService.SHUFFLE_NORMAL);
                final long currentId = mService.getAudioId();
                final int currentQueuePosition = getQueuePosition();
                if (position != -1 && currentQueuePosition == position && currentId == list[position]) {
                    final long[] playlist = getQueue();
                    if (Arrays.equals(list, playlist)) {
                        mService.play();
                        return;
                    }
                }
                if (position < 0) {
                    position = 0;
                }
                mService.open(list, forceShuffle ? -1 : position, sourceId, sourceType.mId);
                mService.play();
            }
        } catch (Exception e) {
            e.printStackTrace();
        }

    }

    public static void playNext(final Context context, final long[] list, final long sourceId, final TimberUtils.IdType sourceType) {
        if (mService == null) {
            return;
        }
        try {
            mService.enqueue(list, NEXT, sourceId, sourceType.mId);
            final String message = makeLabel(context, R.plurals.NNNtrackstoqueue, list.length);
            Toast.makeText(context, message, Toast.LENGTH_SHORT).show();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public static void addToQueue(final Context context, final long[] list, final long sourceId, final TimberUtils.IdType sourceType) {
        if (mService == null) {
            return;
        }
        try {
            mService.enqueue(list, LAST, sourceId, sourceType.mId);
            final String message = makeLabel(context, R.plurals.NNNtrackstoqueue, list.length);
            Toast.makeText(context, message, Toast.LENGTH_SHORT).show();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public static final String makeLabel(final Context context, final int pluralInt, final int number) {
        return context.getResources().getQuantityString(pluralInt, number, number);
    }

    public static void addToPlaylist(final Context context, final long[] ids, final long playlistid) {
        final int size = ids.length;
        final ContentResolver contentResolver = context.getContentResolver();
        final String[] projection = new String[]{"max(" + "play_order" + ")"};
        final Uri uri = MediaStore.Audio.Playlists.Members.getContentUri("external", playlistid);
        Cursor cursor = null;
        int base = 0;
        try {
            cursor = contentResolver.query(uri, projection, null, null, null);
            if (cursor != null && cursor.moveToFirst()) {
                base = cursor.getInt(0) + 1;
            }
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            if ((cursor != null)) {
                cursor.close();
                cursor = null;
            }
        }
        int numinserted = 0;
        for (int offset = 0; offset < size; offset += 1000) {
            makeInsertItems(ids, offset, 1000, base);
            numinserted += contentResolver.bulkInsert(uri, mContentValuesCache);
        }
        final String message = context.getResources().getQuantityString(R.plurals.NNNtrackstoplaylist, numinserted, numinserted);
        Toast.makeText(context, message, Toast.LENGTH_SHORT).show();
    }

    public static void makeInsertItems(final long[] ids, final int offset, int len, final int base) {
        if (offset + len > ids.length) {
            len = ids.length - offset;
        }
        if (mContentValuesCache == null || mContentValuesCache.length != len) {
            mContentValuesCache = new ContentValues[len];
        }
        for (int i = 0; i < len; i++) {
            if (mContentValuesCache[i] == null) {
                mContentValuesCache[i] = new ContentValues();
            }
            mContentValuesCache[i].put(MediaStore.Audio.Playlists.Members.PLAY_ORDER, base + offset + i);
            mContentValuesCache[i].put(MediaStore.Audio.Playlists.Members.AUDIO_ID, offset + i);
        }
    }

    public static final long createPlaylist(final Context context, final String name) {
        if (name != null && name.length() > 0) {
            ContentResolver contentResolver = context.getContentResolver();
            final String[] projection = new String[]{MediaStore.Audio.PlaylistsColumns.NAME};
            final String selection = MediaStore.Audio.PlaylistsColumns.NAME + "=" + name + "'";
            Cursor cursor = contentResolver.query(MediaStore.Audio.Playlists.EXTERNAL_CONTENT_URI, projection, selection, null, null);
            if (cursor.getCount() <= 0) {
                final ContentValues contentValues = new ContentValues();
                contentValues.put(MediaStore.Audio.PlaylistsColumns.NAME, name);
                final Uri uri = contentResolver.insert(MediaStore.Audio.Playlists.EXTERNAL_CONTENT_URI, contentValues);
                return Long.parseLong(uri.getLastPathSegment());
            }
            if (cursor != null) {
                cursor.close();
                cursor = null;
            }
            return -1;
        }
        return -1;
    }

    public static final long[] getQueue() {
        try {
            if (mService != null) {
                return mService.getQueue();
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return sEmptyList;
    }

    public static final int getQueuePosition() {
        try {
            if (mService != null) {
                return mService.getQueuePosition();
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return 0;
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

    public static final boolean isPlaying() {
        try {
            if (mService != null) {
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

    public static void clearQueue() {
        if (mService != null) {
            try {
                mService.removeTracks(0, Integer.MAX_VALUE);
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }

    public static final void openFile(final String path) {
        if (mService != null) {
            try {
                mService.openFile(path);
            } catch (RemoteException e) {
                e.printStackTrace();
            }
        }
    }

    public static final String getTrackName() {
        if (mService != null) {
            try {
                return mService.getTrackName();
            } catch (RemoteException e) {
                e.printStackTrace();
            }
        }
        return null;
    }

    public static final String getArtistName(){
        if(mService !=null){
            try {
                return mService.getArtistName();
            } catch (RemoteException e) {
                e.printStackTrace();
            }
        }
        return null;
    }

    public static final long getCurrentAlbumId(){
        if(mService!=null){
            try {
                return mService.getAlbumId();
            } catch (RemoteException e) {
                e.printStackTrace();
            }
        }
        return -1;
    }


}
