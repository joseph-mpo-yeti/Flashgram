package com.josephmpo.flashgram;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.FragmentPagerAdapter;
import androidx.viewpager.widget.ViewPager;

import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.view.Menu;
import android.view.MenuItem;
import android.view.inputmethod.InputMethodManager;

import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.android.material.dialog.MaterialAlertDialogBuilder;
import com.josephmpo.flashgram.databinding.ActivityMainBinding;
import com.parse.LogOutCallback;
import com.parse.ParseException;
import com.parse.ParseUser;

import java.util.TimerTask;

import es.dmoral.toasty.Toasty;

public class MainActivity extends AppCompatActivity implements ProfileFragment.SignOutInterface,
        NewPostFragment.OpenHome {
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

        try {
            getSupportActionBar().setHomeAsUpIndicator(R.drawable.ic_baseline_photo_camera_24);
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        } catch (NullPointerException e){
            e.printStackTrace();
        }

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
                FragmentPagerAdapter.BEHAVIOR_RESUME_ONLY_CURRENT_FRAGMENT, this, this);
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
            finishAfterTransition();
        } else {
            exitApp = true;
            Toasty.info(getApplicationContext(), "Press Back again to exit", Toasty.LENGTH_SHORT).show();
            handler.postDelayed(task, 3000);
        }
    }

    @Override
    public void signOut() {
        MaterialAlertDialogBuilder builder = new MaterialAlertDialogBuilder(this);
        builder.setTitle("Sign Out");
        builder.setMessage("Are you sure?");
        builder.setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                dialog.dismiss();
            }
        });
        builder.setPositiveButton("Sign Out", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                dialog.dismiss();
                ParseUser.logOutInBackground(new LogOutCallback() {
                    @Override
                    public void done(ParseException e) {
                        if(e == null){
                            startActivity(new Intent(MainActivity.this, LoginActivity.class));
                            finishAfterTransition();
                        } else {
                            Toasty.error(getApplicationContext(), e.getMessage(), Toasty.LENGTH_LONG).show();
                        }
                    }
                });
            }
        });
        builder.show();
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        if (item.getItemId() == android.R.id.home){
            binding.mainViewpager.setCurrentItem(1, true);
        }
        return true;
    }

    public void hideKeyboard(){
        InputMethodManager imm = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
        imm.hideSoftInputFromWindow(binding.getRoot().getWindowToken(), 0);
    }

    @Override
    public void goHome() {
        binding.mainViewpager.setCurrentItem(0, true);
    }

}