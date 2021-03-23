package com.josephmpo.flashgram;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentPagerAdapter;

public class MainViewPagerAdapter extends FragmentPagerAdapter {
    ProfileFragment.SignOutInterface signOutInterface;
    public MainViewPagerAdapter(@NonNull FragmentManager fm, int behavior, ProfileFragment.SignOutInterface signOutInterface) {
        super(fm, behavior);
        this.signOutInterface = signOutInterface;
    }

    @NonNull
    @Override
    public Fragment getItem(int position) {
        switch (position){
            case 1:
                return new NewPostFragment();
            case 2:
                return new ProfileFragment(signOutInterface);
            default:
                return new HomeFragment();
        }
    }

    @Override
    public int getCount() {
        return 3;
    }
}
