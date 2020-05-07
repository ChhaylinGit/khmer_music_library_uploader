package com.example.khmer_music_library_uploader.model;

public class Album {
    private String albumName;
    private String url;

    public Album(String albumName, String url) {
        this.albumName = albumName;
        this.url = url;
    }

    public String getAlbumName() {
        return albumName;
    }

    public void setAlbumName(String albumName) {
        this.albumName = albumName;
    }

    public String getUrl() {
        return url;
    }

    public void setUrl(String url) {
        this.url = url;
    }
}
