package com.deb.notific;

import android.Manifest;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;

import androidx.core.content.ContextCompat;
import androidx.fragment.app.Fragment;

import com.github.paolorotolo.appintro.AppIntro;
import com.github.paolorotolo.appintro.AppIntroFragment;

public class Intro extends AppIntro {
    SharedPreferences sharedPreferences;
    SharedPreferences.Editor editor;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        addSlide(AppIntroFragment.newInstance("Welcome to Zone App", "An App that ensures that you are not Disturbed while Working",
                R.drawable.videocall, ContextCompat.getColor(getApplicationContext(), R.color.secondColor)));

        addSlide(AppIntroFragment.newInstance("Your Best Working Companion", "Mark the Locations where you don't want to be Disturbed",
                R.drawable.bluesign, ContextCompat.getColor(getApplicationContext(), R.color.firstColor)));

        addSlide(AppIntroFragment.newInstance("Never Miss Anything", "A Separate Notification Panel to Remind you what you Missed during Working Hours",
                R.drawable.bell, ContextCompat.getColor(getApplicationContext(), R.color.thirdColor)));
        setFadeAnimation();
        askForPermissions(new String[]{android.Manifest.permission.ACCESS_FINE_LOCATION, android.Manifest.permission.ACCESS_COARSE_LOCATION},1);
        askForPermissions(new String[]{android.Manifest.permission.INTERNET,Manifest.permission.SEND_SMS,Manifest.permission_group.STORAGE},2);
        askForPermissions(new String[]{android.Manifest.permission.READ_CALL_LOG,Manifest.permission.READ_CONTACTS},3);
//        askForPermissions(new String[]{Manifest.permission.SEND_SMS},4,true);
        sharedPreferences = getApplicationContext().getSharedPreferences("MinePreferences", Context.MODE_PRIVATE);
        editor = sharedPreferences.edit();

        if(sharedPreferences!=null){

            boolean checkShared = sharedPreferences.getBoolean("checkStated", false);

            if(checkShared == true){

                startActivity(new Intent(getApplicationContext(), login.class));
                finish();
            }
        }
    }

    @Override
    public void onSkipPressed(Fragment currentFragment) {
        super.onSkipPressed(currentFragment);
        // Do something when users tap on Skip button.

        startActivity(new Intent(getApplicationContext(), login.class));
        editor.putBoolean("checkStated", false).commit();
        finish();
    }

    @Override
    public void onDonePressed(Fragment currentFragment) {
        super.onDonePressed(currentFragment);
        // Do something when users tap on Done button.

        startActivity(new Intent(getApplicationContext(), login.class));
        editor.putBoolean("checkStated", true).commit();
        finish();
    }
}
