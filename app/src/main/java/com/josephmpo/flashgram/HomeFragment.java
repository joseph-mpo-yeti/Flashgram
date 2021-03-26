package com.josephmpo.flashgram;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.recyclerview.widget.SimpleItemAnimator;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;

import com.josephmpo.flashgram.databinding.FragmentHomeBinding;
import com.parse.FindCallback;
import com.parse.ParseException;
import com.parse.ParseObject;
import com.parse.ParseQuery;
import com.parse.ParseUser;

import java.util.ArrayList;
import java.util.List;

import es.dmoral.toasty.Toasty;

public class HomeFragment extends Fragment implements PostAdapter.OnDeletePost {
    public static final String TAG = "HomeFragment";
    FragmentHomeBinding binding;
    List<Post> posts;
    PostAdapter adapter;
    Context context;
    int page;
    public static String currentUserId;
    EndlessRecyclerViewScrollListener scrollListener;
    LinearLayoutManager layoutManager;

    public HomeFragment() {
    }

    public HomeFragment(Context context) {
        this.context = context;
        currentUserId = ParseUser.getCurrentUser().getObjectId();
        this.page = 1;
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        binding = FragmentHomeBinding.inflate(inflater, container, false);
        return binding.getRoot();
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        posts = new ArrayList<>();
        layoutManager = new LinearLayoutManager(context);
        adapter = new PostAdapter(context, posts, this);
        binding.rvPosts.setAdapter(adapter);
        binding.rvPosts.setLayoutManager(layoutManager);
        ((SimpleItemAnimator) binding.rvPosts.getItemAnimator()).setSupportsChangeAnimations(false);
        refreshData();

        binding.swipeRefresh.setColorSchemeColors(
                getResources().getColor(android.R.color.holo_orange_dark),
                getResources().getColor(android.R.color.holo_purple),
                getResources().getColor(android.R.color.holo_red_dark),
                getResources().getColor(android.R.color.holo_blue_dark));

        scrollListener = new EndlessRecyclerViewScrollListener(layoutManager) {
            @Override
            public void onLoadMore(int page, int totalItemsCount, RecyclerView view) {
                infiniteScroll();
            }
        };

        binding.rvPosts.addOnScrollListener(scrollListener);

        binding.swipeRefresh.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                refreshData();
            }
        });

    }

    private void infiniteScroll(){
        ParseQuery<ParseObject> query = ParseQuery.getQuery("Post");
        query.orderByDescending("created_at");
        query.setLimit(10);
        query.setSkip((posts.size()));

        Log.i(TAG, "infiniteScroll: fetching...");

        if(posts.size() > 0){
            query.whereLessThan("created_at", posts.get(posts.size()-1).getCreatedAtTimestamp());
        }

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
                }
            }
        });
    }

    private void refreshData() {
        ParseQuery<ParseObject> query = ParseQuery.getQuery("Post");
        query.orderByDescending("created_at");
        query.setLimit(10);

        if(posts.size() == 0){
            query.findInBackground(new FindCallback<ParseObject>() {
                @Override
                public void done(List<ParseObject> objects, ParseException e) {
                    if (e == null) {
                        posts.clear();
                        for (ParseObject obj : objects) {
                            posts.add(new Post(obj));
                        }
                        adapter.notifyDataSetChanged();
                        binding.swipeRefresh.setRefreshing(false);
                    }
                }
            });
        } else {
            query.whereGreaterThan("created_at", posts.get(0).getCreatedAtTimestamp());
            query.findInBackground(new FindCallback<ParseObject>() {
                @Override
                public void done(List<ParseObject> objects, ParseException e) {
                    if (e == null) {
                        for (ParseObject obj : objects) {
                            posts.add(0, new Post(obj));
                            adapter.notifyItemInserted(0);
                            adapter.notifyItemChanged(0);
                        }
                        binding.swipeRefresh.setRefreshing(false);
                    }
                }
            });
        }
    }

    @Override
    public void onResume() {
        refreshData();
        super.onResume();
    }

    @Override
    public void notifyPostDeleted(int position) {
        posts.remove(position);
        adapter.notifyItemRemoved(position);
        adapter.notifyItemChanged(position);
    }

}
