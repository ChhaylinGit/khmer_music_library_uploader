package com.example.khmer_music_library_uploader.fragment;

import android.os.Bundle;

import androidx.fragment.app.Fragment;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.example.khmer_music_library_uploader.R;

/**
 * A simple {@link Fragment} subclass.
 */
public class SingerFragment extends Fragment {

    public SingerFragment() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_singer, container, false);
    }
}
