/*
 * Copyright (C) 2015 Naman Dwivedi
 *
 * Licensed under the GNU General Public License v3
 *
 * This is free software: you can redistribute it and/or modify it
 * under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or (at your option) any later version.
 *
 * This software is distributed in the hope that it will be useful, but WITHOUT ANY WARRANTY;
 * without even the implied warranty of MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.
 * See the GNU General Public License for more details.
 */

package com.young.timber.lastfmapi;

import android.content.Context;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.util.Log;

import com.young.timber.lastfmapi.callbacks.AlbumInfoListener;
import com.young.timber.lastfmapi.callbacks.ArtistInfoListener;
import com.young.timber.lastfmapi.callbacks.UserListener;
import com.young.timber.lastfmapi.models.AlbumInfo;
import com.young.timber.lastfmapi.models.AlbumQuery;
import com.young.timber.lastfmapi.models.ArtistInfo;
import com.young.timber.lastfmapi.models.ArtistQuery;
import com.young.timber.lastfmapi.models.LastfmUserSession;
import com.young.timber.lastfmapi.models.ScrobbleInfo;
import com.young.timber.lastfmapi.models.ScrobbleQuery;
import com.young.timber.lastfmapi.models.UserLoginInfo;
import com.young.timber.lastfmapi.models.UserLoginQuery;
import com.young.timber.utils.PreferencesUtility;

