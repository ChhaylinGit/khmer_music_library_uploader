package com.example.khmer_music_library_uploader.model;

public class MusicInfor {
    private String key;
    private String value;

    public MusicInfor(String key, String value) {
        this.key = key;
        this.value = value;
    }

    public String getKey() {
        return key;
    }

    public void setKey(String key) {
        this.key = key;
    }

    public String getValue() {
        return value;
    }

    public void setValue(String value) {
        this.value = value;
    }
}
