package com.pycreation.videoplayer;

import static android.view.View.VISIBLE;

import static java.lang.Math.abs;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.constraintlayout.widget.ConstraintLayout;

import android.app.PictureInPictureParams;
import android.content.ContentResolver;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.ActivityInfo;
import android.media.AudioManager;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.provider.Settings;
import android.util.Rational;
import android.view.GestureDetector;
import android.view.MotionEvent;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.SeekBar;
import android.widget.TextView;
import android.widget.Toast;

import com.pycreation.videoplayer.databinding.ActivityPlayVideoBinding;
import com.pycreation.videoplayer.databinding.CustomPlayerBinding;
import com.google.android.exoplayer2.C;
import com.google.android.exoplayer2.ExoPlayer;
import com.google.android.exoplayer2.MediaItem;
import com.google.android.exoplayer2.Player;
import com.google.android.exoplayer2.trackselection.DefaultTrackSelector;
import com.google.android.exoplayer2.ui.AspectRatioFrameLayout;

import java.util.Objects;

public class PlayVideo extends AppCompatActivity {
    ActivityPlayVideoBinding binding;
    String videoTitle = "", uri = "";
    ExoPlayer exoPlayer;
    ImageView playBtn, back, fullScreen, rotate, previous, next, lock, muteVideo, videoSpeed, repeatVideo, selectAudio;
    boolean isPlay = false, isRotate = false, isLock = false, isLocked = false, isMuted = false, isRepeat = false, isSpeedBar = false;
    public static boolean isPIPMode;
    TextView title, vSpeedText, repeatModeStatus;
    String check = "true", speedMode = "true";
    int position, listSize, type;
    LinearLayout chooseSpeedBar;
    TextView set_One, setTwo, setThree, setFour, setFive, setSix, setSeven, setEight;
    AudioManager audioManager;
    SeekBar volumeSeek, brightnessSeek;
    TextView volumeStatus, brightnessStatus;
    Window window;
    int brg;
    long watched;
    DefaultTrackSelector trackSelector;
    ContentResolver contentResolver;
    PictureInPictureParams.Builder pictureInPicture;
    Handler handler = new Handler();


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityPlayVideoBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());
        playBtn = findViewById(R.id.PlayPauseBtn);
        back = findViewById(R.id.backFromPlayer);
        title = findViewById(R.id.videoTitleInPlayer);
        fullScreen = findViewById(R.id.fullScreenVideo);
        rotate = findViewById(R.id.rotateVideo);
        previous = findViewById(R.id.PreviousBtn);
        next = findViewById(R.id.NextBtn);
        lock = findViewById(R.id.LockScreen);
        muteVideo = findViewById(R.id.MuteVideo);
        videoSpeed = findViewById(R.id.VideoSpeed);
        vSpeedText = findViewById(R.id.SelectSpeed);
        repeatVideo = findViewById(R.id.RepeatVideo);
        repeatModeStatus = findViewById(R.id.repeatModeIndicator);
        chooseSpeedBar = findViewById(R.id.chooseSpeed);
        set_One = findViewById(R.id.setSpeed_0_75);
        setTwo = findViewById(R.id.setSpeed_0_5);
        setThree = findViewById(R.id.setSpeed_0_25);
        setFour = findViewById(R.id.setSpeed_1_0);
        setFive = findViewById(R.id.setSpeed_1_5);
        setSix = findViewById(R.id.setSpeed_1_75);
        setSeven = findViewById(R.id.setSpeed_2_0);
        setEight = findViewById(R.id.setSpeedLast);
        volumeSeek = findViewById(R.id.soundSeekbar);
        brightnessSeek = findViewById(R.id.brightnessSeekbar);
        volumeStatus = findViewById(R.id.volumeStatus);
        brightnessStatus = findViewById(R.id.brightnessStatus);
        selectAudio = findViewById(R.id.SelectAudio);

        videoTitle = getIntent().getStringExtra("name");
        uri = getIntent().getStringExtra("uri");
        position = getIntent().getIntExtra("position", 0);
        listSize = getIntent().getIntExtra("size", 0);
        type = getIntent().getIntExtra("type", 0);
        title.setText(videoTitle);

        handler.postDelayed(new Runnable() {
            @Override
            public void run() {
                SharedPreferences sp = getSharedPreferences("Watched", MODE_PRIVATE);
                SharedPreferences.Editor editor = sp.edit();
                editor.putLong(videoTitle, exoPlayer.getCurrentPosition());
                editor.apply();
                handler.postDelayed(this, 1000);
            }
        }, 1000);
        trackSelector = new DefaultTrackSelector(this);

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            pictureInPicture = new PictureInPictureParams.Builder();
        }

        back.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                exoPlayer.setPlayWhenReady(false);
                exoPlayer.release();
                exoPlayer.stop();
                finish();
            }
        });


        exoPlayer = new ExoPlayer.Builder(this).setTrackSelector(trackSelector).build();
        binding.playerView.setPlayer(exoPlayer);
        MediaItem mediaItem = MediaItem.fromUri(uri);
        exoPlayer.setMediaItem(mediaItem);
        exoPlayer.prepare();
        exoPlayer.setPlayWhenReady(true);

        getWindow().getDecorView().setSystemUiVisibility(View.SYSTEM_UI_FLAG_FULLSCREEN | View.SYSTEM_UI_FLAG_IMMERSIVE_STICKY | View.SYSTEM_UI_FLAG_HIDE_NAVIGATION);

        playBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (isPlay) {
                    exoPlayer.setPlayWhenReady(true);
                    playBtn.setImageDrawable(getResources().getDrawable(R.drawable.pause));
                    isPlay = false;
                } else {
                    exoPlayer.setPlayWhenReady(false);
                    playBtn.setImageDrawable(getResources().getDrawable(R.drawable.play_button));
                    isPlay = true;
                }
            }

        });

        contentResolver = getContentResolver();
        window = getWindow();
        brightnessSeek.setMax(100);
        brightnessSeek.setKeyProgressIncrement(1);
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            if (Settings.System.canWrite(this)) {

            } else {
                Intent intent = new Intent(Settings.ACTION_MANAGE_WRITE_SETTINGS);
                intent.setData(Uri.parse("package:" + getApplicationContext().getPackageName()));
                startActivity(intent);
            }
        }

        try {
            brg = Settings.System.getInt(contentResolver, Settings.System.SCREEN_BRIGHTNESS);
            brightnessSeek.setProgress(brg);
        } catch (Settings.SettingNotFoundException e) {
            throw new RuntimeException(e);
        }
        brightnessSeek.setMax(255);
        brightnessSeek.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            @Override
            public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
                brg = progress;
                Settings.System.putInt(contentResolver, Settings.System.SCREEN_BRIGHTNESS, brg);
                WindowManager.LayoutParams layoutParams = window.getAttributes();
                layoutParams.screenBrightness = brg / (float) 300;
                window.setAttributes(layoutParams);
                brightnessStatus.setText(progress + " %");
            }

            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {

            }

            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {

            }
        });

        audioManager = (AudioManager) getApplicationContext().getSystemService(Context.AUDIO_SERVICE);
        volumeSeek.setMax(audioManager.getStreamMaxVolume(AudioManager.STREAM_MUSIC));
        volumeSeek.setProgress(audioManager.getStreamVolume(AudioManager.STREAM_MUSIC));
        volumeSeek.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            @Override
            public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
                audioManager.setStreamVolume(AudioManager.STREAM_MUSIC, progress, 0);
                int mediaVolume = audioManager.getStreamVolume(AudioManager.STREAM_MUSIC);
                int maxVol = audioManager.getStreamMaxVolume(AudioManager.STREAM_MUSIC);
                double volPer = Math.ceil((((double) mediaVolume / (double) maxVol) * (double) 100));
