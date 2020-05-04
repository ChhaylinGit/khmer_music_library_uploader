package com.example.khmer_music_library_uploader.model;

public class StringWithTag {
    public String value;
    public Object key;

    public StringWithTag(String value, Object key) {
        this.value = value;
        this.key = key;
    }

    @Override
    public String toString() {
        return value;
    }
}
