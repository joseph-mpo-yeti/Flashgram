package com.josephmpo.flashgram;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.net.Uri;
import android.os.Handler;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.InputMethodManager;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityOptionsCompat;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.google.android.material.dialog.MaterialAlertDialogBuilder;
import com.josephmpo.flashgram.databinding.ItemSinglePostBinding;
import com.parse.DeleteCallback;
import com.parse.LogOutCallback;
import com.parse.ParseException;
import com.parse.ParseFile;
import com.parse.ParseObject;
import com.parse.ParseUser;
import com.parse.SaveCallback;

import org.json.JSONArray;
import org.json.JSONException;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import es.dmoral.toasty.Toasty;

import static com.josephmpo.flashgram.HomeFragment.currentUserId;

@SuppressLint("SetTextI18n")
public class PostAdapter extends RecyclerView.Adapter<PostAdapter.ViewHolder> {

    List<Post> posts;
    Context context;
    OnDeletePost onDeletePost;


    public interface OnDeletePost {
        void notifyPostDeleted(int position);
    }

    public PostAdapter(Context context, List<Post> posts, OnDeletePost onDeletePost) {
        this.context = context;
        this.posts = posts;
        this.onDeletePost = onDeletePost;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        return new ViewHolder(LayoutInflater.from(parent.getContext())
                .inflate(R.layout.item_single_post, parent, false));
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        holder.bind(position);
    }

    @Override
    public void onViewDetachedFromWindow(@NonNull ViewHolder holder) {
        super.onViewDetachedFromWindow(holder);
        holder.handler.removeCallbacks(holder.doubleClick);
        holder.handler.removeCallbacks(holder.likeAnimation);
    }

    @Override
    public int getItemCount() {
        return posts.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder {
        static final String TAG = "ViewHolder";
        private ItemSinglePostBinding bd;
        private boolean liked, doubledClicked;
        private int taps;
        private Handler handler;
        private Runnable doubleClick, likeAnimation;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            bd = ItemSinglePostBinding.bind(itemView);
            handler = new Handler();
            doubledClicked = false;
            liked = false;
            doubleClick = new Runnable() {
                @Override
                public void run() {
                    doubledClicked = false;
                }
            };
            likeAnimation = new Runnable() {
                @Override
                public void run() {
                    bd.ivLiked.setVisibility(View.GONE);
                    bd.ivPostPicture.setAlpha(1f);
                }
            };
        }

        public void bind(int position) {
            liked = false;

            Post post = posts.get(position);

            JSONArray likers = post.getLikers();
            bd.tvCaption.setText(post.getCaption());
            bd.tvLikes.setText(post.getLikers().length() + " likes");
            bd.tvTime.setText(TimeFormatter.getTimeDifference(post.getCreatedAtTimestamp()));

            ParseUser owner = post.getUser();

            if(owner.getObjectId().equals(ParseUser.getCurrentUser().getObjectId())){
                bd.postMenu.setVisibility(View.VISIBLE);
            }

            ParseFile file = owner.getParseFile("profilePicture");
            Uri profileImgUri = null;
            if(file != null){
                profileImgUri = Uri.parse(file.getUrl());
            }

            Uri postImgUri = Uri.parse(post.getPostImage().getUrl());
            String username = owner.getUsername();
            username = owner.getObjectId().equals(currentUserId) ? "You" : username;
            bd.tvUsernameTop.setText(username);
            bd.tvUsername.setText(username);

            bd.btnLike.setImageDrawable(context.getDrawable(R.drawable.ufi_heart_icon));
            bd.btnLike.setScaleX(1f);
            bd.btnLike.setScaleY(1f);

            for (int i = 0; i < likers.length(); i++) {
                try {
                    if(currentUserId.equals(likers.getString(i))){
                        liked = true;
                        bd.btnLike.setImageDrawable(context.getDrawable(R.drawable.ic_baseline_favorite_red));
                        bd.btnLike.setScaleX(1.15f);
                        bd.btnLike.setScaleY(1.15f);
                    }
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }

            Glide.with(context)
                    .load(postImgUri)
                    .centerInside()
                    .centerCrop()
                    .into(bd.ivPostPicture);

            if(profileImgUri != null) {
                Glide.with(context)
                        .load(profileImgUri)
                        .centerInside()
                        .centerCrop()
                        .circleCrop()
                        .into(bd.ivProfilePicture);
            }

            bd.btnLike.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    updateLike(post);
                }
            });

            bd.ivPostPicture.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    if(doubledClicked){
                        if(!liked) updateLike(post);
                        showLikedAnimation();
                    } else {
                        doubledClicked = true;
                        handler.postDelayed(doubleClick, 500);
                    }
                }
            });

            bd.postMenu.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {

                    showDeleteAlert(post);
                }
            });

            bd.btnShare.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Uri uri = Uri.parse(post.getPostImage().getUrl());
                    Intent shareIntent = new Intent();
                    shareIntent.setAction(Intent.ACTION_SEND);
                    shareIntent.putExtra(Intent.EXTRA_TITLE, "Share Image");
                    shareIntent.putExtra(Intent.EXTRA_STREAM, uri);
                    shareIntent.setType("image/*");
                    context.startActivity(shareIntent);
                }
            });

            bd.userTop.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Intent intent = new Intent(context, ProfileActivity.class);
                    intent.putExtra("user_id", owner.getObjectId());
                    intent.putExtra("position", position);
                    ActivityOptionsCompat options = ActivityOptionsCompat
                            .makeSceneTransitionAnimation((Activity) context, bd.ivProfilePicture, "profileImg");
                    context.startActivity(intent, options.toBundle());
                }
            });
        }

        private void showDeleteAlert(Post post) {
            MaterialAlertDialogBuilder builder = new MaterialAlertDialogBuilder(context);
            builder.setTitle("Delete post");
            builder.setMessage("Are you sure?");
            builder.setNegativeButton("Delete", new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int which) {
                    dialog.dismiss();
                    post.deleteInBackground(new DeleteCallback() {
                        @Override
                        public void done(ParseException e) {
                            if(e == null){
                                onDeletePost.notifyPostDeleted(getAdapterPosition());
                                Toasty.success(context, "Post deleted", Toasty.LENGTH_LONG).show();
                            } else {
                                Toasty.error(context, e.getMessage(), Toasty.LENGTH_LONG).show();
                            }
                        }
                    });
                }
            });
            builder.setPositiveButton("Cancel", new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int which) {
                    dialog.dismiss();
                }
            });
            builder.show();
        }

        private void showLikedAnimation() {
            bd.ivPostPicture.setAlpha(.98f);
            bd.ivLiked.setVisibility(View.VISIBLE);
            handler.postDelayed(likeAnimation, 1000);
        }


        private void updateLike(Post post){
            JSONArray arr = post.getLikers();

            if(!liked){
                arr.put(currentUserId);
            } else {
                post.setLikes(post.getLikes() > 0 ? post.getLikes()-1 : 0);
                int index = -1;
                for (int j = 0; j < arr.length(); j++) {
                    try {
                        if(arr.getString(j).equals(currentUserId)){
                            index = j;
                            break;
                        }
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                }
                if (index >= 0) arr.remove(index);
            }
            post.setLikers(arr);
            post.setLikes(arr.length());
            post.setUpdatedAt();
            post.saveInBackground(new SaveCallback() {
                @Override
                public void done(ParseException e) {
                    notifyItemChanged(getAdapterPosition());
                }
            });
        }

    }

}
