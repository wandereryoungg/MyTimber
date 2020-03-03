package com.young.timber.utils;

import android.provider.MediaStore;

public final class SortOrder {

    public SortOrder() {
    }

    public interface ArtistSortOrder{
        String ARTIST_A_Z = MediaStore.Audio.Artists.DEFAULT_SORT_ORDER;
        String ARTIST_Z_A = ARTIST_A_Z+"DESC";
        String ARTIST_NUMBER_OF_SONGS =MediaStore.Audio.Artists.NUMBER_OF_TRACKS+"DESC";
        String ARTIST_NUMBER_OF_ALBUMS =MediaStore.Audio.Artists.NUMBER_OF_ALBUMS+"DESC";
    }

    public interface ArtistSongSortOrder {
        String SONG_A_Z = MediaStore.Audio.Media.DEFAULT_SORT_ORDER;
        String SONG_Z_A = SONG_A_Z + " DESC";
        String SONG_ALBUM = MediaStore.Audio.Media.ALBUM;
        String SONG_YEAR = MediaStore.Audio.Media.YEAR + " DESC";
        String SONG_DURATION = MediaStore.Audio.Media.DURATION + " DESC";
        String SONG_DATE = MediaStore.Audio.Media.DATE_ADDED + " DESC";
        String SONG_FILENAME = SongSortOrder.SONG_FILENAME;
    }

    public interface SongSortOrder {
        String SONG_A_Z = MediaStore.Audio.Media.DEFAULT_SORT_ORDER;
        String SONG_Z_A = SONG_A_Z + " DESC";
        String SONG_ARTIST = MediaStore.Audio.Media.ARTIST;
        String SONG_ALBUM = MediaStore.Audio.Media.ALBUM;
        String SONG_YEAR = MediaStore.Audio.Media.YEAR + " DESC";
        String SONG_DURATION = MediaStore.Audio.Media.DURATION + " DESC";
        String SONG_DATE = MediaStore.Audio.Media.DATE_ADDED + " DESC";
        String SONG_FILENAME = MediaStore.Audio.Media.DATA;
    }

}
