package com.josephmpo.flashgram;

import android.app.Application;

import com.parse.Parse;
import com.parse.ParseClassName;
import com.parse.ParseObject;
import com.parse.ParseUser;

public class ParseApplication  extends Application {

    @Override
    public void onCreate() {
        super.onCreate();

        ParseObject.registerSubclass(Post.class);

        Parse.initialize(new Parse.Configuration.Builder(this)
                .applicationId("DeN03a1abQswWiWfmSFWoTUqheZ8CFpKMRIWnqUy")
                .clientKey("R9mytVADt5mn41F0GQ3Yyuj6XPEhVGWiCpBR1DeW")
                .server("https://parseapi.back4app.com")
                .build()
        );

    }


}
