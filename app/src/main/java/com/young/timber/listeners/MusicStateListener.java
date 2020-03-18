package com.young.timber.listeners;

public interface MusicStateListener {

    void restartLoader();

    void onPlaylistChanged();

    void onMetaChanged();
}
