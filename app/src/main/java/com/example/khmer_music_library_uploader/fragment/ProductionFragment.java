package com.example.khmer_music_library_uploader.fragment;

import android.Manifest;
import android.app.ProgressDialog;
import android.content.ContentResolver;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.cardview.widget.CardView;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.Fragment;

import android.provider.OpenableColumns;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.webkit.MimeTypeMap;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.example.khmer_music_library_uploader.R;
import com.example.khmer_music_library_uploader.model.Constants;
import com.example.khmer_music_library_uploader.model.Production;
import com.example.khmer_music_library_uploader.model.Setting;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.OnProgressListener;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;
import com.mikhaellopez.circularimageview.CircularImageView;

import static android.app.Activity.RESULT_OK;

/**
 * A simple {@link Fragment} subclass.
 */
public class ProductionFragment extends Fragment {

    private CardView cardViewUploadProduction;
    private CircularImageView imageProduction;
    private FloatingActionButton browseImageProduction;
    private Uri filePath;
    private StorageReference storageReference;
    private DatabaseReference databaseReference;
    private EditText edtProductionName;
    private boolean isImageSelected = false;
    private TextView textViewShowErrorImage;


    public ProductionFragment() {
        // Required empty public constructor
    }

    public void permissionsCheck() {
        if (ContextCompat.checkSelfPermission(getActivity(),
                Manifest.permission.READ_EXTERNAL_STORAGE)
                != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(getActivity(),
                    new String[]{Manifest.permission.READ_EXTERNAL_STORAGE}, Constants.RESULT_LOAD_IMAGE);
            return;
        }
    }

    public void browseImage()
    {
        Intent intent = new Intent();
        intent.setType("image/*");
        intent.setAction(Intent.ACTION_PICK);
        startActivityForResult(Intent.createChooser(intent, "Select Image"), Constants.RESULT_LOAD_IMAGE);
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        Toast.makeText(getActivity(), Constants.RESULT_LOAD_IMAGE+"", Toast.LENGTH_SHORT).show();
        if (requestCode == Constants.RESULT_LOAD_IMAGE && resultCode == RESULT_OK && null != data) {
            filePath = data.getData();
            imageProduction.setImageURI(filePath);
            imageProduction.invalidate();
            this.isImageSelected = true;
            textViewShowErrorImage.setVisibility(View.GONE);
        }
    }

    private void initView(View view)
    {
        imageProduction= view.findViewById(R.id.imageProduction);
        cardViewUploadProduction = view.findViewById(R.id.cardViewUploadProduction);
        browseImageProduction = view.findViewById(R.id.browseImageProduction);
        edtProductionName = view.findViewById(R.id.edtProductionName);
        textViewShowErrorImage = view.findViewById(R.id.textViewShowErrorImage);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_production, container, false);
        initView(view);
        browseImageProduction.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                browseImage();
            }
        });
        cardViewUploadProduction.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(TextUtils.isEmpty(edtProductionName.getText()))
                {
                    edtProductionName.setError(getResources().getString(R.string.please_input_production_title));
                }else
                    {
                        if(!isImageSelected)
                        {
                            Toast.makeText(getActivity(), "No Image", Toast.LENGTH_SHORT).show();
                            uploadProduction();
                        }else
                            {
                                Toast.makeText(getActivity(), "With Image", Toast.LENGTH_SHORT).show();
                                uploadProductionWithImage();
                            }
                    }
            }
        });
        return  view;
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        storageReference = FirebaseStorage.getInstance().getReference();
        databaseReference = FirebaseDatabase.getInstance().getReference(Constants.DATABASE_PATH_PRODUCTION);
    }

    private String getFileName(Uri uri)
    {
        String result=null;
        if(uri.getScheme().equals("content"))
        {
            Cursor cursor = getActivity().getContentResolver().query(uri,null,null,null,null);
            try {
                if(cursor !=null && cursor.moveToFirst())
                {
                    result = cursor.getString(cursor.getColumnIndex(OpenableColumns.DISPLAY_NAME));
                }
            }finally {
                cursor.close();
            }
        }
        if(result == null)
        {
            result = uri.getPath();
            int out = result.lastIndexOf('/');
            if(out != -1)
            {
                result = result.substring(out+1);
            }
        }
        return result;
    }



    private void reSetToDefault()
    {
        imageProduction.setImageDrawable(getResources().getDrawable(R.drawable.default_image));
        edtProductionName.setText("");
        this.isImageSelected=false;
    }

    public void uploadProductionWithImage()
    {
        if(filePath != null)
        {
            final ProgressDialog progressDialog = new ProgressDialog(getActivity());
            progressDialog.setTitle("Please wait...");
            progressDialog.show();
            final StorageReference str = storageReference.child(Constants.STORAGE_PATH_PRODUCTION+edtProductionName.getText().toString()+"_"+System.currentTimeMillis()+"."+Setting.getFileExtension(getActivity(),filePath));
            str.putFile(filePath).addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
                @Override
                public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                    str.getDownloadUrl().addOnSuccessListener(new OnSuccessListener<Uri>() {
                        @Override
                        public void onSuccess(Uri uri) {
                            String url = uri.toString();
                            Production production = new Production(edtProductionName.getText().toString(),url);
                            String productionID = databaseReference.push().getKey();
                            databaseReference.child(productionID).setValue(production);
                            progressDialog.dismiss();
                            reSetToDefault();
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

    public void uploadProduction()
    {
        final ProgressDialog progressDialog = new ProgressDialog(getActivity());
        progressDialog.setTitle("Please wait...");
        progressDialog.show();
        Production production = new Production(edtProductionName.getText().toString(),"");
        String productionID = databaseReference.push().getKey();
        databaseReference.child(productionID).setValue(production).addOnSuccessListener(new OnSuccessListener<Void>() {
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
}
