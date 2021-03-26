package com.josephmpo.flashgram;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityOptionsCompat;
import androidx.fragment.app.FragmentPagerAdapter;
import androidx.viewpager.widget.ViewPager;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.Toast;

import com.google.android.material.snackbar.Snackbar;
import com.google.android.material.tabs.TabLayout;
import com.josephmpo.flashgram.databinding.ActivityLoginBinding;
import com.parse.LogInCallback;
import com.parse.ParseException;
import com.parse.ParseObject;
import com.parse.ParseUser;
import com.parse.SaveCallback;

import es.dmoral.toasty.Toasty;


public class LoginActivity extends AppCompatActivity implements SignUpFragment.Helpers, LoginFragment.Helpers {

    private ActivityLoginBinding binding;
    private LoginPagerAdapter adapter;
    private Handler handler;
    private Runnable task;
    boolean exitApp;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        binding = ActivityLoginBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());


        handler = new Handler();
        task = new Runnable() {
            @Override
            public void run() {
                exitApp = false;
            }
        };

        exitApp = false;

        adapter = new LoginPagerAdapter(
                getSupportFragmentManager(),
                FragmentPagerAdapter.BEHAVIOR_RESUME_ONLY_CURRENT_FRAGMENT,
                getApplicationContext(),
                this, this
        );

        binding.authViewpager.setAdapter(adapter);

        binding.tabLayout.addOnTabSelectedListener(new TabLayout.ViewPagerOnTabSelectedListener(binding.authViewpager));
        binding.authViewpager.addOnPageChangeListener(new ViewPager.OnPageChangeListener() {
            @Override
            public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {

            }

            @Override
            public void onPageSelected(int position) {
                binding.tabLayout.selectTab(binding.tabLayout.getTabAt(position));
            }

            @Override
            public void onPageScrollStateChanged(int state) {

            }
        });

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
            Toasty.info(getApplicationContext(), "Press Back again to exit", Toasty.LENGTH_SHORT).show();
            handler.postDelayed(task, 3000);
        }
    }

    public void startMainActivity(){
        Intent intent = new Intent(LoginActivity.this, MainActivity.class);
        startActivity(intent);
        finishAfterTransition();
    }

    public void hideKeyboard(){
        InputMethodManager imm = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
        imm.hideSoftInputFromWindow(binding.getRoot().getWindowToken(), 0);
    }
}