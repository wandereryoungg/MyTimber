package com.young.timber.utils;

import android.content.ContentResolver;
import android.content.ContentUris;
import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.net.Uri;
import android.os.Build;
import android.provider.BaseColumns;
import android.provider.MediaStore;

import androidx.annotation.NonNull;

import com.afollestad.materialdialogs.DialogAction;
import com.afollestad.materialdialogs.MaterialDialog;
import com.young.timber.adapters.BaseSongAdapter;

import java.net.InetAddress;
import java.net.NetworkInterface;
import java.net.SocketException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class TimberUtils {

    public static boolean isMarshmallow() {
        return Build.VERSION.SDK_INT >= Build.VERSION_CODES.M;
    }

    public static boolean isLollipop() {
        return Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP;
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

    public static void shareTrack(Context context, long id) {
        Intent share = new Intent(Intent.ACTION_SEND);
        share.setType("audio/*");
        share.putExtra(Intent.EXTRA_STREAM, getSongUri(context, id));
        context.startActivity(Intent.createChooser(share, "shared"));
    }

    public static void showDeleteDialog(final Context context, final String name, final long[] list, final BaseSongAdapter adapter, final int pos) {
        new MaterialDialog.Builder(context)
                .title("Delete song?")
                .content("Are you sure you want to delete \" + name + \" ?")
                .positiveText("Delete")
                .negativeText("Cancel")
                .onPositive(new MaterialDialog.SingleButtonCallback() {
                    @Override
                    public void onClick(@NonNull MaterialDialog dialog, @NonNull DialogAction which) {
                        TimberUtils.deleteTracks(context, list);
                        adapter.removeSongAt(pos);
                        adapter.notifyItemRemoved(pos);
                        adapter.notifyItemRangeChanged(pos, adapter.getItemCount());
                    }
                })
                .onNegative(new MaterialDialog.SingleButtonCallback() {
                    @Override
                    public void onClick(@NonNull MaterialDialog dialog, @NonNull DialogAction which) {
                        dialog.dismiss();
                    }
                })
                .show();
    }

    public static void deleteTracks(final Context context, final long[] lists) {

    }

    public static void deleteFromPlaylist(final Context context, final long id,
                                          final long playlistId) {
        final Uri uri = MediaStore.Audio.Playlists.Members.getContentUri("external", playlistId);
        final ContentResolver resolver = context.getContentResolver();
        resolver.delete(uri, MediaStore.Audio.Playlists.Members.AUDIO_ID + "=?", new String[]{Long.toString(id)});
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

    public static String getIPAddress(boolean useIPV4) {
        try {
            List<NetworkInterface> interfaces = Collections.list(NetworkInterface.getNetworkInterfaces());
            for (NetworkInterface intf : interfaces) {
                ArrayList<InetAddress> addresses = Collections.list(intf.getInetAddresses());
                for (InetAddress inetAddress : addresses) {
                    String address = inetAddress.getHostAddress();
                    boolean isIPV4 = address.indexOf(":") < 0;
                    if (useIPV4) {
                        if (isIPV4) {
                            return address;
                        } else if (!isIPV4) {
                            int delim = address.indexOf("%");
                            return delim < 0 ? address.toUpperCase() : address.substring(0, delim).toUpperCase();
                        }
                    }
                }


            }
        } catch (SocketException e) {
            e.printStackTrace();
        }
        return "";
    }

}
