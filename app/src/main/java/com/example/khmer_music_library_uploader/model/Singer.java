package com.example.khmer_music_library_uploader.model;

public class Singer {
    String imageUrl;
    String gender;
    String fullname;

    public Singer(String imageUrl, String gender, String fullname) {
        this.imageUrl = imageUrl;
        this.gender = gender;
        this.fullname = fullname;
    }

    public String getImageUrl() {
        return imageUrl;
    }

    public void setImageUrl(String imageUrl) {
        this.imageUrl = imageUrl;
    }

    public String getGender() {
        return gender;
    }

    public void setGender(String gender) {
        this.gender = gender;
    }

    public String getFullname() {
        return fullname;
    }

    public void setFullname(String fullname) {
        this.fullname = fullname;
    }
}
