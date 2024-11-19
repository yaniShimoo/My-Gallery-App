package com.rabin.mygallery;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;

import com.airbnb.lottie.LottieAnimationView;

public class Logo extends AppCompatActivity {

    LottieAnimationView loadingBar;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_logo);
        loadingBar = findViewById(R.id.loading_bar);
        loadingBar.playAnimation();
        loadingBar.loop(true);
        new Thread(new Runnable() {
            @Override
            public void run() {
                try {
                    synchronized (this) {
                        wait(2000);
                        SharedPreferences sPref = getSharedPreferences("MyPref", MODE_PRIVATE);
                        String savedUserName = sPref.getString("PASSWORD", "");
                        if (savedUserName.equals("")) {
                            Intent intent = new Intent(Logo.this, Registration.class);
                            startActivity(intent);
                            finish();
                        }
                        else {
                            Intent intent = new Intent(Logo.this, Login.class);
                            startActivity(intent);
                            finish();
                        }

                    }
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            }
        }).start();
    }

}