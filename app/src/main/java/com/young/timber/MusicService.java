package com.young.timber;

import android.app.AlarmManager;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.app.Service;
import android.content.BroadcastReceiver;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.database.ContentObserver;
import android.database.Cursor;
import android.media.AudioManager;
import android.media.MediaPlayer;
import android.media.RemoteControlClient;
import android.net.Uri;
import android.os.Handler;
import android.os.HandlerThread;
import android.os.IBinder;
import android.os.Message;
import android.os.PowerManager;
import android.os.Process;
import android.os.RemoteException;
import android.os.SystemClock;
import android.provider.MediaStore;
import android.support.v4.media.session.MediaSessionCompat;
import android.util.Log;

import androidx.annotation.Nullable;
import androidx.core.app.NotificationManagerCompat;

import com.young.timber.helpers.MusicPlaybackTrack;
import com.young.timber.provider.MusicPlaybackState;
import com.young.timber.provider.RecentStore;
import com.young.timber.provider.SongPlayCount;
import com.young.timber.utils.TimberUtils;

import java.io.IOException;
import java.lang.ref.WeakReference;
import java.util.ArrayList;
import java.util.LinkedList;
import java.util.Random;
import java.util.TreeSet;

public class MusicService extends Service {

    public static final String PLAYSTATE_CHANGED = "com.young.timber.playstatechanged";
    public static final String POSITION_CHANGED = "com.young.timber.positionchanged";
    public static final String META_CHANGED = "com.young.timber.metachanged";
    public static final String QUEUE_CHANGED = "com.young.timber.queuechanged";
    public static final String PLAYLIST_CHANGED = "com.young.timber.playlistchanged";
    public static final String REPEATMODE_CHANGED = "com.young.timber.repeatmodechanged";
    public static final String SHUFFLEMODE_CHANGED = "com.young.timber.shufflemodechanged";
    public static final String TRACK_ERROR = "com.young.timber.trackerror";
    public static final String TIMBER_PACKAGE_NAME = "com.young.timber";
    public static final String MUSIC_PACKAGE_NAME = "com.android.music";
    public static final String SERVICECMD = "com.young.timber.musicservicecommand";
    public static final String TOGGLEPAUSE_ACTION = "com.young.timber.togglepause";
    public static final String PAUSE_ACTION = "com.young.timber.pause";
    public static final String STOP_ACTION = "com.young.timber.stop";
    public static final String PREVIOUS_ACTION = "com.young.timber.previous";
    public static final String PREVIOUS_FORCE_ACTION = "com.young.timber.previous.force";
    public static final String NEXT_ACTION = "com.young.timber.next";
    public static final String REPEAT_ACTION = "com.young.timber.repeat";
    public static final String SHUFFLE_ACTION = "com.young.timber.shuffle";
    public static final String FROM_MEDIA_BUTTON = "frommediabutton";
    public static final String REFRESH = "com.young.timber.refresh";
    public static final String UPDATE_LOCKSCREEN = "com.young.timber.updatelockscreen";
    public static final String CMDNAME = "command";
    public static final String CMDTOGGLEPAUSE = "togglepause";
    public static final String CMDSTOP = "stop";
    public static final String CMDPAUSE = "pause";
    public static final String CMDPLAY = "play";
    public static final String CMDPREVIOUS = "previous";
    public static final String CMDNEXT = "next";
    public static final String CMDNOTIF = "buttonId";
    public static final String UPDATE_PREFERENCES = "updatepreferences";
    public static final String CHANNEL_ID = "timber_channel_01";
    public static final int NEXT = 2;
    public static final int LAST = 3;
    public static final int SHUFFLE_NONE = 0;
    public static final int SHUFFLE_NORMAL = 1;
    public static final int SHUFFLE_AUTO = 2;
    public static final int REPEAT_NONE = 0;
    public static final int REPEAT_CURRENT = 1;
    public static final int REPEAT_ALL = 2;
    public static final int MAX_HISTORY_SIZE = 1000;
    private static final String TAG = "MusicPlaybackService";
    private static final boolean D = false;
    private static final String SHUTDOWN = "com.young.timber.shutdown";
    private static final int IDCOLIDX = 0;
    private static final int TRACK_ENDED = 1;
    private static final int TRACK_WENT_TO_NEXT = 2;
    private static final int RELEASE_WAKELOCK = 3;
    private static final int SERVER_DIED = 4;
    private static final int FOCUSCHANGE = 5;
    private static final int FADEDOWN = 6;
    private static final int FADEUP = 7;
    private static final int IDLE_DELAY = 5 * 60 * 1000;
    private static final long REWIND_INSTEAD_PREVIOUS_THRESHOLD = 3000;
    private static final String[] PROJECTION = new String[]{"audio._id AS _id",
            MediaStore.Audio.Media.ARTIST, MediaStore.Audio.Media.ALBUM, MediaStore.Audio.Media.TITLE,
            MediaStore.Audio.Media.DATA, MediaStore.Audio.Media.MIME_TYPE, MediaStore.Audio.Media.ALBUM_ID,
            MediaStore.Audio.Media.ARTIST_ID
    };
    private static final String[] ALBUM_PROJECTION = new String[]{
            MediaStore.Audio.Media.ALBUM, MediaStore.Audio.Media.ARTIST,
            MediaStore.Audio.Albums.LAST_YEAR
    };
    private static final String[] NOTIFICATION_PROJECTION = new String[]{
            "audio._id AS _id", MediaStore.Audio.AudioColumns.ALBUM_ID, MediaStore.Audio.AudioColumns.TITLE,
            MediaStore.Audio.AudioColumns.ARTIST, MediaStore.Audio.AudioColumns.DURATION
    };
    private static final Shuffler mShuffler = new Shuffler();
    private static final int NOTIFY_MODE_NONE = 0;
    private static final int NOTIFY_MODE_FOREGROUND = 1;
    private static final int NOTIFY_MODE_BACKGROUND = 2;
    private static final String[] PROJECTION_MATRIX = new String[]{
            "_id", MediaStore.Audio.Media.ARTIST, MediaStore.Audio.Media.ALBUM,
            MediaStore.Audio.Media.TITLE, MediaStore.Audio.Media.DATA,
            MediaStore.Audio.Media.MIME_TYPE, MediaStore.Audio.Media.ALBUM_ID,
            MediaStore.Audio.Media.ARTIST_ID
    };
    private static LinkedList<Integer> mHistory = new LinkedList<>();
    private MultiPlayer mPlayer;
    private String mFileToPlay;
    private PowerManager.WakeLock mWakeLock;
    private AlarmManager mAlarmManager;
    private PendingIntent mShutdownIntent;
    private boolean mShutdownScheduled;
    private NotificationManagerCompat mNotificationManager;
    private Cursor mCursor;
    private Cursor mAlbumCursor;
    private AudioManager mAudioManager;
    private SharedPreferences mPreferences;
    private boolean mServiceInUse = false;
    private boolean mIsSupposedToBePlaying = false;
    private long mLastPlayedTime;
    private int mNotifyMode = NOTIFY_MODE_NONE;
    private long mNotificationPostTime = 0;
    private boolean mQueueIsSaveable = true;
    private boolean mPausedByTransientLossOfFocus = false;
    private MediaSessionCompat mSession;
    private RemoteControlClient mRemoteControlClient;

