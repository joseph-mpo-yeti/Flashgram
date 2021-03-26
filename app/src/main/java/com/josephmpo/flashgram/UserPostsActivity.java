package com.josephmpo.flashgram;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.recyclerview.widget.SimpleItemAnimator;

import android.app.Activity;
import android.os.Bundle;
import android.util.Log;

import com.josephmpo.flashgram.databinding.ActivityUserPostsBinding;
import com.parse.FindCallback;
import com.parse.ParseException;
import com.parse.ParseObject;
import com.parse.ParseQuery;
import com.parse.ParseUser;

import java.util.ArrayList;
import java.util.List;

import es.dmoral.toasty.Toasty;

public class UserPostsActivity extends AppCompatActivity implements PostAdapter.OnDeletePost {
    ActivityUserPostsBinding binding;
    EndlessRecyclerViewScrollListener scrollListener;
    List<Post> posts;
    PostAdapter adapter;
    int position;
    String userId;
    ParseUser user;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityUserPostsBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        position = getIntent().getIntExtra("position", 0);
        userId = getIntent().getStringExtra("user_id");

        posts = new ArrayList<>();
        adapter = new PostAdapter(this, posts, this);
        LinearLayoutManager layoutManager = new LinearLayoutManager(this);
        binding.rvPosts.setAdapter(adapter);
        binding.rvPosts.setLayoutManager(layoutManager);
        ((SimpleItemAnimator) binding.rvPosts.getItemAnimator()).setSupportsChangeAnimations(false);

        getUser();

        scrollListener = new EndlessRecyclerViewScrollListener(layoutManager) {
            @Override
            public void onLoadMore(int page, int totalItemsCount, RecyclerView view) {
                infiniteScroll();
            }
        };

        binding.rvPosts.addOnScrollListener(scrollListener);

    }

    private void getUser() {
        ParseQuery<ParseUser> query = ParseUser.getQuery();
        query.whereEqualTo("objectId", userId);

        query.findInBackground(new FindCallback<ParseUser>() {
            @Override
            public void done(List<ParseUser> objects, ParseException e) {
                if( e == null ){
                    if(objects.size() > 0){
                        user = objects.get(0);
                        fetchData(position, user);
                    } else {
                        Toasty.error(getApplicationContext(), "No user found", Toasty.LENGTH_SHORT).show();
                    }
                } else {
                    Toasty.error(getApplicationContext(), e.getMessage(), Toasty.LENGTH_SHORT).show();
                }
            }
        });

    }


    private void infiniteScroll(){
        fetchData(position, user);
    }

    private void fetchData(int position, ParseUser user) {
        ParseQuery<ParseObject> query = ParseQuery.getQuery("Post");
        query.whereEqualTo("user", user);
        query.orderByDescending("created_at");
        query.setLimit(10);
        query.setSkip(posts.size());
        query.findInBackground(new FindCallback<ParseObject>() {
            @Override
            public void done(List<ParseObject> objects, ParseException e) {
                if(e == null){
                    for (ParseObject obj : objects) {
                        Post p = new Post(obj);
                        posts.add(p);
                        adapter.notifyItemInserted(posts.size()-1);
                        adapter.notifyItemChanged(posts.size()-1);
                    }
                    binding.rvPosts.scrollToPosition(position);
                }
            }
        });

    }

    @Override
    public void notifyPostDeleted(int position) {
        posts.remove(position);
        adapter.notifyItemRemoved(position);
        adapter.notifyItemChanged(position);
    }
}