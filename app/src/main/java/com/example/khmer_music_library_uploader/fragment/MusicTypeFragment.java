package com.example.khmer_music_library_uploader.fragment;

import android.app.ProgressDialog;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.cardview.widget.CardView;
import androidx.fragment.app.Fragment;

import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.Toast;

import com.example.khmer_music_library_uploader.R;
import com.example.khmer_music_library_uploader.model.Constants;
import com.example.khmer_music_library_uploader.model.MusicType;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

public class MusicTypeFragment extends Fragment {

    private EditText editMusicTypeName;
    private CardView cardViewMusicType;
    private DatabaseReference databaseReference;

    public MusicTypeFragment() {
        // Required empty public constructor
    }

    private void initView(View view)
    {
        editMusicTypeName = view.findViewById(R.id.edtMusicTypeName);
        cardViewMusicType = view.findViewById(R.id.cardViewMusicType);
    }

    private void  uploadMusicType()
    {
        final ProgressDialog progressDialog = new ProgressDialog(getActivity());
        progressDialog.setTitle("Please wait...");
        progressDialog.show();
        MusicType musicType = new MusicType(editMusicTypeName.getText().toString());
        String musictypeid = databaseReference.push().getKey();
        databaseReference.child(musictypeid).setValue(musicType).addOnSuccessListener(new OnSuccessListener<Void>() {
            @Override
            public void onSuccess(Void aVoid) {
                progressDialog.dismiss();
            }
        }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                progressDialog.dismiss();
                Toast.makeText(getActivity(), e.getMessage(), Toast.LENGTH_SHORT).show();
            }
        });
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_music_type, container, false);
        initView(view);
        cardViewMusicType.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(TextUtils.isEmpty(editMusicTypeName.getText().toString().trim()))
                {
                    editMusicTypeName.setError(getResources().getString(R.string.please_input_musictype));
                }else {
                    uploadMusicType();
                }
            }
        });
        return view;
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        databaseReference = FirebaseDatabase.getInstance().getReference(Constants.DATABASE_PATH_MUSIC_TYPE);
        super.onViewCreated(view, savedInstanceState);
    }
}
