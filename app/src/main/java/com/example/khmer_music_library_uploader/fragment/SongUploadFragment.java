package com.example.khmer_music_library_uploader.fragment;


import android.os.Bundle;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.fragment.app.Fragment;

import com.example.khmer_music_library_uploader.R;


/**
 * A simple {@link Fragment} subclass.
 */
public class SongUploadFragment extends Fragment {

    public SongUploadFragment() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_song_upload, container, false);
    }
}
