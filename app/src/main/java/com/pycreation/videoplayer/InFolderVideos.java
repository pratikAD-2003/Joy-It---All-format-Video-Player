package com.pycreation.videoplayer;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.DividerItemDecoration;
import androidx.recyclerview.widget.LinearLayoutManager;

import android.os.Bundle;

import com.pycreation.videoplayer.databinding.ActivityInFolderVideosBinding;

import java.util.ArrayList;
import java.util.Objects;

public class InFolderVideos extends AppCompatActivity {
    ActivityInFolderVideosBinding binding;
    String folderName = "";
    public static ArrayList<videoModel> list = new ArrayList<>();
    LinearLayoutManager layoutManager;
    VideoAdapter2 adapter;

    int i = 0;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityInFolderVideosBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());
        folderName = getIntent().getStringExtra("folderName");

        list.clear();
        for (videoModel a : FirstFragment.list) {
            if (Objects.equals(a.getFolderName(), folderName)) {
                videoModel model = new videoModel(a.getSize(), a.getPath(), a.getDuration(), a.getDate(), a.getVideoUri(), a.getFolderId(), a.getFolderName(), a.getName(),1);
                list.add(model);
            }
        }


        binding.listSizeInFolderVideos.setText("Videos : " + list.size());
        layoutManager = new LinearLayoutManager(this);
        DividerItemDecoration dividerItemDecoration = new DividerItemDecoration(binding.InFolderVideoRV.getContext(), layoutManager.getOrientation());
        dividerItemDecoration.setDrawable(this.getResources().getDrawable(R.drawable.custom_divider));
        binding.InFolderVideoRV.addItemDecoration(dividerItemDecoration);

        binding.InFolderVideoRV.setLayoutManager(layoutManager);
        binding.InFolderVideoRV.setHasFixedSize(true);
        adapter = new VideoAdapter2(this, list);
        binding.InFolderVideoRV.setAdapter(adapter);
    }
}