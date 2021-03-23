package com.josephmpo.flashgram;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import com.josephmpo.flashgram.databinding.FragmentSignUpBinding;
import com.parse.ParseException;
import com.parse.ParseUser;
import com.parse.SignUpCallback;

public class SignUpFragment extends Fragment {
    public static final String TAG = "SignUpFragment";
    FragmentSignUpBinding bnd;
    Context context;
    AfterSignUp afterSignUp;

    public interface AfterSignUp {
        void startMainActivity();
    }

    public SignUpFragment() {
        // Required empty public constructor
    }

    public SignUpFragment(Context context, AfterSignUp afterSignUp) {
        this.context = context;
        this.afterSignUp = afterSignUp;
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
                String username = bnd.etUsername.getText().toString().trim();
                String password = bnd.etPassword.getText().toString().trim();

                ParseUser user = new ParseUser();

                if(username.length() > 0 && password.length() > 0){
                    user.setUsername(username);
                    user.setPassword(password);

                    user.signUpInBackground(new SignUpCallback() {
                        @Override
                        public void done(ParseException e) {
                            if(e == null){
                                bnd.etPassword.setText("");
                                bnd.etUsername.setText("");
                                afterSignUp.startMainActivity();
                            } else {
                                Log.i(TAG, "done: login");
                                Toast.makeText(context, e.getMessage(), Toast.LENGTH_SHORT).show();
                            }
                        }
                    });
                }
            }
        });

    }
}