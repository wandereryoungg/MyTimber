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

package com.young.timber.lastfmapi.models;

import com.google.gson.annotations.SerializedName;

import java.util.List;

public class AlbumInfo {

    private static final String ALBUM = "album";
    private static final String NAME = "name";
    private static final String ARTIST = "artist";
    private static final String MBID = "mbid";
    private static final String URL = "url";
    private static final String LISTENERS = "listeners";
    private static final String PLAYCOUNT = "playcount";


    @SerializedName(ALBUM)
    public LastfmAlbum mAlbum;
    @SerializedName(NAME)
    public String mName;
    @SerializedName(ARTIST)
    public String mArtist;
    @SerializedName(MBID)
    public String mMbid;
    @SerializedName(URL)
    public String MUrl;
    @SerializedName(LISTENERS)
    public String mListeners;
    @SerializedName(PLAYCOUNT)
    public String mPlaycount;
    @SerializedName("tracks")
    public List<Track> mTracks;
    @SerializedName("tags")
    public Tag mTag;
    @SerializedName("wiki")
    public Wiki mWiki;

    public static class Wiki{
        @SerializedName("published")
        public String mPublished;
        @SerializedName("summary")
        public String mSummary;
        @SerializedName("content")
        public String mContent;
    }

    public static class Tag{
        @SerializedName("tag")
        public List<TT> mTT;

        public static class TT{
            @SerializedName("name")
            public String mName;
            @SerializedName("url")
            public String mUrl;
        }
    }

    public static class Track{
        @SerializedName("name")
        public String mName;
        @SerializedName("url")
        public String mUrl;
        @SerializedName("duration")
        public String mDuration;
        @SerializedName("@attr")
        public Attr mAttr;
        @SerializedName("streamable")
        public Streamable mStreamable;
        @SerializedName("artist")
        public Artist mArtist;

        public static class Attr{
            @SerializedName("rank")
            public String mRank;
        }

        public static class Streamable{
            @SerializedName("#text")
            public String mText;
            @SerializedName("fulltrack")
            public String mFulltrack;
        }

        public static class Artist{
            @SerializedName("name")
            public String mName;
            @SerializedName("mbid")
            public String mBid;
            @SerializedName("url")
            public String mUrl;
        }

    }

}
