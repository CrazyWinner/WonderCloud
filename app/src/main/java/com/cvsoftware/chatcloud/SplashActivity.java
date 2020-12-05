package com.cvsoftware.chatcloud;

import android.Manifest;
import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.view.ViewPropertyAnimator;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.ContextCompat;

import com.google.android.gms.ads.MobileAds;
import com.google.android.gms.ads.RequestConfiguration;
import com.google.android.gms.ads.initialization.InitializationStatus;
import com.google.android.gms.ads.initialization.OnInitializationCompleteListener;

import java.util.Arrays;
import java.util.List;


public class SplashActivity extends AppCompatActivity {
    int count = 0;
    //DO NOT OVERRIDE onCreate()!
    @Override
    protected void onCreate(Bundle savedInstance){
        super.onCreate(savedInstance);
        setContentView(R.layout.activity_splash);
        List<String> testDeviceIds = Arrays.asList("6C048AF26EC0E2F9F0BE5D7E92D39BA6","E2B4457F883BDD94F559F088F3B67774");
        RequestConfiguration configuration =
                new RequestConfiguration.Builder().setTestDeviceIds(testDeviceIds).build();
        MobileAds.setRequestConfiguration(configuration);
        MobileAds.initialize(this, new OnInitializationCompleteListener() {
            @Override
            public void onInitializationComplete(InitializationStatus initializationStatus) {
            }
        });
        final ImageView image = findViewById(R.id.image);
        final TextView text = findViewById(R.id.title);
        image.post(new Runnable() {
            @Override
            public void run() {
                image.setRotationX(180);
                ViewPropertyAnimator anim = findViewById(R.id.image).animate().rotationX(0).rotationXBy(180).setDuration(700);
                anim.setListener(new AnimatorListenerAdapter() {
                    @Override
                    public void onAnimationEnd(Animator animation) {
                        super.onAnimationEnd(animation);
                        animationDone();
                    }
                });
                anim.start();
                text.setScaleX(0.01f);
                text.setScaleY(0.01f);
                ViewPropertyAnimator anim2 = text.animate().scaleXBy(0.001f).scaleX(1f)
                        .scaleYBy(0.001f).scaleY(1).setDuration(700);
                anim2.setListener(new AnimatorListenerAdapter() {
                    @Override
                    public void onAnimationEnd(Animator animation) {
                        super.onAnimationEnd(animation);
                        animationDone();
                    }
                });
                anim2.start();
            }
        });



    }
    boolean paused = false;

    public synchronized void animationDone(){
        count++;
        if(count == 2){
            animationsFinished();
        }
    }

    @Override
    public void onPause(){
        paused=true;
        super.onPause();
    }
    @Override
    public void onResume(){
        super.onResume();
        if(paused){
        paused=false;
        animationsFinished();}
    }


    public void animationsFinished() {
        if(!paused) {
            SharedPreferences sharedPreferences
                    = getSharedPreferences("ChatCloudSharedPref",
                    MODE_PRIVATE);

            if (sharedPreferences.getBoolean("firstTime", true) || ContextCompat.checkSelfPermission(this, Manifest.permission.WRITE_EXTERNAL_STORAGE)
                    != PackageManager.PERMISSION_GRANTED) {
                Intent i = new Intent(this, IntroActivity.class);
                startActivity(i);
            } else {
                Intent i = new Intent(this, MainActivity.class);
                startActivity(i);
            }
            finish();
            //transit to another activity here
            //or do whatever you want
        }
    }
}