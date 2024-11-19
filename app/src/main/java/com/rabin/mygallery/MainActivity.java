package com.rabin.mygallery;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;

import android.Manifest;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.pm.PackageManager;
import android.graphics.Color;
import android.graphics.ColorFilter;
import android.graphics.PorterDuff;
import android.graphics.PorterDuffColorFilter;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.view.MotionEvent;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.Button;
import android.widget.CompoundButton;
import android.widget.ImageView;
import android.widget.Switch;
import android.widget.Toast;

import com.airbnb.lottie.LottieAnimationView;
import com.airbnb.lottie.LottieProperty;
import com.airbnb.lottie.model.KeyPath;
import com.airbnb.lottie.value.LottieFrameInfo;
import com.airbnb.lottie.value.SimpleLottieValueCallback;

public class MainActivity extends AppCompatActivity {


    IntentFilter filter;
    SMSIncomeReceiver smsIncomeReceiver = new SMSIncomeReceiver();
    Animation sUp, sDown, slideUp,slideDown;
    Button toGallery, toCustomers, toOrders, toSketches, toSms;
    Switch sw;
    ImageView bgMusic;
    LottieAnimationView musicSwitch, smsSwitch;
    boolean isMusicSwitchOn = false, isSmsSwitchOn = true;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.RECEIVE_SMS}, 1);
        filter = new IntentFilter("android.provider.Telephony.SMS_RECEIVED");

        setContentView(R.layout.activity_main);
        toGallery = findViewById(R.id.b1); toGallery.setVisibility(View.INVISIBLE);
        toCustomers = findViewById(R.id.b2); toCustomers.setVisibility(View.INVISIBLE);
        toOrders = findViewById(R.id.b3); toOrders.setVisibility(View.INVISIBLE);
        toSms = findViewById(R.id.b4); toSms.setVisibility(View.INVISIBLE);
        toSketches = findViewById(R.id.b5); toSketches.setVisibility(View.INVISIBLE);
        sUp = AnimationUtils.loadAnimation(this, R.anim.scale_up);
        sDown = AnimationUtils.loadAnimation(this, R.anim.scale_down);
        slideDown = AnimationUtils.loadAnimation(this, R.anim.slide_down);
        slideUp = AnimationUtils.loadAnimation(this, R.anim.slide_up);
        musicSwitch = findViewById(R.id.musicSwitch);
        bgMusic = findViewById(R.id.bgMusic);
        musicSwitch.setMinAndMaxProgress(0f, 0.4f);
        musicSwitch.setSpeed(2.5f);
        musicSwitch.playAnimation();
        smsSwitch = findViewById(R.id.receiveSmsSwitch);
        sw = findViewById(R.id.switch_receiver);
        final Button[] buttons = {toGallery, toCustomers, toOrders, toSms, toSketches};
        initialAnimations(buttons);
        animationImplementation();

        /*
        sw.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton compoundButton, boolean b) {
                Toast.makeText(MainActivity.this, ""+b, Toast.LENGTH_SHORT).show();
                if (b) {
                    registerReceiver(smsIncomeReceiver, filter);
                } else {
                    unregisterReceiver(smsIncomeReceiver);
                }
            }
        }); */
        smsSwitch.setMinAndMaxProgress(0.0f, 0.5f);
        smsSwitch.setSpeed(3);
        smsSwitch.playAnimation();
        registerReceiver(smsIncomeReceiver, filter);
    }

    private void initialAnimations(@NonNull Button[] buttons) {
        Handler handler1 = new Handler();
        for (Button b: buttons){
            handler1.postDelayed(new Runnable() {
                @Override
                public void run() {
                    b.setVisibility(View.VISIBLE);
                    b.startAnimation(slideDown);
                }
            }, 500);
        }
    }

   public void onSwitchMusic(View view){
        if (isMusicSwitchOn) {
            stopMusicService();
            musicSwitch.setMinAndMaxProgress(0f, 0.5f);
            musicSwitch.setSpeed(2.5f);
            musicSwitch.playAnimation();
            isMusicSwitchOn = false;
        } else {
            startMusicService();
            musicSwitch.setMinAndMaxProgress(0.5f, 1f);
            musicSwitch.setSpeed(2.5f);
            musicSwitch.playAnimation();
            isMusicSwitchOn = true;
        }
    }
    public void onSwitchSmsReceiver(View view){
        if (isSmsSwitchOn) {
            unregisterReceiver(smsIncomeReceiver);
            smsSwitch.setMinAndMaxProgress(0.5f, 1.0f);
            smsSwitch.setSpeed(3);
            smsSwitch.playAnimation();
            isSmsSwitchOn = false;
        } else {
            registerReceiver(smsIncomeReceiver, filter);
            smsSwitch.setMinAndMaxProgress(0.0f, 0.5f);
            smsSwitch.setSpeed(3);
            smsSwitch.playAnimation();
            isSmsSwitchOn = true;
        }
    }

    private void startMusicService() {
        Intent musicIntent = new Intent(this, MusicService.class);
        startService(musicIntent);
    }

    private void animationImplementation() {
        musicSwitch.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View view, MotionEvent motionEvent) {
                if (motionEvent.getAction() == MotionEvent.ACTION_DOWN)
                    musicSwitch.startAnimation(sUp);
                else if (motionEvent.getAction() == MotionEvent.ACTION_UP)
                    musicSwitch.startAnimation(sDown);
                return false;
            }
        });
        toGallery.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View view, MotionEvent motionEvent) {
                if (motionEvent.getAction() == MotionEvent.ACTION_DOWN)
                    toGallery.startAnimation(sUp);
                else if (motionEvent.getAction() == MotionEvent.ACTION_UP)
                    toGallery.startAnimation(sDown);
                return false;
            }
        });
        toCustomers.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View view, MotionEvent motionEvent) {
                if (motionEvent.getAction() == MotionEvent.ACTION_DOWN)
                    toCustomers.startAnimation(sUp);
                else if (motionEvent.getAction() == MotionEvent.ACTION_UP)
                    toCustomers.startAnimation(sDown);
                return false;
            }
        });
        toOrders.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View view, MotionEvent motionEvent) {
                if (motionEvent.getAction() == MotionEvent.ACTION_DOWN)
                    toOrders.startAnimation(sUp);
                else if (motionEvent.getAction() == MotionEvent.ACTION_UP)
                    toOrders.startAnimation(sDown);
                return false;
            }
        });
        toSms.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View view, MotionEvent motionEvent) {
                if (motionEvent.getAction() == MotionEvent.ACTION_DOWN)
                    toSms.startAnimation(sUp);
                else if (motionEvent.getAction() == MotionEvent.ACTION_UP)
                    toSms.startAnimation(sDown);
                return false;
            }
        });
        toSketches.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View view, MotionEvent motionEvent) {
                if (motionEvent.getAction() == MotionEvent.ACTION_DOWN)
                    toSketches.startAnimation(sUp);
                else if (motionEvent.getAction() == MotionEvent.ACTION_UP)
                    toSketches.startAnimation(sDown);
                return false;
            }
        });
    }

    private void stopMusicService() {
        Intent musicIntent = new Intent(this, MusicService.class);
        stopService(musicIntent);
    }

    public void goToGallery(View view) {
        Intent goToGallery = new Intent(this , Gallery.class);
        startActivity(goToGallery);
    }

    public void goToCustomers(View view) {
        Intent goToCustomers = new Intent(this , Customers.class);
        startActivity(goToCustomers);
    }

    public void goToOrders(View view) {
        Intent goToOrders = new Intent(this , Orders.class);
        startActivity(goToOrders);
    }
    public void goToSMS(View view) {
        Intent goToSMS = new Intent(this , Messenger.class);
        startActivity(goToSMS);
    }
    public void goToSketches(View view){
        Intent goToSketches = new Intent(this, Sketches.class);
        startActivity(goToSketches);
    }
    @Override
    public void onDestroy(){
        super.onDestroy();
        stopMusicService();
    }
}