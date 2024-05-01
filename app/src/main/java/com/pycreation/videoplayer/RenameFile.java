package com.pycreation.videoplayer;

import android.content.ContentResolver;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.media.MediaScannerConnection;
import android.net.Uri;
import android.os.Bundle;

import androidx.fragment.app.Fragment;

import android.provider.MediaStore;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import com.pycreation.videoplayer.databinding.FragmentRenameFileBinding;
import com.google.android.material.bottomsheet.BottomSheetDialogFragment;

import java.io.File;

/**
 * A simple {@link Fragment} subclass.
 * Use the {@link RenameFile#newInstance} factory method to
 * create an instance of this fragment.
 */
public class RenameFile extends BottomSheetDialogFragment {

    // TODO: Rename parameter arguments, choose names that match
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";

    // TODO: Rename and change types of parameters
    private String mParam1;
    private String mParam2;
    FragmentRenameFileBinding binding;

    public RenameFile() {
        // Required empty public constructor
    }

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @param param1 Parameter 1.
     * @param param2 Parameter 2.
     * @return A new instance of fragment RenameFile.
     */
    // TODO: Rename and change types and number of parameters
    public static RenameFile newInstance(String param1, String param2) {
        RenameFile fragment = new RenameFile();
        Bundle args = new Bundle();
        args.putString(ARG_PARAM1, param1);
        args.putString(ARG_PARAM2, param2);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            mParam1 = getArguments().getString(ARG_PARAM1);
            mParam2 = getArguments().getString(ARG_PARAM2);
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        binding = FragmentRenameFileBinding.inflate(inflater, container, false);
        SharedPreferences sp = getContext().getSharedPreferences("Properties", Context.MODE_PRIVATE);
        String path = sp.getString("path", "not");
        File file = new File(path);
        String videoName = file.getName();
        videoName = videoName.substring(0, videoName.lastIndexOf("."));
        binding.RenameFileEditText.setText(videoName);
        binding.RenameFileEditText.requestFocus();
        binding.RenameFileOk.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String onlyPath = file.getParentFile().getAbsolutePath();
                String ext = file.getAbsolutePath();
                int position = Integer.parseInt(sp.getString("position", "-1"));
                ext = ext.substring(ext.lastIndexOf("."));
                String newPath = onlyPath + "/" + binding.RenameFileEditText.getText().toString() + ext;
                File newFile = new File(newPath);
                boolean rename = file.renameTo(newFile);
                if (rename) {
                    ContentResolver resolver = getContext().getApplicationContext().getContentResolver();
                    resolver.delete(MediaStore.Files.getContentUri("external"), MediaStore.MediaColumns.DATA + "=?", new String[]{
                            file.getAbsolutePath()});
                    Intent intent = new Intent(Intent.ACTION_MEDIA_SCANNER_SCAN_FILE);
                    intent.setData(Uri.fromFile(newFile));
                    getContext().getApplicationContext().sendBroadcast(intent);
                    MediaScannerConnection.scanFile(getContext(), new String[]{file.getPath()}, new String[]{"video/*"}, null);
                    Toast.makeText(getContext(), "Rename process has been done.", Toast.LENGTH_SHORT).show();
                    dismiss();
//                    getFragmentManager()
//                            .beginTransaction()
//                            .detach(FirstFragment..this)
//                            .attach(FirstFragment..this)
//                            .commit();
                } else {
                    Toast.makeText(getContext(), "Rename process getting error.", Toast.LENGTH_SHORT).show();
                }
            }
        });

        binding.RenameFileCancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dismiss();
            }
        });
        return binding.getRoot();
    }
}