package com.young.timber.models;

public class Song {
    public final long albumId;
    public final String albumName;
    public final long artistId;
    public final String artistName;
    public final int duration;
    public final long id;
    public final String title;
    public final int trackNumber;

    public Song() {
        this.albumId = -1;
        this.albumName = "";
        this.artistId = -1;
        this.artistName = "";
        this.duration = -1;
        this.id = -1;
        this.title = "";
        this.trackNumber = -1;
    }

    public Song(long albumId, String albumName, long artistId, String artistName, int duration, long id, String title, int trackNumber) {
        this.albumId = albumId;
        this.albumName = albumName;
        this.artistId = artistId;
        this.artistName = artistName;
        this.duration = duration;
        this.id = id;
        this.title = title;
        this.trackNumber = trackNumber;
    }
}
