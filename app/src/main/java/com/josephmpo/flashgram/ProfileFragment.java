package com.josephmpo.flashgram;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityOptionsCompat;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;

import com.bumptech.glide.Glide;
import com.josephmpo.flashgram.databinding.FragmentHomeBinding;
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

public class ProfileFragment extends Fragment {
    public static final int EDIT_PROFILE_CODE = 45;
    FragmentProfileBinding binding;
    SignOutInterface signOutInterface;
    ProfileAdapter adapter;
    List<Post> posts;
    ParseUser user;

    public interface SignOutInterface {
        void signOut();
    }

    public ProfileFragment() {
    }

    public ProfileFragment(SignOutInterface signOutInterface) {
        this.signOutInterface = signOutInterface;
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        binding = FragmentProfileBinding.inflate(inflater, container, false);
        return binding.getRoot();
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        binding.btnLogout.setVisibility(View.VISIBLE);

        posts = new ArrayList<>();

        adapter = new ProfileAdapter(posts, getContext(), getActivity());
        binding.rvUserPosts.setAdapter(adapter);
        binding.rvUserPosts.setLayoutManager(new GridLayoutManager(getContext(), 3));

        binding.refreshProfile.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                updateUI();
                binding.refreshProfile.setRefreshing(false);
            }
        });

        binding.btnLogout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                signOutInterface.signOut();
            }
        });

        try {
            user = ParseUser.getCurrentUser().fetchIfNeeded();
            updateUI();
            fetchPosts(user, posts, adapter);
            binding.btnEditProfile.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Intent intent = new Intent(getContext(), EditProfileActivity.class);
                    intent.putExtra("user_id", user.getObjectId());
                    ActivityOptionsCompat options = ActivityOptionsCompat
                            .makeSceneTransitionAnimation((Activity) getContext(), binding.ivProfile, "profileImg");
                    startActivityForResult(intent, EDIT_PROFILE_CODE, options.toBundle());
                }
            });
        } catch (ParseException e) {
            e.printStackTrace();
        }

    }

    private void updateUI() {
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
            Glide.with(getContext())
                    .load(imgUri)
                    .centerInside()
                    .circleCrop()
                    .into(binding.ivProfile);
        } else {
            Glide.with(getContext())
                    .load(R.drawable.person_color)
                    .centerInside()
                    .circleCrop()
                    .into(binding.ivProfile);
        }
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, @Nullable @org.jetbrains.annotations.Nullable Intent data) {
        if(requestCode == EDIT_PROFILE_CODE && resultCode == Activity.RESULT_OK){
            updateUI();
            Toasty.success(getContext(), "Profile updated!", Toasty.LENGTH_SHORT).show();

        }
        super.onActivityResult(requestCode, resultCode, data);
    }

    @Override
    public void onResume() {
        if(user != null){
            fetchPosts(user, posts, adapter);
            updateUI();
        }
        super.onResume();
    }

    private void fetchPosts(ParseUser user, List<Post> posts, ProfileAdapter adapter) {
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
