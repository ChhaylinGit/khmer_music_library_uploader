package com.example.khmer_music_library_uploader.fragment;

import android.app.ProgressDialog;
import android.content.Intent;
import android.graphics.Color;
import android.net.Uri;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.cardview.widget.CardView;
import androidx.fragment.app.Fragment;

import android.text.TextUtils;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.PopupMenu;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.example.khmer_music_library_uploader.R;
import com.example.khmer_music_library_uploader.model.Album;
import com.example.khmer_music_library_uploader.model.Constants;
import com.example.khmer_music_library_uploader.model.Setting;
import com.example.khmer_music_library_uploader.model.Singer;
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

import java.util.ArrayList;
import java.util.List;

import static android.app.Activity.RESULT_OK;

/**
 * A simple {@link Fragment} subclass.
 */
public class SingerFragment extends Fragment {

    private DatabaseReference dbSingerType,dbSinger;
    private StorageReference storageReference;
    private Spinner spinnerSingerType,spinnerGender;
    private String singerTypeId,gender;
    private ImageView imageSinger;
    private Uri filePath;
    private boolean isImageSelected = false;
    private TextView textViewShowErrorImage;
    private EditText editSingerName;
    private CardView cardviewUploadSinger;
    private FloatingActionButton browseImageSinger;

    public SingerFragment() {
        // Required empty public constructor
    }

    private void  initView(View view)
    {
        spinnerSingerType = view.findViewById(R.id.spinnerSingerType);
        spinnerGender = view.findViewById(R.id.spinnerSingerGender);
        imageSinger = view.findViewById(R.id.imageSinger);
        browseImageSinger = view.findViewById(R.id.browseImageSinger);
        textViewShowErrorImage = view.findViewById(R.id.textViewShowErrorImage);
        editSingerName = view.findViewById(R.id.edtSingerName);
        cardviewUploadSinger = view.findViewById(R.id.cardViewUploadSinger);
    }

