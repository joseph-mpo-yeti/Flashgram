package com.josephmpo.flashgram;

import android.content.Context;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentPagerAdapter;

public class LoginPagerAdapter extends FragmentPagerAdapter {

    LoginFragment.AfterLogin afterLogin;
    SignUpFragment.AfterSignUp afterSignUp;
    Context context;

    public LoginPagerAdapter(@NonNull FragmentManager fm, int behavior) {
        super(fm, behavior);
    }

    public LoginPagerAdapter(@NonNull FragmentManager fm,
        int behavior,
        Context context,
        LoginFragment.AfterLogin afterLogin,
        SignUpFragment.AfterSignUp afterSignUp) {

        super(fm, behavior);

        this.context = context;
        this.afterLogin = afterLogin;
        this.afterSignUp = afterSignUp;
    }

    @NonNull
    @Override
    public Fragment getItem(int position) {
        if(position == 0) return new LoginFragment(context, afterLogin);
        return new SignUpFragment(context, afterSignUp);
    }

    @Override
    public int getCount() {
        return 2;
    }
}
