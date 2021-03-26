package com.josephmpo.flashgram;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import androidx.annotation.NonNull;
import androidx.core.app.ActivityOptionsCompat;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.josephmpo.flashgram.databinding.FragmentProfileBinding;
import com.josephmpo.flashgram.databinding.ItemProfilePostBinding;
import com.parse.ParseException;
import com.parse.ParseUser;

import java.io.File;
import java.util.List;

public class ProfileAdapter extends RecyclerView.Adapter<ProfileAdapter.ViewHolder> {

    List<Post> posts;
    Context context;
    Activity activity;

    public ProfileAdapter(List<Post> posts, Context context, Activity activity) {
        this.posts = posts;
        this.context = context;
        this.activity = activity;
    }

    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        return new ViewHolder(LayoutInflater.from(context)
                .inflate(R.layout.item_profile_post, parent, false));
    }

    @Override
    public void onBindViewHolder(@NonNull ProfileAdapter.ViewHolder holder, int position) {
        holder.bind(position);
    }

    @Override
    public int getItemCount() {
        return posts.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder {
        ImageView ivPostImg;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            ivPostImg = itemView.findViewById(R.id.ivPostImg);
        }

        public void bind(int position) {
            Post post = posts.get(position);

            try {
                File postImg = post.getPostImage().getFile();
                Glide.with(context)
                        .load(Uri.fromFile(postImg))
                        .centerInside()
                        .centerCrop()
                        .into(ivPostImg);
            } catch (ParseException e) {
                e.printStackTrace();
            }

            ivPostImg.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Intent intent = new Intent(context, UserPostsActivity.class);
                    try {
                        ParseUser user = post.getUser().fetchIfNeeded();
                        intent.putExtra("user_id", user.getObjectId());
                    } catch (ParseException e) {
                        e.printStackTrace();
                    }
                    intent.putExtra("position", position);
                    ActivityOptionsCompat options = ActivityOptionsCompat.
                            makeSceneTransitionAnimation(activity, v, "postImage");
                    context.startActivity(intent);
                }
            });
        }
    }
}