import java.io.UnsupportedEncodingException;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.HashSet;
import java.util.Map;
import java.util.TreeMap;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class LastFmClient {

    //TODO update the api keys
    public static final String API_KEY = "62ac1851456e4558bef1c41747b1aec2";
    public static final String API_SECRET = "b4ae8965723d67fb18e35d207014d6f3";

    public static final String JSON = "json";

    public static final String BASE_API_URL = "http://ws.audioscrobbler.com/2.0";
    public static final String BASE_SECURE_API_URL = "https://ws.audioscrobbler.com/2.0";

    public static final String PREFERENCES_NAME = "Lastfm";
    static final String PREFERENCE_CACHE_NAME = "Cache";

    private static LastFmClient sInstance;
    private LastFmRestService mRestService;
    private LastFmUserRestService mUserRestService;

    private HashSet<String> queries;
    private boolean isUploading = false;

    private Context context;

    private LastfmUserSession mUserSession;
    private static final Object sLock = new Object();

    public static LastFmClient getInstance(Context context) {
        synchronized (sLock) {
            if (sInstance == null) {
                sInstance = new LastFmClient();
                sInstance.context = context;
                sInstance.mRestService = RestServiceFactory.createStatic(context, BASE_API_URL, LastFmRestService.class);
                sInstance.mUserRestService = RestServiceFactory.create(context, BASE_SECURE_API_URL, LastFmUserRestService.class);
                sInstance.mUserSession = LastfmUserSession.getSession(context);

            }
            return sInstance;
        }
    }

    private static String generateMD5(String in) {
        try {
            byte[] bytesOfMessage = in.getBytes("UTF-8");
            MessageDigest md = MessageDigest.getInstance("MD5");
            byte[] digest = md.digest(bytesOfMessage);
            String out = "";
            for (byte symbol : digest) {
                out += String.format("%02X", symbol);
            }
            return out;
        } catch (UnsupportedEncodingException | NoSuchAlgorithmException ignored) {
            return null;
        }


    }

    public void getAlbumInfo(AlbumQuery albumQuery, final AlbumInfoListener listener) {
        mRestService.getAlbumInfo(albumQuery.mArtist, albumQuery.mALbum, new Callback<AlbumInfo>() {
            @Override
            public void onResponse(Call<AlbumInfo> call, Response<AlbumInfo> response) {
                listener.albumInfoSuccess(response.body().mAlbum);
            }

            @Override
            public void onFailure(Call<AlbumInfo> call, Throwable t) {
                listener.albumInfoFailed();
                t.printStackTrace();
            }
        });
    }

    public void getArtistInfo(ArtistQuery artistQuery, final ArtistInfoListener listener) {
        mRestService.getArtistInfo(artistQuery.mArtist, new Callback<ArtistInfo>() {
            @Override
            public void onResponse(Call<ArtistInfo> call, Response<ArtistInfo> response) {
                listener.artistInfoSucess(response.body().mArtist);
            }

            @Override
            public void onFailure(Call<ArtistInfo> call, Throwable t) {
                listener.artistInfoFailed();
                t.printStackTrace();
            }
        });
    }

    public void getUserLoginInfo(UserLoginQuery userLoginQuery, final UserListener listener) {
        mUserRestService.getUserLoginInfo(UserLoginQuery.Method, JSON, API_KEY, generateMD5(userLoginQuery.getSignature()), userLoginQuery.mUsername, userLoginQuery.mPassword, new Callback<UserLoginInfo>() {
            @Override
            public void onResponse(Call<UserLoginInfo> call, Response<UserLoginInfo> response) {
                listener.userSuccess();
            }

            @Override
            public void onFailure(Call<UserLoginInfo> call, Throwable t) {
                listener.userInfoFailed();
                t.printStackTrace();
            }
        });
    }

    public void Scrobble(final ScrobbleQuery scrobbleQuery) {
        if (mUserSession.isLogedin())
            new ScrobbleUploader(scrobbleQuery);
    }

    private class ScrobbleUploader {
        boolean cachedirty = false;
        ScrobbleQuery newquery;
        SharedPreferences preferences = context.getSharedPreferences(PREFERENCES_NAME, Context.MODE_PRIVATE);

        ScrobbleUploader(ScrobbleQuery query) {
            if (queries == null) {
                queries = new HashSet<>();
                queries.addAll(preferences.getStringSet(PREFERENCE_CACHE_NAME, new HashSet<String>()));
            }
            if (query != null) {
                synchronized (sLock) {
                    if (isUploading) {
                        cachedirty = true;
                        queries.add(query.toString());
                        save();
                        return;
                    }
                }
                newquery = query;
            }
            upload();
        }

        void upload() {
            synchronized (sLock) {
                isUploading = true;
            }
            int size = queries.size();
            if (size == 0 && newquery == null) return;
            //Max 50 Scrobbles per Request (restriction by LastFM)
            if (size > 50) size = 50;
            if (newquery != null && size > 49) size = 49;
            final String currentqueries[] = new String[size];
            int n = 0;
            for (String t : queries) {
                currentqueries[n++] = t;
                if (n >= size) break;
            }

            TreeMap<String, String> fields = new TreeMap<>();
            fields.put("method", ScrobbleQuery.Method);
            fields.put("api_key", API_KEY);
            fields.put("sk", mUserSession.mToken);

            int i = 0;
            for (String squery : currentqueries) {
                ScrobbleQuery query = new ScrobbleQuery(squery);
                fields.put("artist[" + i + ']', query.mArtist);
                fields.put("track[" + i + ']', query.mTrack);
                fields.put("timestamp[" + i + ']', Long.toString(query.mTimestamp));
                i++;
            }
            if (newquery != null) {
                fields.put("artist[" + i + ']', newquery.mArtist);
                fields.put("track[" + i + ']', newquery.mTrack);
                fields.put("timestamp[" + i + ']', Long.toString(newquery.mTimestamp));
            }
            String sig = "";
            for (Map.Entry<String, String> ent : fields.entrySet()) {
                sig += ent.getKey() + ent.getValue();
            }
            sig += API_SECRET;
            mUserRestService.getScrobbleInfo(generateMD5(sig), JSON, fields, new Callback<ScrobbleInfo>() {
                @Override
                public void onResponse(Call<ScrobbleInfo> call, Response<ScrobbleInfo> response) {

                }

                @Override
                public void onFailure(Call<ScrobbleInfo> call, Throwable t) {

                }
            });


        }

        void save() {
            if (!cachedirty) return;
            SharedPreferences.Editor editor = preferences.edit();
            editor.putStringSet(PREFERENCE_CACHE_NAME, queries);
            editor.apply();
        }

    }

    public void logout() {
        this.mUserSession.mToken = null;
        this.mUserSession.mUsername = null;
        SharedPreferences preferences = context.getSharedPreferences(PREFERENCES_NAME, Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = preferences.edit();
        editor.clear();
        editor.apply();
    }

    public String getUsername() {
        if (mUserSession != null) return mUserSession.mUsername;
        return null;
    }
}
