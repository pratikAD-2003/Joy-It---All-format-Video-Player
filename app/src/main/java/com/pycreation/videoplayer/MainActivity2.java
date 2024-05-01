package com.pycreation.videoplayer;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;
import androidx.documentfile.provider.DocumentFile;

import com.pycreation.videoplayer.databinding.ActivityMain2Binding;

public class MainActivity2 extends AppCompatActivity {
    ActivityMain2Binding binding;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        binding = ActivityMain2Binding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });

        Uri uri = getIntent().getData();
        DocumentFile file = DocumentFile.fromSingleUri(this,getIntent().getData());
        String name  = file.getName();

        Intent intent = new Intent(this,PlayVideo.class);
        intent.putExtra("uri",String.valueOf(uri));
//        intent.putExtra("uri",String.valueOf(file.getUri()));
        intent.putExtra("name",name);
        startActivity(intent);
        finish();
    }
}