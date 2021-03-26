package com.josephmpo.flashgram;

import android.content.Context;
import android.content.Intent;
import android.content.res.Resources;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import com.google.android.material.textfield.TextInputLayout;
import com.josephmpo.flashgram.databinding.FragmentSignUpBinding;
import com.parse.ParseException;
import com.parse.ParseUser;
import com.parse.SignUpCallback;

import es.dmoral.toasty.Toasty;

public class SignUpFragment extends Fragment {
    public static final String TAG = "SignUpFragment";
    FragmentSignUpBinding bnd;
    Context context;
    Helpers helpers;

    public interface Helpers {
        void startMainActivity();
        void hideKeyboard();
    }

    public SignUpFragment() {
        // Required empty public constructor
    }

    public SignUpFragment(Context context, Helpers helpers) {
        this.context = context;
        this.helpers = helpers;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        bnd = FragmentSignUpBinding.inflate(inflater, container, false);
        return bnd.getRoot();
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        bnd.btnSignup.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String name = bnd.etName.getText().toString().trim();
                String username = bnd.etUsername.getText().toString().trim();
                String password = bnd.etPassword.getText().toString().trim();

                ParseUser user = new ParseUser();

                if(name.length() == 0) {
                    bnd.etName.setError("Name required");
                } else if (name.length() < 3) {
                    bnd.etName.setError("Name too short");
                } else if (username.length() == 0){
                    bnd.etUsername.setError("Username required");
                } else if (username.length() < 3){
                    bnd.etUsername.setError("Username too short");
                } else if(password.length() == 0){
                    bnd.etPassword.setError("Password required");
                } else if(password.length() < 6){
                    bnd.etPassword.setError("Password too short");
                } else {
                    user.setUsername(username);
                    user.setPassword(password);
                    user.put("bio", "");
                    user.put("website", "");
                    user.put("fullName", name);
                    user.put("following", 0);
                    user.put("followers", 0);
                    user.put("posts", 0);
                    user.put("verified", false);

                    user.signUpInBackground(new SignUpCallback() {
                        @Override
                        public void done(ParseException e) {
                            if(e == null){
                                bnd.etPassword.setText("");
                                bnd.etUsername.setText("");
                                helpers.startMainActivity();
                            } else {
                                Toasty.error(context, e.getMessage(), Toasty.LENGTH_SHORT).show();
                            }
                        }
                    });
                }
            }
        });


        bnd.getRoot().setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(view.getId() != R.id.etPassword && view.getId() != R.id.etUsername){
                    bnd.etPassword.clearFocus();
                    bnd.etUsername.clearFocus();
                    helpers.hideKeyboard();
                }
            }
        });

    }
}