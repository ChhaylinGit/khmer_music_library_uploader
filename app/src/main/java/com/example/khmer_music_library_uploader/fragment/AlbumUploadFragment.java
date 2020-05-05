package com.example.khmer_music_library_uploader.fragment;

import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Spinner;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import com.example.khmer_music_library_uploader.R;
import com.example.khmer_music_library_uploader.model.Constants;
import com.example.khmer_music_library_uploader.model.Production;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.List;

/**
 * A simple {@link Fragment} subclass.
 */

public class AlbumUploadFragment extends Fragment {

    private Spinner spinnerProduction;
    private DatabaseReference databaseReference;


    public AlbumUploadFragment() {
        // Required empty public constructor
    }

    private void initView(View view)
    {
        spinnerProduction = view.findViewById(R.id.spinnerProduction);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_album_upload, container, false);
        initView(view);
        return view;
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        final List<CharSequence> productionList = new ArrayList<>();
        databaseReference = FirebaseDatabase.getInstance().getReference(Constants.DATABASE_PATH_PRODUCTION);
        databaseReference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                for (DataSnapshot data : dataSnapshot.getChildren())
                {
                    String productionName = data.child("productionName").getValue(String.class);
                    productionList.add(productionName);
                }
                ArrayAdapter<CharSequence> areasAdapter = new ArrayAdapter<CharSequence>(getActivity(), android.R.layout.simple_spinner_item, productionList);
                areasAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
                spinnerProduction.setAdapter(areasAdapter);
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
    }
}
