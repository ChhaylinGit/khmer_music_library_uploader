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
import com.example.khmer_music_library_uploader.model.SingerType;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

/**
 * A simple {@link Fragment} subclass.
 */
public class SingerTypeFragment extends Fragment {

    private EditText edtSingerTypeName;
    private CardView cardViewSingertype;
    private DatabaseReference databaseReference;

    public SingerTypeFragment() {
        // Required empty public constructor
    }

    private void initView(View view)
    {
        edtSingerTypeName = view.findViewById(R.id.edtSingerTypeName);
        cardViewSingertype = view.findViewById(R.id.cardViewSingerType);
    }

    private void uploadSingerType()
    {
        final ProgressDialog progressDialog = new ProgressDialog(getActivity());
        progressDialog.setTitle("Please wait...");
        progressDialog.show();
        SingerType singerType = new SingerType(edtSingerTypeName.getText().toString().trim());
        String singerTypeId = databaseReference.push().getKey();
        databaseReference.child(singerTypeId).setValue(singerType).addOnSuccessListener(new OnSuccessListener<Void>() {
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
        View view = inflater.inflate(R.layout.fragment_singer_type, container, false);
        initView(view);
        cardViewSingertype.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(TextUtils.isEmpty(edtSingerTypeName.getText().toString().trim()))
                {
                    edtSingerTypeName.setError(getResources().getString(R.string.please_input_singertype));
                }else{
                    uploadSingerType();
                }
            }
        });
        return view;
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        databaseReference = FirebaseDatabase.getInstance().getReference(Constants.DATABASE_PATH_SINGER_TYPE);
        super.onViewCreated(view, savedInstanceState);
    }
}
