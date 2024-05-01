package com.pycreation.videoplayer;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.media.MediaScannerConnection;
import android.os.Build;
import android.os.Bundle;

import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentActivity;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.pycreation.videoplayer.databinding.FragmentOptionsBinding;
import com.google.android.material.bottomsheet.BottomSheetDialogFragment;

import java.io.File;

/**
 * A simple {@link Fragment} subclass.
 * Use the {@link Options#newInstance} factory method to
 * create an instance of this fragment.
 */
public class Options extends BottomSheetDialogFragment {

    // TODO: Rename parameter arguments, choose names that match
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";

    // TODO: Rename and change types of parameters
    private String mParam1;
    private String mParam2;

    FragmentOptionsBinding binding;

    public Options() {
        // Required empty public constructor
    }

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @param param1 Parameter 1.
     * @param param2 Parameter 2.
     * @return A new instance of fragment Options.
     */
    // TODO: Rename and change types and number of parameters
    public static Options newInstance(String param1, String param2) {
        Options fragment = new Options();
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
        binding = FragmentOptionsBinding.inflate(inflater, container, false);
        SharedPreferences sp = getContext().getSharedPreferences("Properties", Context.MODE_PRIVATE);

        binding.videoProperties.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Properties p = new Properties();
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.R) {
                    p.show(((FragmentActivity) getContext()).getSupportFragmentManager(), p.getTag());
                    dismiss();
                }
            }
        });

        binding.deleteVideo.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                AlertDialog.Builder builder = new AlertDialog.Builder(getContext());
                builder.setTitle("Delete Video ?");
                builder.setIcon(R.drawable.trash);
                builder.setMessage(sp.getString("name", "error"));
                builder.setPositiveButton("Delete", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        String path = sp.getString("path", "dafdfa");
                        int position = Integer.parseInt(sp.getString("position", "-1"));
                        File file = new File(path);
                        if (file.exists() && file.delete()) {
                            MediaScannerConnection.scanFile(getContext(), new String[]{file.getPath()}, new String[]{"video/*"}, null);
                            Intent intent = new Intent(getContext(),FirstFragment.class);
                            startActivity(intent);
                            getActivity().finish();
                        }
                        dismiss();
                    }
                }).setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        dismiss();
                    }
                }).show();
            }
        });

        binding.playVideo.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(getContext(), PlayVideo.class);
                intent.putExtra("name", sp.getString("name", "xxxx.mp4"));
                intent.putExtra("uri", sp.getString("uri", "xxxx.mp4"));
                intent.putExtra("position", sp.getString("position", "xxxx.mp4"));
                intent.putExtra("type", sp.getString("type", "xxxx.mp4"));
                intent.putExtra("size", sp.getString("size", "xxxx.mp4"));
                startActivity(intent);
                dismiss();
            }
        });

        binding.renameVideo.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                RenameFile renameFile = new RenameFile();
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.R) {
                    renameFile.show(((FragmentActivity) getContext()).getSupportFragmentManager(), renameFile.getTag());
                    dismiss();
                }
            }
        });
        return binding.getRoot();
    }
}