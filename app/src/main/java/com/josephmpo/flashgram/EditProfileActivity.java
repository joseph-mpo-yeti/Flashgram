package com.josephmpo.flashgram;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Handler;
import android.service.autofill.RegexValidator;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.InputMethodManager;
import android.webkit.URLUtil;
import android.widget.EditText;

import com.bumptech.glide.Glide;
import com.google.android.material.textfield.TextInputEditText;
import com.google.android.material.textfield.TextInputLayout;
import com.josephmpo.flashgram.databinding.ActivityEditProfileBinding;
import com.otaliastudios.cameraview.CameraListener;
import com.otaliastudios.cameraview.CameraView;
import com.otaliastudios.cameraview.FileCallback;
import com.otaliastudios.cameraview.PictureResult;
import com.otaliastudios.cameraview.controls.Audio;
import com.otaliastudios.cameraview.controls.Facing;
import com.otaliastudios.cameraview.controls.Mode;
import com.parse.FindCallback;
import com.parse.ParseException;
import com.parse.ParseFile;
import com.parse.ParseQuery;
import com.parse.ParseUser;
import com.parse.SaveCallback;

import org.jetbrains.annotations.NotNull;

import java.io.File;
import java.util.List;

import es.dmoral.toasty.Toasty;

import static com.josephmpo.flashgram.ProfileFragment.EDIT_PROFILE_CODE;

public class EditProfileActivity extends AppCompatActivity {
    public static final String TAG = "EditProfileActivity";
    ActivityEditProfileBinding bn;
    CameraView cameraView;
    int position;
    ParseUser user;
    File photo;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        bn = ActivityEditProfileBinding.inflate(getLayoutInflater());
        setContentView(bn.getRoot());

        cameraView = bn.camera;
        cameraView.setRequestPermissions(true);
        cameraView.setAudio(Audio.OFF);
        cameraView.setMode(Mode.PICTURE);
        cameraView.setLifecycleOwner(null);

        cameraView.addCameraListener(new CameraListener() {
            @Override
            public void onPictureTaken(@NonNull @NotNull PictureResult result) {
                long timestamp = System.currentTimeMillis();
                String fileName = "photo_"+timestamp+".jpg";
                photo = new File(getFilesDir()
                        .getAbsolutePath()+ File.separator + fileName);

                result.toFile(photo, new FileCallback() {
                    @Override
                    public void onFileReady(@Nullable File file) {
                        Glide.with(getApplicationContext())
                                .load(photo)
                                .centerInside()
                                .circleCrop()
                                .into(bn.imagePreview);

                        showPreview();
                    }
                });
                super.onPictureTaken(result);
            }
        });

        getUserAndUpdateUI();

