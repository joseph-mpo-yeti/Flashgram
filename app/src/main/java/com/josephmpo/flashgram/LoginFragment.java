package com.josephmpo.flashgram;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import com.josephmpo.flashgram.databinding.FragmentLoginBinding;
import com.parse.LogInCallback;
import com.parse.ParseException;
import com.parse.ParseUser;
import com.parse.SignUpCallback;

import es.dmoral.toasty.Toasty;


public class LoginFragment extends Fragment {
    public static final String TAG = "LoginFragment";
    FragmentLoginBinding binding;
    Context context;
    Helpers helpers;

    public interface Helpers {
        void startMainActivity();
        void hideKeyboard();
    }

    public LoginFragment() {
        // Required empty public constructor
    }

    public LoginFragment(Context context, Helpers helpers) {
        this.context = context;
        this.helpers = helpers;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        binding = FragmentLoginBinding.inflate(inflater, container, false);
        return binding.getRoot();
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        binding.getRoot().setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(view.getId() != R.id.etPassword && view.getId() != R.id.etUsername){
                    binding.etPassword.clearFocus();
                    binding.etUsername.clearFocus();
                    helpers.hideKeyboard();
                }
            }
        });

        binding.btnLogin.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String username = binding.etUsername.getText().toString().trim();
                String password = binding.etPassword.getText().toString().trim();

                if (username.length() == 0){
                    binding.etUsername.setError("Username required");
                } else if (username.length() < 3){
                    binding.etUsername.setError("Username too short");
                } else if(password.length() == 0){
                    binding.etPassword.setError("Password required");
                } else if(password.length() < 6){
                    binding.etPassword.setError("Password too short");
                } else {
                    ParseUser.logInInBackground(username, password, new LogInCallback() {
                        @Override
                        public void done(ParseUser user, ParseException e) {
                            if (e == null) {
                                helpers.startMainActivity();
                            } else {
                                Toasty.error(context, e.getMessage(), Toasty.LENGTH_SHORT).show();
                            }
                        }
                    });
                }
            }
        });
    }
}