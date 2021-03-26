package com.josephmpo.flashgram;

import android.util.Log;

import androidx.annotation.NonNull;

import com.parse.ParseClassName;
import com.parse.ParseException;
import com.parse.ParseFile;
import com.parse.ParseObject;
import com.parse.ParseUser;

import org.json.JSONArray;
import org.json.JSONException;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;

@ParseClassName("Post")
public class Post extends ParseObject {
    public static final String KEY_CAPTION = "caption";
    public static final String KEY_USER = "user";
    public static final String KEY_LIKES = "likes";
    public static final String KEY_LIKERS = "likers";
    public static final String KEY_POST_IMG = "image";
    public static final String KEY_UPDATED_AT= "updated_at";
    public static final String KEY_CREATED_AT = "created_at";
    private ParseObject obj;

    public Post() {
        super();
    }

    public Post(ParseObject obj){
        super();
        setObjectId(obj.getObjectId());
        setCaption(obj.getString(KEY_CAPTION));
        try {
            setUser(obj.getParseUser(KEY_USER).fetchIfNeeded());
        } catch (ParseException e) {
            e.printStackTrace();
        }
        setLikers(obj.getJSONArray(KEY_LIKERS));
        setPostImage(obj.getParseFile(KEY_POST_IMG));
        setLikes(obj.getInt(KEY_LIKES));
        put(KEY_UPDATED_AT, obj.getLong(KEY_UPDATED_AT));
        put(KEY_CREATED_AT, obj.getLong(KEY_CREATED_AT));
    }

    public void setCaption(String caption){
        put(KEY_CAPTION, caption);
    }

    public void setUser(ParseUser user){
        put(KEY_USER, user);
    }

    public void setLikes(int likes){
        put(KEY_LIKES, likes);
    }

    public int getLikes(){
        return getInt(KEY_LIKES);
    }

    public void setLikers(JSONArray likers){
        put(KEY_LIKERS, likers);
    }

    public JSONArray getLikers() {
        return getJSONArray("likers");
    }

    public String getCaption(){
        return getString(KEY_CAPTION);
    }

    public ParseUser getUser(){
        return getParseUser(KEY_USER);
    }

    public void setPostImage(ParseFile file){
        put("image", file);
    }

    public ParseFile getPostImage(){
        return getParseFile(KEY_POST_IMG);
    }

    public void setUpdatedAt(){
        put(KEY_UPDATED_AT, System.currentTimeMillis());
    }

    public void setCreatedAt(){
        put(KEY_CREATED_AT, System.currentTimeMillis());
    }

    public long getCreatedAtTimestamp() {
        return getLong("created_at");
    }
}