    private void uploadSinger()
    {
        final ProgressDialog progressDialog = new ProgressDialog(getActivity());
        progressDialog.setTitle("Please wait...");
        progressDialog.show();
        Singer singer = new Singer("",gender,editSingerName.getText().toString());
        String singerid = dbSinger.push().getKey();
        dbSinger.child(singerid).setValue(singer).addOnSuccessListener(new OnSuccessListener<Void>() {
            @Override
            public void onSuccess(Void aVoid) {
                progressDialog.dismiss();
            }
        }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                Toast.makeText(getActivity(), e.getMessage(), Toast.LENGTH_SHORT).show();
                progressDialog.dismiss();
            }
        });
    }

    private void uploadSingerWithImage()
    {
        if(filePath != null)
        {
            final ProgressDialog progressDialog = new ProgressDialog(getActivity());
            progressDialog.setTitle("Please wait...");
            progressDialog.show();
            final StorageReference str = storageReference.child(Constants.STORAGE_PATH_ALBUM+editSingerName.getText().toString()+"_"+System.currentTimeMillis()+"."+ Setting.getFileExtension(getActivity(),filePath));
            str.putFile(filePath).addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
                @Override
                public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                    str.getDownloadUrl().addOnSuccessListener(new OnSuccessListener<Uri>() {
                        @Override
                        public void onSuccess(Uri uri) {
                            String url = uri.toString();
                            Singer production = new Singer(url,gender,editSingerName.getText().toString());
                            String singerid = dbSinger.push().getKey();
                            dbSinger.child(singerid).setValue(production);
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

    private void browseImage()
    {
        browseImageSinger.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent();
                intent.setType("image/*");
                intent.setAction(Intent.ACTION_PICK);
                startActivityForResult(Intent.createChooser(intent, "Select Image"), Constants.RESULT_LOAD_IMAGE);
            }
        });
    }

    private void  showRemoveImageMenu()
    {
        imageSinger.setOnLongClickListener(new View.OnLongClickListener() {
            @Override
            public boolean onLongClick(View v) {
                PopupMenu popupMenu = new PopupMenu(getActivity(),imageSinger);
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
    }

    private void removeImage()
    {
        imageSinger.setImageDrawable(getResources().getDrawable(R.drawable.default_image));
        browseImageSinger.show();
        isImageSelected=false;
        filePath = null;
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == Constants.RESULT_LOAD_IMAGE && resultCode == RESULT_OK && null != data) {
            filePath = data.getData();
            imageSinger.setImageURI(filePath);
            imageSinger.invalidate();
            this.isImageSelected = true;
            textViewShowErrorImage.setVisibility(View.GONE);
            browseImageSinger.hide();
        }
    }

    private void  loadGenderSpinner()
    {
        ArrayList genderList = new ArrayList();
        genderList.add(getResources().getString(R.string.please_select_gender));
        genderList.add(getResources().getString(R.string.male));
        genderList.add(getResources().getString(R.string.female));
        ArrayAdapter<StringWithTag> genderAdapter = new ArrayAdapter<StringWithTag>(getActivity(),android.R.layout.simple_spinner_item, genderList){
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
        genderAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinnerGender.setAdapter(genderAdapter);
        spinnerGender.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                gender = parent.getItemAtPosition(position).toString();
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });
    }

    private void loadSingerTypeSpinner()
    {
        final List<StringWithTag> singerTypeList=new ArrayList<>();
        dbSingerType.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                singerTypeList.clear();
                singerTypeList.add(new StringWithTag(getResources().getString(R.string.please_select_singer_type),"0"));
                for (DataSnapshot data : dataSnapshot.getChildren())
                {
                    String productionId = data.getKey();
                    String productionName = data.child("singerType").getValue(String.class);
                    singerTypeList.add(new StringWithTag(productionName,productionId));
                }
                ArrayAdapter<StringWithTag> singerTypeAdapter = new ArrayAdapter<StringWithTag>(getActivity(),android.R.layout.simple_spinner_item, singerTypeList){
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
                singerTypeAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
                spinnerSingerType.setAdapter(singerTypeAdapter);
                spinnerSingerType.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
                    @Override
                    public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                        StringWithTag production =(StringWithTag) parent.getItemAtPosition(position);
                        singerTypeId = production.key.toString();
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

    private boolean empty()
    {
        boolean result = false;
        if(singerTypeId.equals("0"))
        {
            TextView errorText = (TextView)spinnerSingerType.getSelectedView();
            errorText.setError("");
            errorText.setTextColor(Color.RED);//just to highlight that this is an error
            errorText.setText(getResources().getString(R.string.please_input_production_title));//changes the selected item text to this
            result = true;
        }
        if(gender.equals(getResources().getString(R.string.please_select_gender)))
        {
            TextView errorText = (TextView)spinnerGender.getSelectedView();
            errorText.setError("");
            errorText.setTextColor(Color.RED);//just to highlight that this is an error
            errorText.setText(getResources().getString(R.string.please_input_production_title));//changes the selected item text to this
            result = true;
        }
        if(TextUtils.isEmpty(editSingerName.getText().toString()))
        {
            editSingerName.setError(getResources().getString(R.string.please_input_production_title));
        }
        return result;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_singer, container, false);
        initView(view);
        cardviewUploadSinger.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
               if(!empty())
               {
                   if(!isImageSelected)
                   {
                       uploadSinger();
                   }else{
                       uploadSingerWithImage();
                   }
               }
            }
        });
        return view;
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        dbSingerType = FirebaseDatabase.getInstance().getReference(Constants.DATABASE_PATH_SINGER_TYPE);
        dbSinger = FirebaseDatabase.getInstance().getReference(Constants.DATABASE_PATH_SINGER);
        storageReference = FirebaseStorage.getInstance().getReference(Constants.STORAGE_PATH_SINGER);
        loadSingerTypeSpinner();
        loadGenderSpinner();
        browseImage();
        showRemoveImageMenu();
        super.onViewCreated(view, savedInstanceState);
    }
}
