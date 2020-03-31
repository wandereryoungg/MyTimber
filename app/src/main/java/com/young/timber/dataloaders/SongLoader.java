package com.young.timber.dataloaders;

import android.content.ContentResolver;
import android.content.Context;
import android.database.Cursor;
import android.net.Uri;
import android.provider.MediaStore;
import android.text.TextUtils;

import com.young.timber.models.Song;
import com.young.timber.utils.PreferencesUtility;

import java.util.ArrayList;

public class SongLoader {


    public static ArrayList<Song> getAllSongs(Context context) {
        return getSongsForCursor(makeSongCursor(context, null, null));
    }

    public static Song getSongForCursor(Cursor cursor) {
        Song song = new Song();
        if (cursor != null && cursor.moveToFirst()) {
            long id = cursor.getLong(0);
            String title = cursor.getString(1);
            String artist = cursor.getString(2);
            String album = cursor.getString(3);
            int duration = cursor.getInt(4);
            int trackNumber = cursor.getInt(5);
            long artistId = cursor.getLong(6);
            long albumId = cursor.getLong(7);
            song = new Song(id, albumId, artistId, title, artist, album, duration, trackNumber);
        }
        if (cursor != null) {
            cursor.close();
            cursor = null;
        }
        return song;
    }

    public static ArrayList<Song> getSongsForCursor(Cursor cursor) {
        ArrayList<Song> songs = new ArrayList<>();
        if (cursor != null && cursor.moveToFirst()) {
            do {
                long id = cursor.getLong(0);
                String title = cursor.getString(1);
                String artist = cursor.getString(2);
                String album = cursor.getString(3);
                int duration = cursor.getInt(4);
                int trackNumber = cursor.getInt(5);
                long artistId = cursor.getLong(6);
                long albumId = cursor.getLong(7);
                songs.add(new Song(id, albumId, artistId, title, artist, album, duration, trackNumber));
            } while (cursor.moveToNext());
        }
        if (cursor != null) {
            cursor.close();
            cursor = null;
        }
        return songs;
    }

    public static Song getSongFromPath(String songPath, Context context) {
        ContentResolver contentResolver = context.getContentResolver();
        Uri uri = MediaStore.Audio.Media.EXTERNAL_CONTENT_URI;
        String selection = MediaStore.Audio.Media.DATA;
        String[] selectionArgs = {songPath};
        String[] projection = new String[]{"_id", "title", "artist", "album", "duration", "track", "artist_id", "album_id"};
        String sortOrder = MediaStore.Audio.Media.TITLE + "ASC";
        Cursor cursor = contentResolver.query(uri, projection, selection + "=?", selectionArgs, sortOrder);
        if (cursor != null && cursor.getCount() > 0) {
            Song song = getSongForCursor(cursor);
            return song;
        } else {
            return new Song();
        }
    }

    public static Cursor makeSongCursor(Context context, String selection, String[] paramArrayOfString) {
        final String songSortOrder = PreferencesUtility.getInstance(context).getSongSortOrder();
        return makeSongCursor(context, selection, paramArrayOfString, songSortOrder);
    }

    private static Cursor makeSongCursor(Context context, String selection, String[] paramArrayOfString, String sortOrder) {
        String selectionStatement = "is_music=1 AND title != ''";
        if (!TextUtils.isEmpty(selection)) {
            selectionStatement = selectionStatement + " AND " + selection;
        }
        return context.getContentResolver().query(MediaStore.Audio.Media.EXTERNAL_CONTENT_URI, new String[]{"_id", "title", "artist", "album", "duration", "track", "artist_id", "album_id"}, selectionStatement, paramArrayOfString, sortOrder);
    }
}
