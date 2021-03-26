package com.josephmpo.flashgram;

import android.Manifest;
import android.content.Context;
import android.graphics.Bitmap;
import android.os.Bundle;
import android.os.Environment;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.InputMethodManager;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import com.bumptech.glide.Glide;
import com.google.android.material.textfield.TextInputLayout;
import com.josephmpo.flashgram.databinding.FragmentNewPostBinding;
import com.otaliastudios.cameraview.BitmapCallback;
import com.otaliastudios.cameraview.CameraListener;
import com.otaliastudios.cameraview.CameraView;
import com.otaliastudios.cameraview.FileCallback;
import com.otaliastudios.cameraview.PictureResult;
import com.otaliastudios.cameraview.controls.Audio;
import com.otaliastudios.cameraview.controls.Control;
import com.otaliastudios.cameraview.controls.Facing;
import com.otaliastudios.cameraview.controls.Mode;
import com.otaliastudios.cameraview.size.AspectRatio;
import com.otaliastudios.cameraview.size.SizeSelectors;
import com.parse.ParseException;
import com.parse.ParseFile;
import com.parse.ParseUser;
import com.parse.SaveCallback;

import org.jetbrains.annotations.NotNull;
import org.json.JSONArray;

import java.io.File;
import java.io.IOException;
import java.io.OutputStream;
import java.security.Permission;

import es.dmoral.toasty.Toasty;

public class NewPostFragment extends Fragment {
    FragmentNewPostBinding binding;
    CameraView cameraView;
    OpenHome openHome;
    File photo;

    public interface OpenHome {
        public void goHome();
    }

    public NewPostFragment(OpenHome home) {
        this.openHome = home;
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        binding = FragmentNewPostBinding.inflate(inflater, container, false);
        return binding.getRoot();
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        cameraView = binding.camera;
        cameraView.setRequestPermissions(true);
        cameraView.setAudio(Audio.OFF);
        cameraView.setMode(Mode.PICTURE);
        cameraView.setLifecycleOwner(getViewLifecycleOwner());

        cameraView.addCameraListener(new CameraListener() {
            @Override
            public void onPictureTaken(@NonNull PictureResult result) {
                long timestamp = System.currentTimeMillis();
                String fileName = "photo_"+timestamp+".jpg";
                photo = new File(getContext().getFilesDir()
                        .getAbsolutePath()+ File.separator + fileName);

                result.toFile(photo, new FileCallback() {
                    @Override
                    public void onFileReady(@Nullable File file) {
                        Glide.with(getContext())
                                .load(photo)
                                .centerInside()
                                .centerCrop()
                                .into(binding.image);

                        binding.btnSubmitPost.setVisibility(View.VISIBLE);
                        hideCamera();
                    }
                });
            }
        });

        binding.btnCloseCamera.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                hideCamera();
            }
        });

        binding.btnAddImg.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                showCamera();
            }
        });

        binding.getRoot().setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                hideKeyboard(view);
            }
        });

        binding.btnCapture.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                cameraView.takePicture();
            }
        });

        binding.reverseCamera.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(cameraView.getFacing() == Facing.BACK){
                    cameraView.setFacing(Facing.FRONT);
                } else {
                    cameraView.setFacing(Facing.BACK);
                }
            }
        });

        binding.etCaption.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                if(binding.etCaption.getText().length() > 0 ){
                    binding.textInputLayout.setError("");
                } else {
                    binding.textInputLayout.setError("A caption is required");
                }
            }

            @Override
            public void afterTextChanged(Editable s) {

            }
        });

        binding.btnSubmitPost.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String caption = binding.etCaption.getText().toString();
                if(caption.length() > 0){
                    Post post = new Post();
                    post.setCreatedAt();
                    post.setUpdatedAt();
                    post.setUser(ParseUser.getCurrentUser());
                    post.setCaption(caption);
                    post.setLikers(new JSONArray());
                    post.setLikes(0);
                    post.setPostImage(new ParseFile(photo));
                    post.saveInBackground(new SaveCallback() {
                        @Override
                        public void done(ParseException e) {
                            if(e == null){
                                if(photo != null && photo.exists()){
                                    photo.deleteOnExit();
                                }
                                Toasty.success(getContext(), "Picture posted!", Toasty.LENGTH_SHORT).show();
                                openHome.goHome();
                            } else {
                                Toasty.error(getContext(), e.getMessage(), Toasty.LENGTH_SHORT).show();
                            }
                        }
                    });
                } else {
                    binding.textInputLayout.setError("A caption is required");
                }
            }
        });


    }

    private void hideKeyboard(View v) {
        InputMethodManager imm = (InputMethodManager) v.getContext().getSystemService(Context.INPUT_METHOD_SERVICE);
        imm.hideSoftInputFromWindow(v.getWindowToken(), 0);
        binding.etCaption.clearFocus();
    }

    @Override
    public void onPause() {
        if(photo != null && photo.exists()){
            binding.image.setImageDrawable(null);
            binding.etCaption.setText("");
            binding.textInputLayout.setError("");
            binding.etCaption.clearFocus();
            photo.deleteOnExit();
        }
        super.onPause();
    }

    @Override
    public void onResume() {
        if(cameraView != null && !cameraView.isOpened()){
            showCamera();
        }
        super.onResume();
    }

    private void hideCamera() {
        cameraView.close();
        binding.actions.setVisibility(View.GONE);
        binding.image.setVisibility(View.VISIBLE);
        binding.btnAddImg.setVisibility(View.VISIBLE);
        binding.etCaption.setVisibility(View.VISIBLE);
        binding.textInputLayout.setVisibility(View.VISIBLE);
        binding.camera.setVisibility(View.GONE);
    }

    private void showCamera() {
        cameraView.open();
        binding.btnAddImg.setVisibility(View.GONE);
        binding.actions.setVisibility(View.VISIBLE);
        binding.image.setVisibility(View.GONE);
        binding.btnSubmitPost.setVisibility(View.GONE);
        binding.etCaption.setVisibility(View.GONE);
        binding.textInputLayout.setVisibility(View.GONE);
        binding.camera.setVisibility(View.VISIBLE);
    }



}
