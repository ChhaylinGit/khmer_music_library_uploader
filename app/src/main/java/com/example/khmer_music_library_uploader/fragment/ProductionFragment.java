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
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.webkit.MimeTypeMap;
import android.widget.EditText;
import android.widget.Toast;

import com.example.khmer_music_library_uploader.R;
import com.example.khmer_music_library_uploader.model.Constants;
import com.example.khmer_music_library_uploader.model.Production;
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

     CardView cardViewUploadProduction;
     CircularImageView imageProduction;
     FloatingActionButton browseImageProduction;
     Uri filePath;
     static final int RESULT_LOAD_IMAGE = 1;
     StorageReference storageReference;
     DatabaseReference databaseReference;
     EditText edtProductionName;


    public ProductionFragment() {
        // Required empty public constructor
    }

    public void permissionsCheck() {
        if (ContextCompat.checkSelfPermission(getActivity(),
                Manifest.permission.READ_EXTERNAL_STORAGE)
                != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(getActivity(),
                    new String[]{Manifest.permission.READ_EXTERNAL_STORAGE}, RESULT_LOAD_IMAGE);
            return;
        }
    }

    public void browseImage()
    {
        Intent intent = new Intent();
        intent.setType("image/*");
        intent.setAction(Intent.ACTION_PICK);
        startActivityForResult(Intent.createChooser(intent, "Select Image"), RESULT_LOAD_IMAGE);
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == RESULT_LOAD_IMAGE && resultCode == RESULT_OK && null != data) {
            filePath = data.getData();
            Toast.makeText(getActivity(), filePath.toString()+"."+getFileExtension(filePath), Toast.LENGTH_LONG).show();
            imageProduction.setImageURI(filePath);
            imageProduction.invalidate();
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_production, container, false);
        imageProduction= view.findViewById(R.id.imageProduction);
        cardViewUploadProduction = view.findViewById(R.id.cardViewUploadProduction);
        browseImageProduction = view.findViewById(R.id.browseImageProduction);
        edtProductionName = view.findViewById(R.id.edtProductionName);
        browseImageProduction.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                browseImage();
            }
        });
        cardViewUploadProduction.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                uploadProduction();
            }
        });
        return  view;
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        storageReference = FirebaseStorage.getInstance().getReference();
        databaseReference = FirebaseDatabase.getInstance().getReference(Constants.DATABASE_PATH_UPLOADS);
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

    public String getFileExtension(Uri uri)
    {
        ContentResolver contentResolver = getActivity().getContentResolver();
        MimeTypeMap mimeTypeMap = MimeTypeMap.getSingleton();
        return mimeTypeMap.getExtensionFromMimeType(contentResolver.getType(uri));
    }

    public void uploadProduction()
    {
        if(filePath != null)
        {
            final ProgressDialog progressDialog = new ProgressDialog(getActivity());
            progressDialog.setTitle("uploading...");
            progressDialog.show();
            final StorageReference str = storageReference.child(Constants.STORAGE_PATH_UPLOADS+System.currentTimeMillis()+"."+getFileExtension(filePath));
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
                            Toast.makeText(getActivity(), "FileUploaded-"+url, Toast.LENGTH_SHORT).show();
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
                    progressDialog.setMessage("upload"+(int)progress+"%.....");
                }
            });
        }
    }
}
