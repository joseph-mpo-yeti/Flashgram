package com.josephmpo.flashgram;

import com.parse.ParseClassName;
import com.parse.ParseObject;
import com.parse.ParseUser;

@ParseClassName("Post")
public class Post extends ParseObject {
    public static final String KEY_CAPTION = "caption";
    public static final String KEY_USER = "user";

    public void setCaption(String caption){
        put(KEY_CAPTION, caption);
    }

    public void setUser(ParseUser user){
        put(KEY_USER, user);
    }

    public String getCaption(){
        return getString(KEY_CAPTION).toString();
    }

    public ParseUser getUser(){
        return getParseUser(KEY_USER);
    }
}
