package com.young.timber.cast;

import android.content.ContentResolver;
import android.content.Context;
import android.net.Uri;

import com.young.timber.utils.Constants;
import com.young.timber.utils.TimberUtils;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.InputStream;
import java.util.Map;

import fi.iki.elonen.NanoHTTPD;

public class WebServer extends NanoHTTPD {

    private Context context;
    private Uri songUri, albumArtUri;

    public WebServer(Context context) {
        super(Constants.CAST_SERVER_PORT);
        this.context = context;
    }

    @Override
    public Response serve(String uri, Method method,
                          Map<String, String> headers,
                          Map<String, String> parms,
                          Map<String, String> files) {
        if (uri.contains("albumart")) {
            String albumId = parms.get("id");
            this.albumArtUri = TimberUtils.getAlbumArtUri(Long.parseLong(albumId));
            if (albumArtUri != null) {
                String mediasend = "image/jpg";
                InputStream fisAlbumArt = null;
                try {
                    fisAlbumArt = context.getContentResolver().openInputStream(albumArtUri);
                } catch (FileNotFoundException e) {
                    e.printStackTrace();
                }
                Response.Status status = Response.Status.OK;
                return newChunkedResponse(status, mediasend, fisAlbumArt);
            }
        } else if (uri.contains("song")) {
            String songId = parms.get("id");
            this.songUri = TimberUtils.getSongUri(context, Long.parseLong(songId));
            if (songUri != null) {
                String mediasend = "audio/mp3";
                FileInputStream fisSong = null;
                File song = new File(songUri.getPath());
                try {
                    fisSong = new FileInputStream(song);
                } catch (FileNotFoundException e) {
                    e.printStackTrace();
                }
                Response.Status status = Response.Status.OK;
                return newFixedLengthResponse(status, mediasend, fisSong, song.length());
            }

        }
        return newFixedLengthResponse("Error");
    }
}
