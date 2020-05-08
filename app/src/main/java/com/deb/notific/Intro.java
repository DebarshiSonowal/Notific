package com.deb.notific;

import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;

import android.Manifest;
import android.graphics.Color;
import android.os.Bundle;

import com.github.appintro.AppIntro2;
import com.github.appintro.AppIntroFragment;

public class Intro extends AppIntro2 {

    @Override
    protected void onSkipPressed(Fragment currentFragment) {
        super.onSkipPressed(currentFragment);
    }

    @Override
    protected void onDonePressed(Fragment currentFragment) {
        super.onDonePressed(currentFragment);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
//        setContentView(R.layout.activity_intro);
//        addSlide(AppIntroFragment.newInstance("The First Page","Give the required permission",R.drawable.communications, Color.parseColor("#252525")));
//        addSlide(AppIntroFragment.newInstance("The Second Page","Give the required permission",R.drawable.letter, Color.parseColor("#252525")));
//        addSlide(AppIntroFragment.newInstance("The Third Page","Give the required permission",R.drawable.notebook, Color.parseColor("#252525")));
//        addSlide(AppIntroFragment.newInstance("The ForthPage","Give the required permission",R.drawable.technology, Color.parseColor("#252525")));
//        askForPermissions(new String[]{Manifest.permission.ACCESS_FINE_LOCATION, Manifest.permission.ACCESS_COARSE_LOCATION},1,true);
//        askForPermissions(new String[]{Manifest.permission.INTERNET},2,true);
//        askForPermissions(new String[]{Manifest.permission.READ_CALL_LOG},3,true);
//        askForPermissions(new String[]{Manifest.permission.SEND_SMS},4,true);
//        setColorTransitionsEnabled(true);
    }
}
