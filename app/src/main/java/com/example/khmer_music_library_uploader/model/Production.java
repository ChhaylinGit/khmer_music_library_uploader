package com.example.khmer_music_library_uploader.model;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Context;
import android.net.Uri;

import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;

public class Production {
    private String productionName;
    private String imageurl;
    private Context context;
    private DatabaseReference databaseReference;
    private StorageReference storageReference;


    public Production(String productionName, String url) {
        this.productionName = productionName;
        this.imageurl = url;
    }

    public String getProductionName() {
        return productionName;
    }

    public void setProductionName(String productionName) {
        this.productionName = productionName;
    }

    public String getImageUrl() {
        return imageurl;
    }

    public void setUrl(String url) {
        this.imageurl = url;
    }


}
