package com.josephmpo.flashgram;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityOptionsCompat;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;

import android.app.Activity;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;

import com.bumptech.glide.Glide;
import com.josephmpo.flashgram.databinding.FragmentProfileBinding;
import com.parse.FindCallback;
import com.parse.LogOutCallback;
import com.parse.ParseException;
import com.parse.ParseFile;
import com.parse.ParseObject;
import com.parse.ParseQuery;
import com.parse.ParseUser;

import java.util.ArrayList;
import java.util.List;

import es.dmoral.toasty.Toasty;

import static com.josephmpo.flashgram.ProfileFragment.EDIT_PROFILE_CODE;

public class ProfileActivity extends AppCompatActivity {
    FragmentProfileBinding binding;
    ParseUser user;
    List<Post> posts;
    ProfileAdapter adapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        binding = FragmentProfileBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());
        String userId = getIntent().getStringExtra("user_id");

        binding.btnLogout.setVisibility(View.GONE);

        posts = new ArrayList<>();
        adapter = new ProfileAdapter(posts, this, this);
        binding.rvUserPosts.setAdapter(adapter);
        binding.rvUserPosts.setLayoutManager(new GridLayoutManager(this, 3));

        binding.btnEditProfile.setVisibility(View.GONE);
        binding.btnSendMessage.setVisibility(View.GONE);
        binding.btnFollow.setVisibility(View.GONE);

        binding.btnEditProfile.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(ProfileActivity.this, EditProfileActivity.class);
                intent.putExtra("user_id", user.getObjectId());
                ActivityOptionsCompat options = ActivityOptionsCompat
                        .makeSceneTransitionAnimation(ProfileActivity.this, binding.ivProfile, "profileImg");
                startActivityForResult(intent, EDIT_PROFILE_CODE, options.toBundle());
            }
        });

        binding.refreshProfile.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                getUserAndUpdateUI(userId);
                binding.refreshProfile.setRefreshing(false);
            }
        });

        getUserAndUpdateUI(userId);

    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {

        if(resultCode == RESULT_OK && requestCode == EDIT_PROFILE_CODE){
            String userId = data.getStringExtra("user_id");
            getUserAndUpdateUI(userId);
        }
        super.onActivityResult(requestCode, resultCode, data);
    }

    @Override
    protected void onResume() {
        if(user != null){
            getUserAndUpdateUI(user.getObjectId());
        }
        super.onResume();
    }

    private void getUserAndUpdateUI(String userId) {
        ParseQuery<ParseUser> query = ParseUser.getQuery();
        query.whereEqualTo("objectId", userId);

        query.findInBackground(new FindCallback<ParseUser>() {
            @Override
            public void done(List<ParseUser> objects, ParseException e) {
                if( e == null ){
                    if(objects.size() > 0){
                        user = objects.get(0);
                        if(user.getObjectId().equals(ParseUser.getCurrentUser().getObjectId())){
                            binding.btnEditProfile.setVisibility(View.VISIBLE);
                            binding.btnSendMessage.setVisibility(View.GONE);
                            binding.btnFollow.setVisibility(View.GONE);
                            setSupportActionBar(binding.toolbar);
                            getSupportActionBar().setTitle("");
                        } else {
                            binding.btnEditProfile.setVisibility(View.GONE);
                            binding.btnSendMessage.setVisibility(View.VISIBLE);
                            binding.btnFollow.setVisibility(View.VISIBLE);
                        }

                        String username, name, bio, website;
                        long followers, following, posts;
                        username = user.getUsername();
                        name = user.getString("fullName");
                        bio = user.getString("bio");
                        website = user.getString("website");
                        followers = user.getLong("followers");
                        following = user.getLong("following");
                        posts = user.getLong("posts");

                        if (username == null || username.length() == 0) binding.tvUsername.setVisibility(View.GONE);
                        if (bio == null || bio.length() == 0) binding.tvBio.setVisibility(View.GONE);
                        if (name == null || name.length() == 0) binding.tvName.setVisibility(View.GONE);
                        if (website == null || website.length() == 0) binding.tvLink.setVisibility(View.GONE);

                        binding.tvUsername.setText(username);
                        binding.tvBio.setText(bio);
                        binding.tvName.setText(name);
                        binding.tvLink.setText( (website == "" || website == null) ? "" :
                                website.replace("https://", "")
                                        .replace("http://", ""));
                        binding.tvFollowersCount.setText(String.valueOf(followers));
                        binding.tvFollowingCount.setText(String.valueOf(following));
                        binding.tvPostsCount.setText(String.valueOf(posts));

                        ParseFile picture = user.getParseFile("profilePicture");
                        Uri imgUri = picture != null ? Uri.parse(picture.getUrl()) : null;

                        if(imgUri != null){
                            Glide.with(getApplicationContext())
                                    .load(imgUri)
                                    .centerInside()
                                    .circleCrop()
                                    .into(binding.ivProfile);
                        } else {
                            Glide.with(getApplicationContext())
                                    .load(R.drawable.person_color)
                                    .centerInside()
                                    .circleCrop()
                                    .into(binding.ivProfile);
                        }

                        fetchPosts(user);
                    } else {
                        Toasty.error(getApplicationContext(), "No user found", Toasty.LENGTH_SHORT).show();
                    }
                } else {
                    Toasty.error(getApplicationContext(), e.getMessage(), Toasty.LENGTH_SHORT).show();
                }
            }
        });
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        if(item.getItemId() == R.id.itemLogout){
            ParseUser.logOutInBackground(new LogOutCallback() {
                @Override
                public void done(ParseException e) {
                    if(e == null){
                        finishAfterTransition();
                    }
                }
            });
        }
        return true;
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        if(user.equals(ParseUser.getCurrentUser())){
            getMenuInflater().inflate(R.menu.profile_activity_menu, menu);
        }
        return super.onCreateOptionsMenu(menu);
    }

    private void fetchPosts(ParseUser user) {
        ParseQuery<ParseObject> query = ParseQuery.getQuery("Post");
        query.setLimit(10);
        query.whereEqualTo("user", user);
        query.orderByDescending("created_at");

        query.findInBackground(new FindCallback<ParseObject>() {
            @Override
            public void done(List<ParseObject> objects, ParseException e) {
                if(e == null){
                    posts.clear();
                    for (ParseObject obj : objects) {
                        posts.add(new Post(obj));
                    }
                    binding.tvPostsCount.setText(String.valueOf(posts.size()));
                    adapter.notifyDataSetChanged();
                }
            }
        });
    }

}