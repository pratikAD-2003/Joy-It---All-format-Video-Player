package com.pycreation.videoplayer;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;
import androidx.viewpager2.widget.ViewPager2;

import android.os.Bundle;
import android.view.MenuItem;

import com.pycreation.videoplayer.databinding.ActivityMainBinding;
import com.google.android.material.navigation.NavigationBarView;

import java.util.ArrayList;

public class MainActivity extends AppCompatActivity {
    ActivityMainBinding myBinding;
    ArrayList<Fragment> list = new ArrayList<>();
    PageAdapter adapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        myBinding = ActivityMainBinding.inflate(getLayoutInflater());
        setContentView(myBinding.getRoot());

        list.add(new FirstFragment());
        list.add(new SecondFragment());

        adapter = new PageAdapter(this, list);
        myBinding.allVideosViewPager.setAdapter(adapter);

        myBinding.allVideosViewPager.registerOnPageChangeCallback(new ViewPager2.OnPageChangeCallback() {
            @Override
            public void onPageSelected(int position) {
                switch (position) {
                    case 0:
                        myBinding.bottomNavigationView.setSelectedItemId(R.id.all_videos);
                        break;
                    case 1:
                        myBinding.bottomNavigationView.setSelectedItemId(R.id.other_videos);
                        break;
                }
                super.onPageSelected(position);
            }
        });

        myBinding.bottomNavigationView.setOnItemSelectedListener(new NavigationBarView.OnItemSelectedListener() {
            @Override
            public boolean onNavigationItemSelected(@NonNull MenuItem item) {
                int ID = item.getItemId();
                if (ID == R.id.all_videos) {
                    myBinding.allVideosViewPager.setCurrentItem(0);
                } else if (ID == R.id.other_videos) {
                    myBinding.allVideosViewPager.setCurrentItem(1);
                }
                return true;
            }
        });
    }
}