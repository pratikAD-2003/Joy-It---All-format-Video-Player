package com.pycreation.videoplayer;
import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.fragment.app.FragmentActivity;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.google.android.gms.ads.AdError;
import com.google.android.gms.ads.AdRequest;
import com.google.android.gms.ads.FullScreenContentCallback;
import com.google.android.gms.ads.LoadAdError;
import com.google.android.gms.ads.MobileAds;
import com.google.android.gms.ads.initialization.InitializationStatus;
import com.google.android.gms.ads.initialization.OnInitializationCompleteListener;
import com.google.android.gms.ads.interstitial.InterstitialAd;
import com.google.android.gms.ads.interstitial.InterstitialAdLoadCallback;
import com.pycreation.videoplayer.databinding.MainVLyBinding;
import com.pycreation.videoplayer.databinding.VideoItemsBinding;

import java.util.ArrayList;

public class VideoAdapter extends RecyclerView.Adapter<VideoAdapter.MyHolder> {
    Context context;
    Activity activity;
    ArrayList<videoModel> list;
    InterstitialAd mInterstitialAd;

    public VideoAdapter(Context context, ArrayList<videoModel> list,Activity activity) {
        this.context = context;
        this.list = list;
        this.activity = activity;
    }

    @NonNull
    @Override
    public MyHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
//        VideoItemsBinding binding = VideoItemsBinding.inflate(LayoutInflater.from(context), parent, false);
        MainVLyBinding binding = MainVLyBinding.inflate(LayoutInflater.from(context), parent, false);
        return new MyHolder(binding);
    }

    @Override
    public void onBindViewHolder(@NonNull MyHolder holder, int position) {
        adsInitialization();
        SharedPreferences sp = context.getSharedPreferences("Properties", Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = sp.edit();
        Glide.with(context).load(list.get(position).getVideoUri()).into(holder.binding.videoThumbnail);
        holder.binding.videoDuration.setText(durationToString(Integer.parseInt(list.get(position).getDuration())));

        if (list.get(position).getName().length() > 20) {
            holder.binding.videoTitle.setText(list.get(position).getName().substring(0, 20 - 3) + "...");
        } else {
            holder.binding.videoTitle.setText(list.get(position).getName());
        }
        holder.binding.more.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                editor.putString("path", list.get(position).getPath());
                editor.putString("size", list.get(position).getSize());
                editor.putString("videoUri", list.get(position).getVideoUri());
                editor.putString("duration", list.get(position).getDuration());
                editor.putString("date", list.get(position).getDate());
                editor.putString("name", list.get(position).getName());
                editor.putString("uri", list.get(position).getVideoUri());
                editor.putString("position", String.valueOf(position));
                editor.putString("type", String.valueOf(list.get(position).getType()));
                editor.apply();
                Options options = new Options();
                options.show(((FragmentActivity) context).getSupportFragmentManager(), options.getTag());
            }
        });

        holder.itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (mInterstitialAd != null) {
                    mInterstitialAd.show(activity);
                } else {
//                    Toast.makeText(context, "Ads is not ready yet", Toast.LENGTH_SHORT).show();
                }
                Intent intent = new Intent(context, PlayVideo.class);
                intent.putExtra("name", list.get(position).getName());
                intent.putExtra("uri", list.get(position).getVideoUri());
                intent.putExtra("position", position);
                intent.putExtra("type", list.get(position).getType());
                intent.putExtra("size", list.size());
                context.startActivity(intent);
            }
        });
    }

    @Override
    public int getItemCount() {
        return list.size();
    }

    public class MyHolder extends RecyclerView.ViewHolder {
        //        VideoItemsBinding binding;
        MainVLyBinding binding;

        //        public MyHolder(@NonNull VideoItemsBinding itemView) {
        public MyHolder(@NonNull MainVLyBinding itemView) {
            super(itemView.getRoot());
            binding = itemView;
        }
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

    private void adsInitialization() {
        MobileAds.initialize(context, new OnInitializationCompleteListener() {
            @Override
            public void onInitializationComplete(@NonNull InitializationStatus initializationStatus) {

            }
        });

        // banner ads---------------------------------------------------------
        AdRequest adRequest = new AdRequest.Builder().build();
        // interstitial ads---------------------------------------------------
        InterstitialAd.load(context, context.getResources().getString(R.string.interstitial_id), adRequest, new InterstitialAdLoadCallback() {
            @Override
            public void onAdFailedToLoad(@NonNull LoadAdError loadAdError) {
                super.onAdFailedToLoad(loadAdError);
                Log.d("A6", "FAIL");
            }

            @Override
            public void onAdLoaded(@NonNull InterstitialAd interstitialAd) {
                super.onAdLoaded(interstitialAd);
                mInterstitialAd = interstitialAd;
                Log.d("A1", "Done");
                mInterstitialAd.setFullScreenContentCallback(new FullScreenContentCallback() {
                    @Override
                    public void onAdDismissedFullScreenContent() {
                        super.onAdDismissedFullScreenContent();
                        Log.d("A2", "FSC");
                    }

                    @Override
                    public void onAdFailedToShowFullScreenContent(@NonNull AdError adError) {
                        super.onAdFailedToShowFullScreenContent(adError);
                        Log.d("A3", "FSF");
                    }

                    @Override
                    public void onAdImpression() {
                        super.onAdImpression();
                        Log.d("A4", "OAI");
                    }

                    @Override
                    public void onAdShowedFullScreenContent() {
                        super.onAdShowedFullScreenContent();
                        Log.d("A5", "OAFSC");
                        mInterstitialAd = null;
                    }
                });
            }
        });
    }
}
