package com.josephmpo.flashgram;

import androidx.appcompat.app.AppCompatActivity;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;

import com.parse.ParseUser;

public class SplashActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_splash);

        ParseUser user = ParseUser.getCurrentUser();

        new Handler().postDelayed(new Runnable(){
            @Override
            public void run() {
                Class nextActivity;
                if(user != null && user.isAuthenticated()){
                    nextActivity = MainActivity.class;
                } else {
                    nextActivity = LoginActivity.class;
                }
                Intent mainIntent = new Intent(SplashActivity.this, nextActivity);
                startActivity(mainIntent);
                finish();
            }
        }, 1000);
    }
}