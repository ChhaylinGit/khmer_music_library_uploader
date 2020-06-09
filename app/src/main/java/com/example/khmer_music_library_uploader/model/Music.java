package com.example.khmer_music_library_uploader.model;

public class Music {
    public String productionID;
    public String albumID;
    public String singerID;
    public String musicTypeID;
    public String music;
    public String duration;
    public String uri;

    public Music(String productionID, String albumID, String singerID, String musicTypeID, String music, String duration, String uri) {
        this.productionID = productionID;
        this.albumID = albumID;
        this.singerID = singerID;
        this.musicTypeID = musicTypeID;
        this.music = music;
        this.duration = duration;
        this.uri = uri;
    }

    public String getProductionID() {
        return productionID;
    }

    public void setProductionID(String productionID) {
        this.productionID = productionID;
    }

    public String getAlbumID() {
        return albumID;
    }

    public void setAlbumID(String albumID) {
        this.albumID = albumID;
    }

    public String getSingerID() {
        return singerID;
    }

    public void setSingerID(String singerID) {
        this.singerID = singerID;
    }

    public String getMusicTypeID() {
        return musicTypeID;
    }

    public void setMusicTypeID(String musicTypeID) {
        this.musicTypeID = musicTypeID;
    }

    public String getMusic() {
        return music;
    }

    public void setMusic(String music) {
        this.music = music;
    }

    public String getDuration() {
        return duration;
    }

    public void setDuration(String duration) {
        this.duration = duration;
    }

    public String getUri() {
        return uri;
    }

    public void setUri(String uri) {
        this.uri = uri;
    }
}
