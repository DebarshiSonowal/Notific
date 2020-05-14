package com.deb.notific;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.animation.ObjectAnimator;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.graphics.Typeface;
import android.os.Bundle;
import android.os.Handler;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.view.animation.BounceInterpolator;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.google.android.gms.maps.model.LatLng;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;


public class MainActivity extends AppCompatActivity {
    Animation topAnim, bottomAnim;
    ImageView image1, image2, image3, image4, image5, image6, image7, image8, image9, image10;
    TextView logo, tag;
    int SPLASH_TIME = 3000; //This is 3 seconds
Boolean serv;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        loadData();



        topAnim = AnimationUtils.loadAnimation(this,R.anim.top_animation);
        bottomAnim = AnimationUtils.loadAnimation(this,R.anim.bottom_animation);

        image1 = findViewById(R.id.top);
        image2 = findViewById(R.id.left);
        image3 = findViewById(R.id.right);
        image4 = findViewById(R.id.bottom);
        image5 = findViewById(R.id.bottomleft);
        image6 = findViewById(R.id.bottomright);
        image7 = findViewById(R.id.topleft);
        image8 = findViewById(R.id.topright);
        image9 = findViewById(R.id.worldm);
        image10 = findViewById(R.id.markperson);

        logo = findViewById(R.id.textView);
        tag = findViewById(R.id.textView2);

        image1.setAnimation(topAnim);
        image2.setAnimation(topAnim);
        image3.setAnimation(topAnim);
        image4.setAnimation(topAnim);
        image5.setAnimation(topAnim);
        image6.setAnimation(topAnim);
        image7.setAnimation(topAnim);
        image8.setAnimation(topAnim);
        image9.setAnimation(topAnim);
        image10.setAnimation(topAnim);

        logo.setAnimation(bottomAnim);
        tag.setAnimation(bottomAnim);

        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {

                Intent intent = new Intent(MainActivity.this,Intro.class);
                startActivity(intent);
                finish();
            }
        },SPLASH_TIME);

    }

    private void loadData() {
        SharedPreferences sharedPreferences = this.getSharedPreferences("sharedPrefs", Context.MODE_PRIVATE);
        serv = sharedPreferences.getBoolean("onswitch",true);
        if(serv)
        {
            Intent intent = new Intent(this,MyService.class);
            startService(intent);
        }
    }
//
//    private void setPref() {
//        SharedPreferences preferences = getSharedPreferences("prefs",MODE_PRIVATE);
//        SharedPreferences.Editor editor = preferences.edit();
//        editor.putBoolean("firstStart",false);
//        editor.apply();
//    }

    //Method to run progress bar for 5 seconds
}
