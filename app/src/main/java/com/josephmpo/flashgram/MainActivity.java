package com.josephmpo.flashgram;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.FragmentPagerAdapter;
import androidx.viewpager.widget.ViewPager;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.Toast;

import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.josephmpo.flashgram.databinding.ActivityMainBinding;
import com.parse.LogOutCallback;
import com.parse.ParseException;
import com.parse.ParseUser;

import java.util.TimerTask;

public class MainActivity extends AppCompatActivity implements ProfileFragment.SignOutInterface {
    ActivityMainBinding binding;
    private Handler handler;
    private Runnable task;
    boolean exitApp;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        binding = ActivityMainBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        handler = new Handler();
        task = new Runnable() {
            @Override
            public void run() {
                exitApp = false;
            }
        };

        exitApp = false;

        setSupportActionBar(binding.toolbar);

        binding.toolbar.setLogo(R.drawable.ic_baseline_photo_camera_24);

        binding.bottomNav.getMenu().getItem(0).setIcon(R.drawable.instagram_home_filled_24);

        binding.bottomNav.setOnNavigationItemSelectedListener(new BottomNavigationView.OnNavigationItemSelectedListener() {
            @Override
            public boolean onNavigationItemSelected(@NonNull MenuItem item) {
                setActiveIcon(item);
                switch (item.getItemId()) {
                    case R.id.homeMenuItem:
                        binding.mainViewpager.setCurrentItem(0, true);
                        break;
                    case R.id.addMenuItem:
                        binding.mainViewpager.setCurrentItem(1, true);
                        break;
                    default:
                        binding.mainViewpager.setCurrentItem(2, true);
                }
                return true;
            }
        });

        MainViewPagerAdapter adapter = new MainViewPagerAdapter(getSupportFragmentManager(),
                FragmentPagerAdapter.BEHAVIOR_RESUME_ONLY_CURRENT_FRAGMENT, this);
        binding.mainViewpager.setAdapter(adapter);

        binding.mainViewpager.addOnPageChangeListener(new ViewPager.OnPageChangeListener() {
            @Override
            public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {

            }

            @Override
            public void onPageSelected(int position) {
                MenuItem item = binding.bottomNav.getMenu().getItem(position);
                setActiveIcon(item);
            }

            @Override
            public void onPageScrollStateChanged(int state) {

            }
        });
    }

    private void resetIcons() {
        binding.bottomNav.getMenu().getItem(0).setIcon(R.drawable.instagram_home_outline_24);
        binding.bottomNav.getMenu().getItem(1).setIcon(R.drawable.instagram_new_post_outline_24);
        binding.bottomNav.getMenu().getItem(2).setIcon(R.drawable.instagram_user_outline_24);
    }

    private void setActiveIcon(MenuItem item){
        resetIcons();
        item.setChecked(true);
        switch (item.getItemId()) {
            case R.id.homeMenuItem:
                item.setIcon(R.drawable.instagram_home_filled_24);
                break;
            case R.id.addMenuItem:
                item.setIcon(R.drawable.instagram_new_post_filled_24);
                break;
            default:
                item.setIcon(R.drawable.instagram_user_filled_24);
        }
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
         switch (item.getItemId()){
             case R.id.addNew:
                 startActivity(new Intent(MainActivity.this, CreatePostActivity.class));
                 break;
             default:

        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu, menu);
        return super.onCreateOptionsMenu(menu);
    }

    @Override
    protected void onDestroy() {
        handler.removeCallbacks(task);
        super.onDestroy();
    }

    @Override
    protected void onPause() {
        handler.removeCallbacks(task);
        super.onPause();
    }

    @Override
    public void onBackPressed() {
        if(exitApp){
            finish();
        } else {
            exitApp = true;
            Toast.makeText(getApplicationContext(), "Pressed back button again to exit", Toast.LENGTH_SHORT).show();
            handler.postDelayed(task, 3000);
        }
    }

    @Override
    public void signOut() {
        ParseUser.logOutInBackground(new LogOutCallback() {
            @Override
            public void done(ParseException e) {
                if(e == null){
                    startActivity(new Intent(MainActivity.this, LoginActivity.class));
                    finish();
                } else {
                    Toast.makeText(getApplicationContext(), e.getMessage(), Toast.LENGTH_LONG).show();
                }
            }
        });
    }
}