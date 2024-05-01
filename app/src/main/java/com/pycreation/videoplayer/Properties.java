package com.pycreation.videoplayer;

import android.content.Context;
import android.content.SharedPreferences;
import android.os.Bundle;

import androidx.fragment.app.Fragment;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.pycreation.videoplayer.databinding.FragmentPropertiesBinding;
import com.google.android.material.bottomsheet.BottomSheetDialogFragment;

import java.util.Date;
import java.util.concurrent.TimeUnit;

/**
 * A simple {@link Fragment} subclass.
 * Use the {@link Properties#newInstance} factory method to
 * create an instance of this fragment.
 */
public class Properties extends BottomSheetDialogFragment {

    // TODO: Rename parameter arguments, choose names that match
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";

    // TODO: Rename and change types of parameters
    private String mParam1;
    private String mParam2;
    FragmentPropertiesBinding binding;
    Date date;

    public Properties() {
        // Required empty public constructor
    }

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @param param1 Parameter 1.
     * @param param2 Parameter 2.
     * @return A new instance of fragment Properties.
     */
    // TODO: Rename and change types and number of parameters
    public static Properties newInstance(String param1, String param2) {
        Properties fragment = new Properties();
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
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        binding = FragmentPropertiesBinding.inflate(inflater, container, false);
        SharedPreferences sp = getContext().getSharedPreferences("Properties", Context.MODE_PRIVATE);
        date = new Date(TimeUnit.SECONDS.toMillis(Integer.parseInt(sp.getString("date", "xx-xx-xx"))));
        binding.FileName.setText(sp.getString("name", "xxxx.mp4"));
        binding.FileDate.setText(String.valueOf(date));
        binding.FileDuration.setText(durationToString(Long.parseLong(sp.getString("duration", "xx-xx-xx"))));
        binding.FilePath.setText(sp.getString("path", "xxxxx/xxxx/xxx"));
        binding.FileSize.setText(sp.getString("size", "xxxx"));
        return binding.getRoot();
    }

    public static String durationToString(long durationInMillis) {
        // Calculate hours, minutes, seconds, and milliseconds
        long hours = durationInMillis / (1000 * 60 * 60);
        long remainingMillis = durationInMillis % (1000 * 60 * 60);
        long minutes = remainingMillis / (1000 * 60);
        long remainingSeconds = (remainingMillis % (1000 * 60));
        long seconds = remainingSeconds / 1000;
        long milliseconds = remainingSeconds % 1000;

        // Format the result
        String formattedDuration = String.format("%02d:%02d:%02d", hours, minutes, seconds);
        return formattedDuration;
    }
}