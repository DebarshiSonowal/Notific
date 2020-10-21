package com.deb.notific;

import androidx.appcompat.app.AppCompatActivity;

import android.content.ClipData;
import android.content.ClipboardManager;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.view.View;
import android.widget.TextView;

import com.blogspot.atifsoftwares.animatoolib.Animatoo;
import com.skydoves.elasticviews.ElasticImageView;

public class About extends AppCompatActivity {
    ElasticImageView mailbtn;
    TextView email;

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        Animatoo.animateZoom(About.this);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_about);
        mailbtn = findViewById(R.id.loginbtn);
        email = findViewById(R.id.email);
        mailbtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent send = new Intent(Intent.ACTION_SENDTO);
                String uriText = "mailto:" + Uri.encode("d17co002@cit.ac.in") +
                        "?subject=" + Uri.encode(" ") +
                        "&body=" + Uri.encode("");
                Uri uri = Uri.parse(uriText);
                send.setData(uri);
                startActivity(Intent.createChooser(send, "Send mail..."));
                Animatoo.animateZoom(About.this);
            }
        });
        email.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                ClipboardManager manager = (ClipboardManager) getSystemService(CLIPBOARD_SERVICE);
                ClipData clipData = ClipData.newPlainText("email","d17co002@cit.ac.in");
                manager.setPrimaryClip(clipData);
            }
        });
    }
}