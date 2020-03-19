package com.young.timber.dataloaders;

import android.content.Context;
import android.database.Cursor;
import android.provider.MediaStore;

import com.young.timber.models.Album;
import com.young.timber.utils.PreferencesUtility;

public class AlbumLoader {


    public static Album getAlbum(Context context, long id) {
        return getAlbum(makeAlbumCursor(context, "id=?", new String[]{String.valueOf(id)}));
    }

    public static Album getAlbum(Cursor cursor) {
        Album album = new Album();
        if (cursor != null && cursor.moveToFirst()) {
            album = new Album(cursor.getLong(0), cursor.getString(1), cursor.getString(2),
                    cursor.getLong(3), cursor.getInt(4), cursor.getInt(5));
        }
        if (cursor != null) {
            cursor.close();
        }
        return album;
    }

    public static Cursor makeAlbumCursor(Context context, String selection, String[] paramArrayOfString) {
        final String albumSortOrder = PreferencesUtility.getInstance(context).getAlbumSortOrder();
        Cursor cursor = context.getContentResolver().query(MediaStore.Audio.Albums.EXTERNAL_CONTENT_URI,
                new String[]{"_id", "album", "artist", "artist_id", "numsongs", "minyear"},
                selection, paramArrayOfString, albumSortOrder);
        return cursor;
    }

}