    private ComponentName mMediaButtonReceiverComponent;

    private int mCardId;

    private int mPlayPos = -1;

    private int mNextPlayPos = -1;

    private int mOpenFailedCounter = 0;

    private int mMediaMountedCount = 0;

    private int mShuffleMode = SHUFFLE_NONE;

    private int mRepeatMode = REPEAT_NONE;

    private int mServiceStartId = -1;
    private ArrayList<MusicPlaybackTrack> mPlaylist = new ArrayList<>(100);

    private long[] mAutoShuffleList = null;
    private MusicPlayerHandler mPlayerHander;
    private final AudioManager.OnAudioFocusChangeListener onAudioFocusChangeListener = new AudioManager.OnAudioFocusChangeListener() {
        @Override
        public void onAudioFocusChange(int focusChange) {

        }
    };
    private HandlerThread mHandlerThread;
    private BroadcastReceiver mUnmountReceiver = null;
    private MusicPlaybackState mPlaybackStateStore;
    private boolean mShowAlbumArtOnLockscreen;
    private boolean mActivateXTrackSelector;
    private SongPlayCount mSongPlayCount;
    private RecentStore mRecentStore;
    private final BroadcastReceiver mIntentReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {

        }
    };
    private ContentObserver mMediaStoreObserver;

    private final IBinder mBinder = new ServiceStub(this);


    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        if (D) {
            Log.d(TAG, "Service bound, intent = " + intent);
        }
        cancelShutdown();
        mServiceInUse = true;
        return mBinder;
    }

    @Override
    public boolean onUnbind(Intent intent) {
        if (D) {
            Log.d(TAG, "service unBind");
        }
        mServiceInUse = false;
        saveQueue(true);
        if (mIsSupposedToBePlaying || mPausedByTransientLossOfFocus) {
            return true;
        } else if (mPlaylist.size() > 0 || mPlayerHander.hasMessages(TRACK_ENDED)) {
            scheduleDelayedShutdown();
            return true;
        }
        stopSelf(mServiceStartId);
        return true;
    }

    @Override
    public void onRebind(Intent intent) {
        cancelShutdown();
        mServiceInUse = true;
    }

    private void scheduleDelayedShutdown() {
        if (D) Log.v(TAG, "Scheduling shutdown in " + IDLE_DELAY + " ms");
        mAlarmManager.set(AlarmManager.ELAPSED_REALTIME_WAKEUP,
                SystemClock.elapsedRealtime() + IDLE_DELAY, mShutdownIntent);
        mShutdownScheduled = true;
    }


    @Override
    public void onCreate() {
        super.onCreate();
        if (D) {
            Log.d(TAG, "Creating service");
        }
        mNotificationManager = NotificationManagerCompat.from(this);
        createNotificationChannel();
        mPlaybackStateStore = MusicPlaybackState.getInstance(this);
        mSongPlayCount = SongPlayCount.getInstance(this);
        mRecentStore = RecentStore.getInstance(this);
        mHandlerThread= new HandlerThread("MusicPlayerHandler", Process.THREAD_PRIORITY_BACKGROUND);
        mHandlerThread.start();


    }

    private void createNotificationChannel() {
        if (TimberUtils.isOreo()) {
            CharSequence name = "Timber";
            int importance = NotificationManager.IMPORTANCE_LOW;
            NotificationManager manager = (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);
            NotificationChannel mChannel = new NotificationChannel(CHANNEL_ID, name, importance);
            manager.createNotificationChannel(mChannel);
        }
    }

    private void saveQueue(final boolean full) {
        if (!mQueueIsSaveable) {
            return;
        }
        final SharedPreferences.Editor editor = mPreferences.edit();
        if (full) {
            mPlaybackStateStore.saveState(mPlaylist, mShuffleMode != SHUFFLE_NONE ? mHistory : null);
            editor.putInt("cardid", mCardId);
        }
        editor.putInt("curpos", mPlayPos);
        if (mPlayer.isInitialized()) {
            editor.putLong("seekpos", mPlayer.position());
        }
        editor.putInt("repeatmode", mRepeatMode);
        editor.putInt("shufflemode", mShuffleMode);
        editor.commit();
    }

    private void cancelShutdown() {
        if (D) {
            Log.d(TAG, "Cancelling delayed shutdown, scheduled = " + mShutdownScheduled);
        }
        if (mShutdownScheduled) {
            mAlarmManager.cancel(mShutdownIntent);
            mShutdownScheduled = false;
        }
    }

    private static final class ServiceStub extends ITimberService.Stub {

        private WeakReference<MusicService> mService;

        public ServiceStub(final MusicService service) {
            this.mService = new WeakReference<>(service);
        }

        @Override
        public void openFile(String path) throws RemoteException {

        }

        @Override
        public void open(long[] list, int position, long sourceId, int sourceType) throws RemoteException {

        }

        @Override
        public void stop() throws RemoteException {

        }

        @Override
        public void pause() throws RemoteException {

        }

        @Override
        public void play() throws RemoteException {

        }

        @Override
        public void prev(boolean forcePrevious) throws RemoteException {

        }

        @Override
        public void next() throws RemoteException {

        }

        @Override
        public void enqueue(long[] list, int action, long sourceId, int sourceType) throws RemoteException {

        }

        @Override
        public void setQueuePosition(int index) throws RemoteException {

        }

        @Override
        public void setShuffleMode(int shufflemode) throws RemoteException {

        }

        @Override
        public void setRepeatMode(int repeatmode) throws RemoteException {

        }

        @Override
        public void moveQueueItem(int from, int to) throws RemoteException {

        }

        @Override
        public void refresh() throws RemoteException {

        }

        @Override
        public void playlistChanged() throws RemoteException {

        }

        @Override
        public boolean isPlaying() throws RemoteException {
            return false;
        }

        @Override
        public long[] getQueue() throws RemoteException {
            return new long[0];
        }

        @Override
        public long getQueueItemAtPosition(int position) throws RemoteException {
            return 0;
        }

        @Override
        public int getQueueSize() throws RemoteException {
            return 0;
        }

        @Override
        public int getQueuePosition() throws RemoteException {
            return 0;
        }

        @Override
        public int getQueueHistoryPosition(int position) throws RemoteException {
            return 0;
        }

        @Override
        public int getQueueHistorySize() throws RemoteException {
            return 0;
        }

        @Override
        public int[] getQueueHistoryList() throws RemoteException {
            return new int[0];
        }

        @Override
        public long duration() throws RemoteException {
            return 0;
        }

        @Override
        public long position() throws RemoteException {
            return 0;
        }

        @Override
        public long seek(long pos) throws RemoteException {
            return 0;
        }

        @Override
        public void seekRelative(long deltaInMs) throws RemoteException {

        }

        @Override
        public long getAudioId() throws RemoteException {
            return 0;
        }

        @Override
        public MusicPlaybackTrack getCurrentTrack() throws RemoteException {
            return null;
        }

        @Override
        public MusicPlaybackTrack getTrack(int index) throws RemoteException {
            return null;
        }

        @Override
        public long getNextAudioId() throws RemoteException {
            return 0;
        }

        @Override
        public long getPreviousAudioId() throws RemoteException {
            return 0;
        }

        @Override
        public long getArtistId() throws RemoteException {
            return 0;
        }

        @Override
        public long getAlbumId() throws RemoteException {
            return 0;
        }

        @Override
        public String getArtistName() throws RemoteException {
            return null;
        }

        @Override
        public String getTrackName() throws RemoteException {
            return null;
        }

        @Override
        public String getAlbumName() throws RemoteException {
            return null;
        }

        @Override
        public String getPath() throws RemoteException {
            return null;
        }

        @Override
        public int getShuffleMode() throws RemoteException {
            return 0;
        }

        @Override
        public int removeTracks(int first, int last) throws RemoteException {
            return 0;
        }

        @Override
        public int removeTrack(long id) throws RemoteException {
            return 0;
        }

        @Override
        public boolean removeTrackAtPosition(long id, int position) throws RemoteException {
            return false;
        }

        @Override
        public int getRepeatMode() throws RemoteException {
            return 0;
        }

        @Override
        public int getMediaMountedCount() throws RemoteException {
            return 0;
        }

        @Override
        public int getAudioSessionId() throws RemoteException {
            return 0;
        }
    }

    private static final class Shuffler {
        private final LinkedList<Integer> mHistoryOfNumbers = new LinkedList<>();
        private final TreeSet<Integer> mPreviousNumbers = new TreeSet<>();
        private final Random random = new Random();
        private int mPrevious;

        public Shuffler() {
            super();
        }

        public int nextInt(final int interval) {
            int next;
            do {
                next = random.nextInt(interval);
            } while (next == mPrevious && interval > 1 && !mPreviousNumbers.contains(Integer.valueOf(next)));
            mPrevious = next;
            mHistoryOfNumbers.add(mPrevious);
            mPreviousNumbers.add(mPrevious);
            cleanUpHistory();
            return next;

        }

        private void cleanUpHistory() {
            if (!mHistoryOfNumbers.isEmpty() && mHistoryOfNumbers.size() >= MAX_HISTORY_SIZE) {
                for (int i = 0; i < Math.max(1, MAX_HISTORY_SIZE / 2); i++) {
                    mPreviousNumbers.remove(mHistoryOfNumbers.removeFirst());
                }
            }
        }
    }

    private static final class MultiPlayer implements MediaPlayer.OnErrorListener, MediaPlayer.OnCompletionListener {
        private final WeakReference<MusicService> mService;
        private MediaPlayer mCurrentMediaPlayer = new MediaPlayer();
        private MediaPlayer mNextMediaPlayer;
        private Handler mHandler;
        private boolean mIsInitialized = false;
        private String mNextMediaPath;

        public MultiPlayer(MusicService service) {
            this.mService = new WeakReference<>(service);
            mCurrentMediaPlayer.setWakeMode(mService.get(), PowerManager.PARTIAL_WAKE_LOCK);
        }

        public void setDataSource(final String path) {
            mIsInitialized = setDataSourceImpl(mCurrentMediaPlayer, path);
            if (mIsInitialized) {

            }
        }

        public void setNextDataSource(final String path) {
            mNextMediaPath = null;
            try {
                mCurrentMediaPlayer.setNextMediaPlayer(null);
            } catch (Exception e) {
                e.printStackTrace();
                return;
            }
            if (mNextMediaPlayer != null) {
                mNextMediaPlayer.release();
                mNextMediaPlayer = null;
            }
            if (path == null) {
                return;
            }
            mNextMediaPlayer = new MediaPlayer();
            mNextMediaPlayer.setWakeMode(mService.get(), PowerManager.PARTIAL_WAKE_LOCK);
            mNextMediaPlayer.setAudioSessionId(getAudioSessionId());
            if (setDataSourceImpl(mNextMediaPlayer, path)) {
                mNextMediaPath = path;
                mCurrentMediaPlayer.setNextMediaPlayer(mNextMediaPlayer);
            } else {
                if (mNextMediaPlayer != null) {
                    mNextMediaPlayer.release();
                    mNextMediaPlayer = null;
                }
            }

        }

        public void setHandler(final Handler handler) {
            mHandler = handler;
        }

        public boolean isInitialized() {
            return mIsInitialized;
        }

        public void start() {
            mCurrentMediaPlayer.start();
        }

        public void stop() {
            mCurrentMediaPlayer.reset();
            mIsInitialized = false;
        }

        public void release() {
            mCurrentMediaPlayer.release();
        }


        public void pause() {
            mCurrentMediaPlayer.pause();
        }


        public long duration() {
            return mCurrentMediaPlayer.getDuration();
        }


        public long position() {
            return mCurrentMediaPlayer.getCurrentPosition();
        }

        public long seek(final long whereto) {
            return whereto;
        }

        public void setVolume(final float vol) {
            try {
                mCurrentMediaPlayer.setVolume(vol, vol);
            } catch (Exception e) {
                e.printStackTrace();
            }
        }

        public void setAudioSessionId(final int sessionId) {
            mCurrentMediaPlayer.setAudioSessionId(sessionId);
        }

        public int getAudioSessionId() {
            return mCurrentMediaPlayer.getAudioSessionId();
        }


        private boolean setDataSourceImpl(final MediaPlayer player, final String path) {
            try {
                player.reset();
                player.setOnPreparedListener(null);
                if (path.startsWith("content://")) {
                    player.setDataSource(mService.get(), Uri.parse(path));
                } else {
                    player.setDataSource(path);
                }
                player.setAudioStreamType(AudioManager.STREAM_MUSIC);
                player.prepare();
            } catch (IOException e) {
                e.printStackTrace();
                return false;
            }
            player.setOnCompletionListener(this);
            player.setOnErrorListener(this);
            return true;
        }

        @Override
        public void onCompletion(MediaPlayer mp) {
            if (mp == mCurrentMediaPlayer && mNextMediaPlayer != null) {
                mCurrentMediaPlayer.release();
                mCurrentMediaPlayer = mNextMediaPlayer;
                mNextMediaPlayer = null;
                mNextMediaPath = null;
                mHandler.sendEmptyMessage(TRACK_WENT_TO_NEXT);
            } else {
                mService.get().mWakeLock.acquire(30000);
                mHandler.sendEmptyMessage(TRACK_ENDED);
                mHandler.sendEmptyMessage(RELEASE_WAKELOCK);
            }
        }

        @Override
        public boolean onError(MediaPlayer mp, int what, int extra) {
            switch (what) {
                case MediaPlayer.MEDIA_ERROR_SERVER_DIED:
                    final MusicService service = mService.get();
                    final TrackErrorInfo errorInfo = new TrackErrorInfo(service.getAudioId(), service.getTrackName());
                    mIsInitialized = false;
                    mCurrentMediaPlayer.release();
                    mCurrentMediaPlayer = new MediaPlayer();
                    mCurrentMediaPlayer.setWakeMode(service, PowerManager.PARTIAL_WAKE_LOCK);
                    Message msg = mHandler.obtainMessage(SERVER_DIED, errorInfo);
                    mHandler.sendMessageDelayed(msg, 2000);
                    return true;
                default:
                    break;
            }
            return false;
        }
    }

    public String getTrackName() {
        synchronized (this) {
            if (mCursor == null) {
                return null;
            }
            return mCursor.getString(mCursor.getColumnIndexOrThrow(MediaStore.Audio.AudioColumns.TITLE));
        }
    }

    public long getAudioId() {
        MusicPlaybackTrack track = getCurrentTrack();
        if (track != null) {
            return track.mId;
        }

        return -1;
    }

    public MusicPlaybackTrack getCurrentTrack() {
        return getTrack(mPlayPos);
    }

    public synchronized MusicPlaybackTrack getTrack(int index) {
        if (index >= 0 && index < mPlaylist.size() && mPlayer.isInitialized()) {
            return mPlaylist.get(index);
        }

        return null;
    }

    private static final class TrackErrorInfo {
        public long mId;
        public String mTrackName;

        public TrackErrorInfo(long id, String trackName) {
            mId = id;
            mTrackName = trackName;
        }
    }

    private static final class MusicPlayerHandler extends Handler {

    }


}
