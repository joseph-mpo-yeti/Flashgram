package com.josephmpo.flashgram;

import android.content.Context;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentPagerAdapter;

public class LoginPagerAdapter extends FragmentPagerAdapter {

    LoginFragment.Helpers loginHelpers;
    SignUpFragment.Helpers signupHelpers;
    Context context;

    public LoginPagerAdapter(@NonNull FragmentManager fm, int behavior) {
        super(fm, behavior);
    }

    public LoginPagerAdapter(@NonNull FragmentManager fm,
        int behavior,
        Context context,
        LoginFragment.Helpers loginHelpers,
        SignUpFragment.Helpers signupHelpers) {

        super(fm, behavior);

        this.context = context;
        this.loginHelpers = loginHelpers;
        this.signupHelpers = signupHelpers;
    }

    @NonNull
    @Override
    public Fragment getItem(int position) {
        if(position == 0) return new LoginFragment(context, loginHelpers);
        return new SignUpFragment(context, signupHelpers);
    }

    @Override
    public int getCount() {
        return 2;
    }
}
