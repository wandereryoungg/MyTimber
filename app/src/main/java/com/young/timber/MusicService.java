package com.young.timber;

import android.app.Service;
import android.content.Intent;
import android.os.IBinder;
import android.os.RemoteException;

import androidx.annotation.Nullable;

import com.young.timber.helpers.MusicPlaybackTrack;

import java.lang.ref.WeakReference;

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

    private final IBinder mBinder = new ServiceStub(this);



    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        return mBinder;
    }

    private static final class ServiceStub extends ITimberService.Stub{

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


}