//                Toast.makeText(PlayVideo.this, String.valueOf(volPer), Toast.LENGTH_SHORT).show();
                volumeStatus.setText((volPer) + " %");
            }

            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {

            }

            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {

            }
        });

        binding.Forward.setOnTouchListener(new View.OnTouchListener() {
            GestureDetector gestureDetector = new GestureDetector(new GestureDetector.SimpleOnGestureListener() {
                @Override
                public boolean onDoubleTap(@NonNull MotionEvent e) {
                    exoPlayer.seekTo(exoPlayer.getCurrentPosition() + 10000);
                    binding.ForAnim.setVisibility(VISIBLE);
                    binding.FAnimText.setVisibility(VISIBLE);
                    Handler handler = new Handler();
                    handler.postDelayed(new Runnable() {
                        @Override
                        public void run() {
                            binding.ForAnim.setVisibility(View.INVISIBLE);
                            binding.FAnimText.setVisibility(View.INVISIBLE);
                        }
                    }, 500);
                    return super.onDoubleTap(e);
                }
            });

            @Override
            public boolean onTouch(View v, MotionEvent event) {
                gestureDetector.onTouchEvent(event);
                return true;
            }
        });

        binding.Backward.setOnTouchListener(new View.OnTouchListener() {
            GestureDetector gestureDetector = new GestureDetector(new GestureDetector.SimpleOnGestureListener() {
                @Override
                public boolean onDoubleTap(@NonNull MotionEvent e) {
                    exoPlayer.seekTo(exoPlayer.getCurrentPosition() - 10000);
                    binding.BackAnim.setVisibility(VISIBLE);
                    binding.BAnimText.setVisibility(VISIBLE);
                    Handler handler = new Handler();
                    handler.postDelayed(new Runnable() {
                        @Override
                        public void run() {
                            binding.BackAnim.setVisibility(View.INVISIBLE);
                            binding.BAnimText.setVisibility(View.INVISIBLE);
                        }
                    }, 500);
                    return super.onDoubleTap(e);
                }
            });

            @Override
            public boolean onTouch(View v, MotionEvent event) {
                gestureDetector.onTouchEvent(event);
                return true;
            }
        });

        binding.playerView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (isLocked) {
                    if (!isLock) {
                        binding.UnlockScreen.setVisibility(View.INVISIBLE);
                        isLock = true;
                    } else {
                        binding.UnlockScreen.setVisibility(VISIBLE);
                        isLock = false;
                    }
                } else {

                }
            }
        });

        lock.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                binding.playerView.hideController();
                binding.playerView.setUseController(false);
                binding.UnlockScreen.setVisibility(VISIBLE);
                binding.Forward.setVisibility(View.GONE);
                binding.Backward.setVisibility(View.GONE);
                muteVideo.setVisibility(View.GONE);
                vSpeedText.setVisibility(View.INVISIBLE);
                videoSpeed.setVisibility(View.INVISIBLE);
                repeatModeStatus.setVisibility(View.INVISIBLE);
                repeatVideo.setVisibility(View.INVISIBLE);
                volumeSeek.setVisibility(View.INVISIBLE);
                volumeStatus.setVisibility(View.INVISIBLE);
                brightnessSeek.setVisibility(View.INVISIBLE);
                brightnessStatus.setVisibility(View.INVISIBLE);
                isLocked = true;
            }
        });

        binding.UnlockScreen.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                binding.playerView.showController();
                binding.playerView.setUseController(true);
                binding.UnlockScreen.setVisibility(View.INVISIBLE);
                binding.Forward.setVisibility(VISIBLE);
                binding.Backward.setVisibility(VISIBLE);
                muteVideo.setVisibility(VISIBLE);
                volumeSeek.setVisibility(VISIBLE);
                volumeStatus.setVisibility(VISIBLE);
                brightnessSeek.setVisibility(VISIBLE);
                brightnessStatus.setVisibility(VISIBLE);
                if (!Objects.equals(speedMode, "true")) {
                    vSpeedText.setVisibility(VISIBLE);
                } else {
                    videoSpeed.setVisibility(VISIBLE);
                }
                if (isRepeat) {
                    repeatModeStatus.setVisibility(VISIBLE);
                    repeatVideo.setVisibility(VISIBLE);
                } else {
                    repeatVideo.setVisibility(VISIBLE);
                }
                isLocked = false;
            }
        });

        if (position == 0) {
            previous.setClickable(false);
            previous.setVisibility(View.INVISIBLE);
        } else if (position == listSize - 1) {
            next.setClickable(true);
            next.setVisibility(View.INVISIBLE);
        }
        previous.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (type == 0) {
                    if (position == 0) {
                    } else {
                        Intent intent = new Intent(PlayVideo.this, PlayVideo.class);
                        intent.putExtra("name", FirstFragment.list.get(position - 1).getName());
                        intent.putExtra("uri", FirstFragment.list.get(position - 1).getVideoUri());
                        intent.putExtra("position", position - 1);
                        intent.putExtra("size", FirstFragment.list.size());
                        startActivity(intent);
//                        exoPlayer.stop();
//                        finish();
                        exoPlayer.setPlayWhenReady(false);
                        finish();
                    }
                } else {
                    if (position == 0) {
                    } else {
                        Intent intent = new Intent(PlayVideo.this, PlayVideo.class);
                        intent.putExtra("name", InFolderVideos.list.get(position - 1).getName());
                        intent.putExtra("uri", InFolderVideos.list.get(position - 1).getVideoUri());
                        intent.putExtra("position", position - 1);
                        intent.putExtra("size", InFolderVideos.list.size());
                        startActivity(intent);
//                        exoPlayer.stop();
//                        finish();
                        exoPlayer.setPlayWhenReady(false);
                        finish();
                    }
                }

            }
        });

        next.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (type == 0) {
                    if (position == listSize - 1) {
                    } else {
                        Intent intent = new Intent(PlayVideo.this, PlayVideo.class);
                        intent.putExtra("name", FirstFragment.list.get(position + 1).getName());
                        intent.putExtra("uri", FirstFragment.list.get(position + 1).getVideoUri());
                        intent.putExtra("position", position + 1);
                        intent.putExtra("size", FirstFragment.list.size());
                        startActivity(intent);
//                        exoPlayer.stop();
//                        finish();
                        exoPlayer.setPlayWhenReady(false);
                        finish();
                    }
                } else {
                    if (position == listSize - 1) {
                    } else {
                        Intent intent = new Intent(PlayVideo.this, PlayVideo.class);
                        intent.putExtra("name", InFolderVideos.list.get(position + 1).getName());
                        intent.putExtra("uri", InFolderVideos.list.get(position + 1).getVideoUri());
                        intent.putExtra("position", position + 1);
                        intent.putExtra("size", InFolderVideos.list.size());
                        startActivity(intent);
//                        exoPlayer.stop();
//                        finish();
                        exoPlayer.setPlayWhenReady(false);
                        finish();
                    }
                }

            }
        });

        rotate.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (!isRotate) {
                    rotate.setImageResource(R.drawable.vertical_phone);
                    getWindow().getDecorView().setSystemUiVisibility(View.SYSTEM_UI_FLAG_FULLSCREEN | View.SYSTEM_UI_FLAG_IMMERSIVE_STICKY | View.SYSTEM_UI_FLAG_HIDE_NAVIGATION);
                    setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_LANDSCAPE);
                    ConstraintLayout.LayoutParams params = (ConstraintLayout.LayoutParams) binding.playerView.getLayoutParams();
                    params.width = params.MATCH_PARENT;
                    params.height = params.MATCH_PARENT;
                    binding.playerView.setLayoutParams(params);
                    isRotate = true;
                } else {
                    rotate.setImageResource(R.drawable.horizontal_phone);
                    getWindow().getDecorView().setSystemUiVisibility(View.SYSTEM_UI_FLAG_VISIBLE);
                    setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);
                    ConstraintLayout.LayoutParams params = (ConstraintLayout.LayoutParams) binding.playerView.getLayoutParams();
                    params.width = params.MATCH_PARENT;
                    params.height = (int) (200 * getApplicationContext().getResources().getDisplayMetrics().density);
                    binding.playerView.setLayoutParams(params);
                    isRotate = false;
                }
            }
        });

        fullScreen.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (Objects.equals(check, "true")) {
                    binding.playerView.setResizeMode(AspectRatioFrameLayout.RESIZE_MODE_FIT);
                    exoPlayer.setVideoScalingMode(C.VIDEO_SCALING_MODE_SCALE_TO_FIT_WITH_CROPPING);
                    fullScreen.setImageResource(R.drawable.full_creen);
                    check = "true1";
                } else if (Objects.equals(check, "true1")) {
                    binding.playerView.setResizeMode(AspectRatioFrameLayout.RESIZE_MODE_FILL);
                    exoPlayer.setVideoScalingMode(C.VIDEO_SCALING_MODE_SCALE_TO_FIT);
                    fullScreen.setImageResource(R.drawable.minimize);
                    check = "true2";
                } else if (Objects.equals(check, "true2")) {
                    binding.playerView.setResizeMode(AspectRatioFrameLayout.RESIZE_MODE_ZOOM);
                    exoPlayer.setVideoScalingMode(C.VIDEO_SCALING_MODE_DEFAULT);
                    fullScreen.setImageResource(R.drawable.mini_screen);
                    check = "true";
                }
            }
        });

        muteVideo.setOnClickListener(new View.OnClickListener() {
            float getCurrentVol;
            float muteVol = 0;

            @Override
            public void onClick(View v) {
                if (!isMuted) {
                    getCurrentVol = exoPlayer.getVolume();
                    exoPlayer.setVolume(muteVol);
                    muteVideo.setImageResource(R.drawable.unmute);
//                    muteVideo.setColorFilter(getResources().getColor(R.color.white));
                    isMuted = true;
                } else {
                    muteVideo.setImageResource(R.drawable.mute);
//                    muteVideo.setColorFilter(getResources().getColor(R.color.white));
                    exoPlayer.setVolume(getCurrentVol);
                    isMuted = false;
                }
            }
        });

        videoSpeed.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (!isSpeedBar) {
                    chooseSpeedBar.setVisibility(VISIBLE);
                    isSpeedBar = true;
                } else {
                    chooseSpeedBar.setVisibility(View.INVISIBLE);
                    isSpeedBar = false;
                }
            }
        });

        vSpeedText.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (!isSpeedBar) {
                    chooseSpeedBar.setVisibility(VISIBLE);
                    isSpeedBar = true;
                } else {
                    chooseSpeedBar.setVisibility(View.INVISIBLE);
                    isSpeedBar = false;
                }
            }
        });

        set_One.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                exoPlayer.setPlaybackSpeed(0.75f);
                videoSpeed.setVisibility(View.INVISIBLE);
                vSpeedText.setVisibility(VISIBLE);
                vSpeedText.setText("0.75x");
                chooseSpeedBar.setVisibility(View.INVISIBLE);
            }
        });

        setTwo.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                videoSpeed.setVisibility(View.INVISIBLE);
                vSpeedText.setVisibility(VISIBLE);
                exoPlayer.setPlaybackSpeed(0.5f);
                vSpeedText.setText("0.5x");
                chooseSpeedBar.setVisibility(View.INVISIBLE);
            }
        });

        setThree.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                videoSpeed.setVisibility(View.INVISIBLE);
                vSpeedText.setVisibility(VISIBLE);
                exoPlayer.setPlaybackSpeed(0.25f);
                vSpeedText.setText("0.25x");
                chooseSpeedBar.setVisibility(View.INVISIBLE);
            }
        });

        setFour.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                videoSpeed.setVisibility(VISIBLE);
                vSpeedText.setVisibility(View.INVISIBLE);
                exoPlayer.setPlaybackSpeed(1f);
