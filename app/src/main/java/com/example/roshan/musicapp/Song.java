package com.example.roshan.musicapp;

import java.io.Serializable;

public class Song implements Serializable {
    public final String title;
    public final String album;
    public final String artists;
    public final String path;
    public final String albumArtPath;

    public Song(String title, String album, String artists, String path, String albumArtPath) {
        this.title = title;
        this.album = album;
        this.artists = artists;
        this.path = path;
        this.albumArtPath = albumArtPath;
    }
}
