package com.young.timber.utils;

import android.content.ContentResolver;
import android.content.ContentUris;
import android.content.Context;
import android.database.Cursor;
import android.net.Uri;
import android.os.Build;
import android.provider.BaseColumns;
import android.provider.MediaStore;

public class TimberUtils {

    public static boolean isMarshmallow() {
        return Build.VERSION.SDK_INT >= Build.VERSION_CODES.M;
    }

    public enum IdType {

        NA(0), Artist(1), Album(2), Playlist(3);

        public final int mId;

        IdType(final int mId) {
            this.mId = mId;
        }

        public static IdType getTypeById(int id) {
            for (IdType type : values()) {
                if (type.mId == id) {
                    return type;
                }
            }
            throw new IllegalArgumentException("Unrecognized id" + id);
        }
    }

    public static Uri getAlbumArtUri(long albumId) {
        return ContentUris.withAppendedId(Uri.parse("content://media/external/audio/albumart"), albumId);
    }

    public static Uri getSongUri(Context context, long songId) {
        final String[] projection = new String[]{
                BaseColumns._ID, MediaStore.MediaColumns.DATA, MediaStore.Audio.AudioColumns.ALBUM_ID};
        final StringBuilder selection = new StringBuilder();
        selection.append(BaseColumns._ID + "IN(");
        selection.append(songId);
        selection.append(")");
        final Cursor c = context.getContentResolver().query(MediaStore.Audio.Media.EXTERNAL_CONTENT_URI,
                projection, selection.toString(), null, null);
        if (c == null) {
            return null;
        }
        c.moveToFirst();
        try {
            Uri uri = Uri.parse(c.getString(1));
            c.close();
            return uri;
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }

}