        bn.btnConfirm.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                bn.etName.clearFocus();
                bn.etUsername.clearFocus();
                bn.etBio.clearFocus();
                bn.etWebsite.clearFocus();
                bn.progressCircular.setVisibility(View.VISIBLE);
                isValid(bn.etUsername.getText().toString(), bn.etWebsite.getText().toString());
            }
        });

        bn.btnPreviewConfirm.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                ParseUser user = ParseUser.getCurrentUser();
                user.put("profilePicture", new ParseFile(photo));
                user.saveInBackground(new SaveCallback() {
                    @Override
                    public void done(ParseException e) {
                        if(photo != null && photo.exists()){
                            photo.deleteOnExit();
                        }
                        getUserAndUpdateUI();
                        showProfile();
                    }
                });
            }
        });

        bn.changePicture.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                showCamera();
            }
        });

        bn.progressCircular.setIndicatorColor(
                getResources().getColor(android.R.color.holo_orange_dark),
                getResources().getColor(android.R.color.holo_purple),
                getResources().getColor(android.R.color.holo_red_dark),
                getResources().getColor(android.R.color.holo_blue_dark)
        );

        bn.btnCancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                endActivity(RESULT_CANCELED);
            }
        });

        bn.btnPreviewCancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                showProfile();
                bn.imagePreview.setImageDrawable(null);
            }
        });


        bn.btnCloseCamera.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                showProfile();
            }
        });

        bn.btnCapture.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                cameraView.takePicture();
            }
        });

        bn.reverseCamera.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(cameraView.getFacing() == Facing.BACK){
                    cameraView.setFacing(Facing.FRONT);
                } else {
                    cameraView.setFacing(Facing.BACK);
                }
            }
        });


        bn.getRoot().setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(!(v instanceof EditText || v instanceof TextInputLayout)){
                    bn.etUsername.clearFocus();
                    bn.etBio.clearFocus();
                    bn.etWebsite.clearFocus();
                    InputMethodManager imm = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
                    imm.hideSoftInputFromWindow(v.getWindowToken(), 0);
                }
            }
        });
    }

    private void showProfile() {
        cameraView.close();
        bn.profileContainer.setVisibility(View.VISIBLE);
        bn.previewContainer.setVisibility(View.GONE);
        bn.cameraContainer.setVisibility(View.GONE);
    }

    private void showCamera() {
        cameraView.open();
        bn.profileContainer.setVisibility(View.GONE);
        bn.previewContainer.setVisibility(View.GONE);
        bn.cameraContainer.setVisibility(View.VISIBLE);
    }

    private void showPreview() {
        cameraView.close();
        bn.profileContainer.setVisibility(View.GONE);
        bn.previewContainer.setVisibility(View.VISIBLE);
        bn.cameraContainer.setVisibility(View.GONE);
    }

    private void hideInputs() {
        bn.progressCircular.setVisibility(View.GONE);
        ((View) bn.etUsername.getParent()).setVisibility(View.GONE);
        ((View) bn.etWebsite.getParent()).setVisibility(View.GONE);
        ((View) bn.etName.getParent()).setVisibility(View.GONE);
        ((View) bn.etBio.getParent()).setVisibility(View.GONE);
    }

    private void getUserAndUpdateUI() {
        user = ParseUser.getCurrentUser();
        bn.etUsername.setText(user.getString("username"));;
        bn.etBio.setText(user.getString("bio"));
        bn.etWebsite.setText(user.getString("website"));
        bn.etName.setText(user.getString("fullName"));

        ParseFile picture = user.getParseFile("profilePicture");
        Uri imgUri = picture != null ? Uri.parse(picture.getUrl()) : null;

        if(imgUri != null){
            Glide.with(getApplicationContext())
                    .load(imgUri)
                    .centerInside()
                    .circleCrop()
                    .into(bn.ivProfileImage);
        } else {
            Glide.with(getApplicationContext())
                    .load(R.drawable.person_color)
                    .centerInside()
                    .circleCrop()
                    .into(bn.ivProfileImage);
        }
    }


    private void isValid(String username, String url){
        if(username == "") {
            bn.progressCircular.setVisibility(View.GONE);
            bn.etUsername.setHighlightColor(getResources().getColor(android.R.color.holo_red_dark));
            bn.etUsername.setError("Username required");
            return;
        }

        if(url.length() > 0 && (!URLUtil.isHttpUrl(url) && !URLUtil.isHttpsUrl(url))){
            bn.progressCircular.setVisibility(View.GONE);
            bn.etWebsite.setError("Enter a full url with http:// or https://");
            bn.etWebsite.setHighlightColor(getResources().getColor(android.R.color.holo_red_dark));
            return;
        }

        saveProfile();
    }

    private void saveProfile() {
        user.setUsername(bn.etUsername.getText().toString());
        user.put("fullName", bn.etName.getText().toString());
        user.put("bio", bn.etBio.getText().toString());
        user.put("website", bn.etWebsite.getText().toString());
        user.saveInBackground(new SaveCallback() {
            @Override
            public void done(ParseException e) {
                if(e == null){
                    endActivity(RESULT_OK);
                } else {
                    bn.progressCircular.setVisibility(View.GONE);
                    Toasty.error(getApplicationContext(), e.getMessage(), Toasty.LENGTH_SHORT).show();
                }
            }
        });
    }

    private void endActivity(int resultCode) {
        hideInputs();
        Intent intent = new Intent();
        if(resultCode == RESULT_OK){
            intent.putExtra("username", bn.etUsername.getText().toString());
            intent.putExtra("user_id", user.getObjectId());
            intent.putExtra("bio", bn.etBio.getText().toString());
            intent.putExtra("website", bn.etWebsite.getText().toString());
            intent.putExtra("name", bn.etName.getText().toString());
            intent.putExtra("position", position);
        }
        (new Handler()).postDelayed(new Runnable() {
            @Override
            public void run() {
                setResult(resultCode, intent);
                finishAfterTransition();
            }
        }, 100);
    }

    @Override
    protected void onPause() {
        cameraView.close();
        super.onPause();
    }

}