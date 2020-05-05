package com.example.khmer_music_library_uploader.model;

import android.app.Activity;
import android.content.ContentResolver;
import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.webkit.MimeTypeMap;

import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentPagerAdapter;

public class Setting {
    public static String getFileExtension(Context context,Uri uri)
    {
        ContentResolver contentResolver = context.getContentResolver();
        MimeTypeMap mimeTypeMap = MimeTypeMap.getSingleton();
        return mimeTypeMap.getExtensionFromMimeType(contentResolver.getType(uri));
    }
}
