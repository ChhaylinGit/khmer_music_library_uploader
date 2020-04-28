package com.example.khmer_music_library_uploader.adapter;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import com.example.khmer_music_library_uploader.R;
import com.example.khmer_music_library_uploader.model.MusicInfor;

import java.util.List;

public class MusicInforAdapter extends BaseAdapter {

    private List<MusicInfor> musicInforList;

    public MusicInforAdapter(List<MusicInfor> musicInforList){
        this.musicInforList = musicInforList;
    }

    @Override
    public int getCount() {
        return musicInforList.size();
    }

    @Override
    public Object getItem(int position) {
        return musicInforList.get(position);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public View getView(int position, View view, ViewGroup viewGroup) {
        View inflater = LayoutInflater.from(viewGroup.getContext()).inflate(R.layout.custom_list_view_music,viewGroup,false);
        TextView textViewKey = inflater.findViewById(R.id.textViewKey);
        TextView textViewValue = inflater.findViewById(R.id.textViewValue);
        MusicInfor musicInfor = musicInforList.get(position);
        textViewKey.setText(musicInfor.getKey());
        textViewValue.setText(musicInfor.getValue());
        return inflater;
    }
}
