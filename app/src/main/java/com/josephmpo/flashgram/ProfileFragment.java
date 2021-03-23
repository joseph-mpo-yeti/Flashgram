package com.josephmpo.flashgram;

import android.content.Context;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import com.josephmpo.flashgram.databinding.FragmentHomeBinding;
import com.josephmpo.flashgram.databinding.FragmentProfileBinding;
import com.parse.LogOutCallback;
import com.parse.ParseException;
import com.parse.ParseUser;

public class ProfileFragment extends Fragment {
    FragmentProfileBinding binding;
    SignOutInterface signOutInterface;

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

        binding.btnLogout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                signOutInterface.signOut();
            }
        });

    }
}
