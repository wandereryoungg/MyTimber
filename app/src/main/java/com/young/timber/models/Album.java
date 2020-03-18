package com.young.timber.models;

public class Album {

    public final long artistId;
    public final String artistName;
    public final long id;
    public final int songCount;
    public final String title;
    public final int year;

    public Album() {
        this.artistId = -1;
        this.artistName = "";
        this.id = -1;
        this.songCount = -1;
        this.title = "";
        this.year = -1;
    }

    public Album(long artistId, String artistName, long id, int songCount, String title, int year) {
        this.artistId = artistId;
        this.artistName = artistName;
        this.id = id;
        this.songCount = songCount;
        this.title = title;
        this.year = year;
    }

}
