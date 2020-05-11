package com.example.khmer_music_library_uploader.fragment;
import android.annotation.SuppressLint;
import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Color;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.PopupMenu;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.cardview.widget.CardView;
import androidx.fragment.app.Fragment;
import com.example.khmer_music_library_uploader.R;
import com.example.khmer_music_library_uploader.model.Album;
import com.example.khmer_music_library_uploader.model.Constants;
import com.example.khmer_music_library_uploader.model.Production;
import com.example.khmer_music_library_uploader.model.Setting;
import com.example.khmer_music_library_uploader.model.StringWithTag;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.OnProgressListener;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;
import com.mikhaellopez.circularimageview.CircularImageView;
import java.util.ArrayList;
import java.util.List;
import static android.app.Activity.RESULT_OK;

/**
 * A simple {@link Fragment} subclass.
 */

public class AlbumUploadFragment extends Fragment {
    private CircularImageView imageViewAlbum;
    private CardView cardViewUploadAlbum;
    private Spinner spinnerProduction;
    private FloatingActionButton browseImageAlbum;
    private DatabaseReference dbSpinner,dbUploadAlbum;
    private StorageReference storUploadAlbum;
    private EditText editTextAlbumName;
    private boolean isImageSelected = false;
    private TextView textViewShowErrorImage;
    private Uri filePath;
    private String productionID;
    private AlertDialog.Builder builder ;

    public AlbumUploadFragment() {
        // Required empty public constructor
    }

    private void initView(View view)
    {
        spinnerProduction = view.findViewById(R.id.spinnerProduction);
        editTextAlbumName = view.findViewById(R.id.edtAlbumName);
        cardViewUploadAlbum = view.findViewById(R.id.cardViewUploadAlbum);
        imageViewAlbum = view.findViewById(R.id.imageAlbum);
        browseImageAlbum = view.findViewById(R.id.browseImageAlbum);
        textViewShowErrorImage = view.findViewById(R.id.textViewShowErrorImage);
    }

    public void browseImage()
    {
        Intent intent = new Intent();
        intent.setType("image/*");
        intent.setAction(Intent.ACTION_PICK);
        startActivityForResult(Intent.createChooser(intent, "Select Image"), Constants.RESULT_LOAD_IMAGE);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        final View view = inflater.inflate(R.layout.fragment_album_upload, container, false);
        initView(view);
        browseImageAlbum.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                browseImage();
            }
        });
        cardViewUploadAlbum.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                uploadAlbum();
            }
        });
        imageViewAlbum.setOnLongClickListener(new View.OnLongClickListener() {
            @Override
            public boolean onLongClick(View v) {
                PopupMenu popupMenu = new PopupMenu(getActivity(),imageViewAlbum);
                popupMenu.getMenuInflater().inflate(R.menu.modify_image_menu,popupMenu.getMenu());
                popupMenu.setOnMenuItemClickListener(new PopupMenu.OnMenuItemClickListener() {
                    @Override
                    public boolean onMenuItemClick(MenuItem item) {
                        switch (item.getItemId())
                        {
                            case R.id.menu_remove_image:
                                removeImage();
                                break;
                        }
                        return true;
                    }
                });
                if(isImageSelected){popupMenu.show();}
                return true;
            }
        });
        return view;
    }


    private void uploadAlbum()
    {
        if(filePath != null)
        {
            final ProgressDialog progressDialog = new ProgressDialog(getActivity());
            progressDialog.setTitle("Please wait...");
            progressDialog.show();
            final StorageReference str = storUploadAlbum.child(Constants.STORAGE_PATH_ALBUM+editTextAlbumName.getText().toString()+"_"+System.currentTimeMillis()+"."+ Setting.getFileExtension(getActivity(),filePath));
            str.putFile(filePath).addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
                @Override
                public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                    str.getDownloadUrl().addOnSuccessListener(new OnSuccessListener<Uri>() {
                        @Override
                        public void onSuccess(Uri uri) {
                            String url = uri.toString();
                            Log.e("xxxxxxxx",productionID+"\n"+url);
                            Album production = new Album(editTextAlbumName.getText().toString(),url);
                            String albumid = dbUploadAlbum.push().getKey();
                            dbUploadAlbum.child(productionID).child(albumid).setValue(production);
                            progressDialog.dismiss();
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

    @SuppressLint("RestrictedApi")
    private void removeImage()
    {
        imageViewAlbum.setImageDrawable(getResources().getDrawable(R.drawable.default_image));
        browseImageAlbum.setVisibility(View.VISIBLE);
        isImageSelected=false;
        filePath = null;
    }


    @SuppressLint("RestrictedApi")
    @Override
    public void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == Constants.RESULT_LOAD_IMAGE && resultCode == RESULT_OK && null != data) {
            filePath = data.getData();
            imageViewAlbum.setImageURI(filePath);
            imageViewAlbum.invalidate();
            this.isImageSelected = true;
            textViewShowErrorImage.setVisibility(View.GONE);
            browseImageAlbum.setVisibility(View.GONE);
        }
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        final List<StringWithTag> productionList=new ArrayList<>();
        builder = new AlertDialog.Builder(getActivity());
        storUploadAlbum = FirebaseStorage.getInstance().getReference();
        dbUploadAlbum = FirebaseDatabase.getInstance().getReference(Constants.DATABASE_PATH_ALBUM);
        dbSpinner = FirebaseDatabase.getInstance().getReference(Constants.DATABASE_PATH_PRODUCTION);
        dbSpinner.addValueEventListener(new ValueEventListener() {
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
}