//                vSpeedText.setText("1x");
                chooseSpeedBar.setVisibility(View.INVISIBLE);
            }
        });

        setFive.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                videoSpeed.setVisibility(View.INVISIBLE);
                vSpeedText.setVisibility(VISIBLE);
                exoPlayer.setPlaybackSpeed(1.25f);
                vSpeedText.setText("1.25x");
                chooseSpeedBar.setVisibility(View.INVISIBLE);
            }
        });

        setSix.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                videoSpeed.setVisibility(View.INVISIBLE);
                vSpeedText.setVisibility(VISIBLE);
                exoPlayer.setPlaybackSpeed(1.5f);
                vSpeedText.setText("1.5x");
                chooseSpeedBar.setVisibility(View.INVISIBLE);
            }
        });

        setSeven.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                videoSpeed.setVisibility(View.INVISIBLE);
                vSpeedText.setVisibility(VISIBLE);
                exoPlayer.setPlaybackSpeed(1.75f);
                vSpeedText.setText("1.75x");
                chooseSpeedBar.setVisibility(View.INVISIBLE);
            }
        });

        setEight.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                videoSpeed.setVisibility(View.INVISIBLE);
                vSpeedText.setVisibility(VISIBLE);
                exoPlayer.setPlaybackSpeed(2f);
                vSpeedText.setText("2x");
                chooseSpeedBar.setVisibility(View.INVISIBLE);
            }
        });
        repeatVideo.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (!isRepeat) {
                    isRepeat = true;
                    repeatModeStatus.setVisibility(VISIBLE);
//                    Toast.makeText(PlayVideo.this, "Repeat on", Toast.LENGTH_SHORT).show();
                } else {
                    isRepeat = false;
                    repeatModeStatus.setVisibility(View.INVISIBLE);
//                    Toast.makeText(PlayVideo.this, "Repeat off", Toast.LENGTH_SHORT).show();
                }
            }
        });

        exoPlayer.addListener(new Player.Listener() {
            @Override
            public void onPlayerStateChanged(boolean playWhenReady, int playbackState) {
                if (playbackState == ExoPlayer.STATE_ENDED) {
                    if (isRepeat) {
                        exoPlayer.seekTo(0);
                    }
                }
            }
        });

        binding.playerView.setOnTouchListener(new View.OnTouchListener() {
            int threshold = 100;
            int velocity_threshold = 100;
            GestureDetector gestureDetector = new GestureDetector(new GestureDetector.SimpleOnGestureListener() {
                @Override
                public boolean onFling(@Nullable MotionEvent e1, @NonNull MotionEvent e2, float velocityX, float velocityY) {
                    // get X and Y differences
                    float xDiff = e2.getX() - e1.getX();
                    float yDiff = e2.getY() - e1.getY();
                    try {
                        // check condition
                        if (abs(xDiff) > abs(yDiff)) {
                            // when x is greater than y
                            if (abs(xDiff) > threshold && abs(velocityX) > velocity_threshold) {
                                if (xDiff > 0) {
                                    // when swipe left
//                                    Toast.makeText(PlayVideo.this, "Left", Toast.LENGTH_SHORT).show();
                                    exoPlayer.seekTo(exoPlayer.getCurrentPosition() + 10000);
                                    binding.ForAnim.setVisibility(VISIBLE);
                                    binding.FAnimText.setVisibility(VISIBLE);
                                    Handler handler = new Handler();
                                    handler.postDelayed(new Runnable() {
                                        @Override
                                        public void run() {
                                            binding.ForAnim.setVisibility(View.INVISIBLE);
                                            binding.FAnimText.setVisibility(View.INVISIBLE);
                                        }
                                    }, 500);
                                } else {
                                    // when swipe right
//                                    Toast.makeText(PlayVideo.this, "Right", Toast.LENGTH_SHORT).show();
                                    exoPlayer.seekTo(exoPlayer.getCurrentPosition() - 10000);
                                    binding.BackAnim.setVisibility(VISIBLE);
                                    binding.BAnimText.setVisibility(VISIBLE);
                                    Handler handler = new Handler();
                                    handler.postDelayed(new Runnable() {
                                        @Override
                                        public void run() {
                                            binding.BackAnim.setVisibility(View.INVISIBLE);
                                            binding.BAnimText.setVisibility(View.INVISIBLE);
                                        }
                                    }, 500);
                                }
                                return true;
                            }
                        }
                    } catch (Exception e) {

                    }
                    return false;
                }
            });

            @Override
            public boolean onTouch(View v, MotionEvent event) {
                gestureDetector.onTouchEvent(event);
                return false;
            }
        });

        selectAudio.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                    Rational aspectRatio = new Rational(16, 9);
                    pictureInPicture.setAspectRatio(aspectRatio);
                    enterPictureInPictureMode(pictureInPicture.build());
                } else {
                    Toast.makeText(PlayVideo.this, "Please allow Picture in Picture Settings.", Toast.LENGTH_SHORT).show();
                }
            }
        });
    }

    @Override
    public void onPictureInPictureModeChanged(boolean isInPictureInPictureMode) {
        super.onPictureInPictureModeChanged(isInPictureInPictureMode);
        isPIPMode = isInPictureInPictureMode;
        if (isInPictureInPictureMode) {
            binding.playerView.hideController();
        } else {
            binding.playerView.showController();
        }
    }

    @Override
    protected void onStart() {
        super.onStart();
        SharedPreferences sp = getSharedPreferences("Watched", MODE_PRIVATE);
        watched =  sp.getLong(videoTitle, 0);
        exoPlayer.seekTo(watched);
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        exoPlayer.setPlayWhenReady(false);
        exoPlayer.release();
        exoPlayer.stop();
        finish();
    }

    @Override
    protected void onStop() {
        super.onStop();
        if (isPIPMode) {
            exoPlayer.setPlayWhenReady(false);
            exoPlayer.stop();
            exoPlayer.release();
        }
    }

}