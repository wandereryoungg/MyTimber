package com.young.timber.activities;

import android.os.Bundle;
import android.view.View;

import com.google.android.gms.cast.framework.CastContext;
import com.google.android.gms.cast.framework.CastSession;
import com.google.android.gms.cast.framework.Session;
import com.google.android.gms.cast.framework.SessionManager;
import com.google.android.gms.cast.framework.SessionManagerListener;
import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.GoogleApiAvailability;
import com.young.timber.cast.SimpleSessionManagerListener;
import com.young.timber.cast.WebServer;
import com.young.timber.listeners.MusicStateListener;
import com.young.timber.slidinguppanel.SlidingUpPanelLayout;

import java.io.IOException;
import java.util.ArrayList;

public class BaseActivity extends ATEActivity implements MusicStateListener {

    private final ArrayList<MusicStateListener> mMusicStateListener = new ArrayList<>();
    private CastSession mCastSession;
    private SessionManager mSessionManager;
    private final SessionManagerListener mSessionManagerListener = new SessionManagerListenerImpl();
    private WebServer castServer;
    public boolean playServicesAvailable = false;

    @Override
    public void restartLoader() {

    }

    @Override
    public void onPlaylistChanged() {

    }

    @Override
    public void onMetaChanged() {

    }

    private class SessionManagerListenerImpl extends SimpleSessionManagerListener {
        @Override
        public void onSessionStarting(Session session) {
            super.onSessionStarting(session);
            startCastServer();
        }

        @Override
        public void onSessionStarted(Session session, String s) {
            invalidateOptionsMenu();
            mCastSession = mSessionManager.getCurrentCastSession();
            showCastMiniController();
        }

        @Override
        public void onSessionEnding(Session session) {
            super.onSessionEnding(session);
        }

        @Override
        public void onSessionEnded(Session session, int i) {
            mCastSession = null;
            hideCastMiniController();
            stopCastServer();
        }

        @Override
        public void onSessionResuming(Session session, String s) {
            super.onSessionResuming(session, s);
            startCastServer();
        }

        @Override
        public void onSessionResumed(Session session, boolean b) {
            invalidateOptionsMenu();
            mCastSession = mSessionManager.getCurrentCastSession();
        }

        @Override
        public void onSessionSuspended(Session session, int i) {
            super.onSessionSuspended(session, i);
            stopCastServer();
        }
    }

    private void startCastServer() {
        castServer = new WebServer(this);
        try {
            castServer.start();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private void stopCastServer() {
        if (castServer != null) {
            castServer.stop();
        }
    }

    public void showCastMiniController() {
        //implement by overriding in activities
    }

    public void hideCastMiniController() {
        //implement by overriding in activities
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);


        try {
            playServicesAvailable = GoogleApiAvailability.getInstance().isGooglePlayServicesAvailable(this) == ConnectionResult.SUCCESS;
        } catch (Exception e) {
            e.printStackTrace();
        }
        if (playServicesAvailable) {
            initCast();
        }
    }

    private void initCast() {
        CastContext castContext = CastContext.getSharedInstance(this);
        mSessionManager = castContext.getSessionManager();
    }

    public CastSession getCastSession() {
        return mCastSession;
    }

    public void setPanelSlideListeners(SlidingUpPanelLayout panelLayout) {
        panelLayout.setPanelSlideListener(new SlidingUpPanelLayout.PanelSlideListener() {
            @Override
            public void onPanelSlide(View panel, float slideOffset) {

            }

            @Override
            public void onPanelCollapsed(View panel) {

            }

            @Override
            public void onPanelExpanded(View panel) {

            }

            @Override
            public void onPanelAnchored(View panel) {

            }

            @Override
            public void onPanelHidden(View panel) {

            }
        });
    }

    public void setMusicStateListenerListener(final MusicStateListener status) {
        if (status == this) {
            throw new UnsupportedOperationException("Override the method, don't add a listener");
        }
        if (status != null) {
            mMusicStateListener.add(status);
        }
    }


}
