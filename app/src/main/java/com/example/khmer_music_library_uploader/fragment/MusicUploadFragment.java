package com.example.khmer_music_library_uploader.fragment;

import android.app.ProgressDialog;
import android.content.Intent;
import android.graphics.Color;
import android.media.MediaMetadataRetriever;
import android.net.Uri;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.cardview.widget.CardView;
import androidx.fragment.app.Fragment;

import android.text.TextUtils;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.example.khmer_music_library_uploader.R;
import com.example.khmer_music_library_uploader.model.Constants;
import com.example.khmer_music_library_uploader.model.Music;
import com.example.khmer_music_library_uploader.model.Setting;
import com.example.khmer_music_library_uploader.model.StringWithTag;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.OnProgressListener;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.StorageTask;
import com.google.firebase.storage.UploadTask;

import java.util.ArrayList;
import java.util.List;

import static android.app.Activity.RESULT_OK;
import static androidx.constraintlayout.widget.Constraints.TAG;

/**
 * A simple {@link Fragment} subclass.
 */
public class MusicUploadFragment extends Fragment {

    private TextView textViewShowErrorMusic;
    private CardView cardViewBrowseMusic,cardViewSaveMusic;
    private Spinner spinnerProduction,spinnerAlbum,spinnerSinger,spinnerMusicType;
    private EditText edtMusicTitle,edtMusicDuration;
    private DatabaseReference dbProduction,dbAlbum,dbSinger,dbMusicType,dbMusic;
    private StorageReference storageReference;
    private String productionID,albumID,singerID,musicTypeID,musicDuration;
    private Uri audioUri;
    private StorageTask mUploadTask;
    private MediaMetadataRetriever mediaMetadataRetriever;
    private FirebaseAuth mAuth;

    public MusicUploadFragment() {
        mAuth = FirebaseAuth.getInstance();
    }

    private void initView(View view)
    {
        textViewShowErrorMusic = view.findViewById(R.id.textViewShowErrorMusic);
        cardViewBrowseMusic = view.findViewById(R.id.cardViewBrowseMusic);
        cardViewSaveMusic = view.findViewById(R.id.cardViewSaveMusic);
        spinnerProduction = view.findViewById(R.id.spinnerProduction);
        spinnerAlbum = view.findViewById(R.id.spinnerAlbum);
        spinnerSinger = view.findViewById(R.id.spinnerSinger);
        spinnerMusicType = view.findViewById(R.id.spinnerMusicType);
        edtMusicDuration = view.findViewById(R.id.edtMusicDuration);
        edtMusicTitle = view.findViewById(R.id.edtMusicTitle);
        mediaMetadataRetriever = new MediaMetadataRetriever();
    }

    @Override
    public void onStart() {
        super.onStart();
        FirebaseUser user = mAuth.getCurrentUser();
        if (user != null) {
            // do your stuff
        } else {
            signInAnonymously();
        }
    }

