package com.example.khmer_music_library_uploader.fragment;


import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.media.MediaMetadata;
import android.media.MediaMetadataRetriever;
import android.net.Uri;
import android.os.Bundle;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.ProgressBar;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.cardview.widget.CardView;
import androidx.fragment.app.Fragment;

import com.example.khmer_music_library_uploader.R;
import com.example.khmer_music_library_uploader.adapter.MusicInforAdapter;
import com.example.khmer_music_library_uploader.model.MusicInfor;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.StorageTask;

import java.net.URL;
import java.util.ArrayList;
import java.util.List;

import kotlin.jvm.internal.PropertyReference0Impl;

import static android.app.Activity.RESULT_OK;


/**
 * A simple {@link Fragment} subclass.
 */
public class SongUploadFragment extends Fragment {


    private CardView btnBrowse;
    private ListView listView;
    private Spinner spinner;
    private ImageView albumPicture;
    private ProgressBar progressBar;
    private StorageTask mUploadTask;
    private StorageReference storageReference;
    private DatabaseReference databaseReference;
    private MediaMetadataRetriever mediaMetadataRetriever;
    private String songCategory;
    private Uri audioUri;
    private byte[] art;
    private String title,singer,album,dreation;

    public SongUploadFragment() {
        mediaMetadataRetriever = new MediaMetadataRetriever();
        databaseReference = FirebaseDatabase.getInstance().getReference().child("song");
        storageReference = FirebaseStorage.getInstance().getReference().child("song");

    }

    private void  loadMusicInfor(String title,String album,String singer,String duration)
    {
        List<MusicInfor> musicInfor = new ArrayList<>();
        musicInfor.add(new MusicInfor(getResources().getString(R.string.music_title).toString(),title));
        musicInfor.add(new MusicInfor(getResources().getString(R.string.album).toString(),album));
        musicInfor.add(new MusicInfor(getResources().getString(R.string.singer).toString(),singer));
        musicInfor.add(new MusicInfor(getResources().getString(R.string.duration).toString(),duration));
        MusicInforAdapter adapter = new MusicInforAdapter(musicInfor);
        listView.setAdapter(adapter);
    }

    private void addItemToSpinner()
    {
        List<CharSequence> albumList = new ArrayList<>();
        albumList.add("1");
        albumList.add("2");
        albumList.add("3");
        albumList.add("4");
        ArrayAdapter<CharSequence> arrayAdapter = new ArrayAdapter<CharSequence>(getActivity(),android.R.layout.simple_spinner_item,albumList);
        arrayAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinner.setAdapter(arrayAdapter);
    }

    private void openAudioFiles()
    {
        Intent intent = new Intent(Intent.ACTION_GET_CONTENT);
        intent.setType("audio/*");
        startActivityForResult(intent,101);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view =  inflater.inflate(R.layout.fragment_song_upload, container, false);
        btnBrowse = view.findViewById(R.id.btnBrowse);
        listView = view.findViewById(R.id.lstView);
        spinner = view.findViewById(R.id.spinnerAlbum);
        progressBar = view.findViewById(R.id.progressBar);
        albumPicture = view.findViewById(R.id.albumPicture);
        spinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> adapterView, View view, int i, long l) {
                songCategory = adapterView.getItemAtPosition(i).toString();
            }

            @Override
            public void onNothingSelected(AdapterView<?> adapterView) {

            }
        });
        
        btnBrowse.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                openAudioFiles();
            }
        });
        
        return  view;
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if(requestCode == 101 && resultCode == RESULT_OK && data.getData() != null)
        {
            audioUri = data.getData();
            mediaMetadataRetriever.setDataSource(getActivity(),audioUri);
//            art = mediaMetadataRetriever.getEmbeddedPicture();
//            Bitmap bitmap = BitmapFactory.decodeByteArray(art,0,art.length);
//            albumPicture.setImageBitmap(bitmap);
            loadMusicInfor(
                    mediaMetadataRetriever.extractMetadata(MediaMetadataRetriever.METADATA_KEY_ALBUM),
                    mediaMetadataRetriever.extractMetadata(MediaMetadataRetriever.METADATA_KEY_ARTIST),
                    mediaMetadataRetriever.extractMetadata(MediaMetadataRetriever.METADATA_KEY_GENRE),
                    mediaMetadataRetriever.extractMetadata(MediaMetadataRetriever.METADATA_KEY_DURATION));
        }
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        loadMusicInfor("N/A","N/A","N/A","N/A");
        addItemToSpinner();
    }
}
