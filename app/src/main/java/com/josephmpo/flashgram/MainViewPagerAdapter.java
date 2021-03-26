package com.josephmpo.flashgram;

import android.app.Activity;
import android.content.Context;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentPagerAdapter;

public class MainViewPagerAdapter extends FragmentPagerAdapter {
    ProfileFragment.SignOutInterface signOutInterface;
    Context context;

    public MainViewPagerAdapter(@NonNull FragmentManager fm, int behavior, Context context, ProfileFragment.SignOutInterface signOutInterface) {
        super(fm, behavior);
        this.signOutInterface = signOutInterface;
        this.context = context;
    }

    @NonNull
    @Override
    public Fragment getItem(int position) {
        switch (position){
            case 1:
                return new NewPostFragment((NewPostFragment.OpenHome) context);
            case 2:
                return new ProfileFragment(signOutInterface);
            default:
                return new HomeFragment(context);
        }
    }

    @Override
    public int getCount() {
        return 3;
    }
}