    private void signInAnonymously() {
        mAuth.signInAnonymously().addOnSuccessListener(getActivity(), new  OnSuccessListener<AuthResult>() {
            @Override
            public void onSuccess(AuthResult authResult) {
                // do your stuff
            }
        })
                .addOnFailureListener(getActivity(), new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception exception) {
                        Log.e(TAG, "signInAnonymously:FAILURE", exception);
                    }
                });
    }

    private void loadSpinnerProduction()
    {
        final List<StringWithTag> productionList=new ArrayList<>();
        dbProduction.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                productionList.clear();
                productionList.add(new StringWithTag(getResources().getString(R.string.please_select_production),"0"));
                for (DataSnapshot data : dataSnapshot.getChildren())
                {
                    String productionId = data.getKey();
                    String productionName = data.child("productionName").getValue(String.class);
                    productionList.add(new StringWithTag(productionName,productionId));
                }
                ArrayAdapter<StringWithTag> areasAdapter = new ArrayAdapter<StringWithTag>(getActivity(), android.R.layout.simple_spinner_item, productionList){
                    @Override
                    public boolean isEnabled(int position) {
                        if(position == 0) { return false; }
                        else { return true; }
                    }
                    @Override
                    public View getDropDownView(int position, @Nullable View convertView, @NonNull ViewGroup parent) {
                        View view = super.getDropDownView(position, convertView, parent);
                        TextView tv = (TextView) view;
                        if(position == 0){
                            // Set the hint text color gray
                            tv.setTextColor(Color.GRAY);
                        }
                        else {
                            tv.setTextColor(Color.BLACK);
                        }
                        return view;
                    }

                };
                areasAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
                spinnerProduction.setAdapter(areasAdapter);
                spinnerProduction.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
                    @Override
                    public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                        StringWithTag production =(StringWithTag) parent.getItemAtPosition(position);
                        productionID = production.key.toString();
                        loadSpinnerAlbum(productionID);
//                      Toast.makeText(getActivity(), production.key.toString(), Toast.LENGTH_SHORT).show();
                    }

                    @Override
                    public void onNothingSelected(AdapterView<?> parent) {

                    }
                });
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
    }

    private void loadSpinnerAlbum(String productionID)
    {
        final List<StringWithTag> albumList=new ArrayList<>();
        dbAlbum.child(productionID).addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                albumList.clear();
                albumList.add(new StringWithTag(getResources().getString(R.string.please_select_album),"0"));
                for (DataSnapshot data : dataSnapshot.getChildren())
                {
                    String productionId = data.getKey();
                    String productionName = data.child("albumName").getValue(String.class);
                    albumList.add(new StringWithTag(productionName,productionId));
                }
                ArrayAdapter<StringWithTag> areasAdapter = new ArrayAdapter<StringWithTag>(getActivity(), android.R.layout.simple_spinner_item, albumList){
                    @Override
                    public boolean isEnabled(int position) {
                        if(position == 0) { return false; }
                        else { return true; }
                    }
                    @Override
                    public View getDropDownView(int position, @Nullable View convertView, @NonNull ViewGroup parent) {
                        View view = super.getDropDownView(position, convertView, parent);
                        TextView tv = (TextView) view;
                        if(position == 0){
                            // Set the hint text color gray
                            tv.setTextColor(Color.GRAY);
                        }
                        else {
                            tv.setTextColor(Color.BLACK);
                        }
                        return view;
                    }

                };
                areasAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
                spinnerAlbum.setAdapter(areasAdapter);
                spinnerAlbum.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
                    @Override
                    public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                        StringWithTag album =(StringWithTag) parent.getItemAtPosition(position);
                        albumID = album.key.toString();
//                      Toast.makeText(getActivity(), production.key.toString(), Toast.LENGTH_SHORT).show();
                    }

                    @Override
                    public void onNothingSelected(AdapterView<?> parent) {

                    }
                });
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
    }

    private void loadSpinnerSinger()
    {
        final List<StringWithTag> singerList=new ArrayList<>();
        dbSinger.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                singerList.clear();
                singerList.add(new StringWithTag(getResources().getString(R.string.please_select_singer),"0"));
                for (DataSnapshot data : dataSnapshot.getChildren())
                {
                    String productionId = data.getKey();
                    String productionName = data.child("fullname").getValue(String.class);
                    singerList.add(new StringWithTag(productionName,productionId));
                }
                ArrayAdapter<StringWithTag> areasAdapter = new ArrayAdapter<StringWithTag>(getActivity(), android.R.layout.simple_spinner_item, singerList){
                    @Override
                    public boolean isEnabled(int position) {
                        if(position == 0) { return false; }
                        else { return true; }
                    }
                    @Override
                    public View getDropDownView(int position, @Nullable View convertView, @NonNull ViewGroup parent) {
                        View view = super.getDropDownView(position, convertView, parent);
                        TextView tv = (TextView) view;
                        if(position == 0){
                            // Set the hint text color gray
                            tv.setTextColor(Color.GRAY);
                        }
                        else {
                            tv.setTextColor(Color.BLACK);
                        }
                        return view;
                    }

                };
                areasAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
                spinnerSinger.setAdapter(areasAdapter);
                spinnerSinger.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
                    @Override
                    public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                        StringWithTag singer =(StringWithTag) parent.getItemAtPosition(position);
                        singerID = singer.key.toString();
//                      Toast.makeText(getActivity(), production.key.toString(), Toast.LENGTH_SHORT).show();
                    }

                    @Override
                    public void onNothingSelected(AdapterView<?> parent) {

                    }
                });
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
    }

    private void loadSpinnerMusicType()
    {
        final List<StringWithTag> musicTypeList=new ArrayList<>();
        dbMusicType.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                musicTypeList.clear();
                musicTypeList.add(new StringWithTag(getResources().getString(R.string.please_select_music_type),"0"));
                for (DataSnapshot data : dataSnapshot.getChildren())
                {
                    String productionId = data.getKey();
                    String productionName = data.child("musicTypeName").getValue(String.class);
                    musicTypeList.add(new StringWithTag(productionName,productionId));
                }
                ArrayAdapter<StringWithTag> areasAdapter = new ArrayAdapter<StringWithTag>(getActivity(), android.R.layout.simple_spinner_item, musicTypeList){
                    @Override
                    public boolean isEnabled(int position) {
                        if(position == 0) { return false; }
                        else { return true; }
                    }
                    @Override
                    public View getDropDownView(int position, @Nullable View convertView, @NonNull ViewGroup parent) {
                        View view = super.getDropDownView(position, convertView, parent);
                        TextView tv = (TextView) view;
                        if(position == 0){
                            // Set the hint text color gray
                            tv.setTextColor(Color.GRAY);
                        }
                        else {
                            tv.setTextColor(Color.BLACK);
                        }
                        return view;
                    }

                };
                areasAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
                spinnerMusicType.setAdapter(areasAdapter);
                spinnerMusicType.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
                    @Override
                    public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                        StringWithTag musicType =(StringWithTag) parent.getItemAtPosition(position);
                        musicTypeID = musicType.key.toString();
//                      Toast.makeText(getActivity(), production.key.toString(), Toast.LENGTH_SHORT).show();
                    }

                    @Override
                    public void onNothingSelected(AdapterView<?> parent) {

                    }
                });
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
    }

    private void browseMusic()
    {
        cardViewBrowseMusic.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(Intent.ACTION_GET_CONTENT);
                intent.setType("audio/*");
                startActivityForResult(intent,Constants.RESULT_LOAD_MUSIC);
            }
        });
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == Constants.RESULT_LOAD_MUSIC && resultCode == RESULT_OK && data.getData() != null)
        {
            audioUri = data.getData();
            mediaMetadataRetriever.setDataSource(getActivity(),audioUri);
            musicDuration = mediaMetadataRetriever.extractMetadata(MediaMetadataRetriever.METADATA_KEY_DURATION);
            edtMusicDuration.setText(Setting.formateMilliSeccond(Long.parseLong(mediaMetadataRetriever.extractMetadata(MediaMetadataRetriever.METADATA_KEY_DURATION))));
            edtMusicTitle.setText(mediaMetadataRetriever.extractMetadata(MediaMetadataRetriever.METADATA_KEY_TITLE));
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view =  inflater.inflate(R.layout.fragment_music_upload, container, false);
        initView(view);
        return  view;
    }

    private boolean empty()
    {
        boolean result=false;
        if(audioUri == null)
        {
            textViewShowErrorMusic.setVisibility(View.VISIBLE);
            result = true;
        }
        if(productionID.equals("0"))
        {
            TextView errorText = (TextView)spinnerProduction.getSelectedView();
            errorText.setError("");
            errorText.setTextColor(Color.RED);//just to highlight that this is an error
            errorText.setText(getResources().getString(R.string.select_production));//changes the selected item text to this
            result = true;
        }
        if(albumID.equals("0"))
        {
            TextView errorText = (TextView)spinnerAlbum.getSelectedView();
            errorText.setError("");
            errorText.setTextColor(Color.RED);//just to highlight that this is an error
            errorText.setText(getResources().getString(R.string.select_album));//changes the selected item text to this
            result = true;
        }
        if(singerID.equals("0"))
        {
            TextView errorText = (TextView)spinnerSinger.getSelectedView();
            errorText.setError("");
            errorText.setTextColor(Color.RED);//just to highlight that this is an error
            errorText.setText(getResources().getString(R.string.select_singer));//changes the selected item text to this
            result = true;
        }
        if(musicTypeID.equals("0"))
        {
            TextView errorText = (TextView)spinnerMusicType.getSelectedView();
            errorText.setError("");
            errorText.setTextColor(Color.RED);//just to highlight that this is an error
            errorText.setText(getResources().getString(R.string.select_music_type));//changes the selected item text to this
            result = true;
        }
        if(TextUtils.isEmpty(edtMusicTitle.getText().toString()))
        {
            edtMusicTitle.setError(getResources().getString(R.string.please_input_music_title));
            result = true;
        }
        if(TextUtils.isEmpty(edtMusicDuration.getText().toString()))
        {
            edtMusicDuration.setError(getResources().getString(R.string.please_input_music_duration));
            result = true;
        }
        return result;
    }

    private void saveMusic()
    {
        cardViewSaveMusic.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(!empty())
                {
                   uploadMusic();
                }
            }
        });
    }

    private void  uploadMusic()
    {
        if(audioUri != null)
        {
            final ProgressDialog progressDialog = new ProgressDialog(getActivity());
            progressDialog.setTitle("Please wait...");
            progressDialog.show();
            final StorageReference str = storageReference.child(Constants.STORAGE_PATH_MUSIC+edtMusicTitle.getText().toString()+"_"+System.currentTimeMillis()+"."+ Setting.getFileExtension(getActivity(),audioUri));
            mUploadTask = str.putFile(audioUri).addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
                @Override
                public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                    str.getDownloadUrl().addOnSuccessListener(new OnSuccessListener<Uri>() {
                        @Override
                        public void onSuccess(Uri uri) {
                            Music music = new Music(productionID,albumID,singerID,musicTypeID,edtMusicTitle.getText().toString(),musicDuration,uri.toString());
                            String uploadID = dbMusic.push().getKey();
                            dbMusic.child(productionID).child(albumID).child(singerID).child(musicTypeID).child(uploadID).setValue(music);
                            progressDialog.dismiss();
                        }
                    }).addOnFailureListener(new OnFailureListener() {
                        @Override
                        public void onFailure(@NonNull Exception e) {
                            progressDialog.dismiss();
                            Toast.makeText(getActivity(), e.getMessage(), Toast.LENGTH_LONG).show();
                        }
                    });
                }
            }).addOnFailureListener(new OnFailureListener() {
                @Override
                public void onFailure(@NonNull Exception e) {
                    progressDialog.dismiss();
                    Toast.makeText(getActivity(), e.getMessage(), Toast.LENGTH_SHORT).show();
                }
            }).addOnProgressListener(new OnProgressListener<UploadTask.TaskSnapshot>() {
                @Override
                public void onProgress(UploadTask.TaskSnapshot taskSnapshot) {
                    double progress = (100.0 * taskSnapshot.getBytesTransferred())/taskSnapshot.getTotalByteCount();
                    progressDialog.setMessage("Uploading.... "+(int)progress+"/100%"); //Uploading....50/100%
                }
            });
        }
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        dbProduction = FirebaseDatabase.getInstance().getReference(Constants.DATABASE_PATH_PRODUCTION);
        dbAlbum = FirebaseDatabase.getInstance().getReference(Constants.DATABASE_PATH_ALBUM);
        dbSinger = FirebaseDatabase.getInstance().getReference(Constants.DATABASE_PATH_SINGER);
        dbMusicType = FirebaseDatabase.getInstance().getReference(Constants.DATABASE_PATH_MUSIC_TYPE);
        dbMusic = FirebaseDatabase.getInstance().getReference(Constants.DATABASE_PATH_MUSIC);
        storageReference = FirebaseStorage.getInstance().getReference();
        loadSpinnerProduction();
        loadSpinnerSinger();
        loadSpinnerMusicType();
        browseMusic();
        saveMusic();
    }
}
