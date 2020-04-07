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

import com.young.timber.lastfmapi.models.AlbumInfo;
import com.young.timber.lastfmapi.models.ArtistInfo;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.http.GET;
import retrofit2.http.Header;
import retrofit2.http.Headers;
import retrofit2.http.Query;

public interface LastFmRestService {

    String BASE_PARAMETERS_ALBUM = "?method=album.getinfo&api_key=ec3813b8d1a1f9f87175dfdd3f7d5118&format=json";
    String BASE_PARAMETERS_ARTIST = "?method=artist.getinfo&api_key=fdb3a51437d4281d4d64964d333531d4&format=json";

    @Headers("Cache-Control: public")
    @GET(BASE_PARAMETERS_ALBUM)
    Call<AlbumInfo> getAlbumInfo(@Query("artist") String artist, @Query("album") String album);

    @Headers("Cache-Control: public")
    @GET(BASE_PARAMETERS_ARTIST)
    void getArtistInfo(@Query("artist") String artist, Callback<ArtistInfo> callback);

}
