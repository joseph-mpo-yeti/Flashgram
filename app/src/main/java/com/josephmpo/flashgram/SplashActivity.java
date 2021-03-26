package com.josephmpo.flashgram;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityOptionsCompat;

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
                if(nextActivity == LoginActivity.class){
                    ActivityOptionsCompat options = ActivityOptionsCompat.
                            makeSceneTransitionAnimation(SplashActivity.this, findViewById(R.id.logo), "logo");
                    startActivity(mainIntent, options.toBundle());

                } else {
                    startActivity(mainIntent);
                }
                finishAfterTransition();
            }
        }, 1000);
    }
}