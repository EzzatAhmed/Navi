package com.example.navigationapp;

import android.Manifest;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Handler;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

import android.os.Bundle;
import android.view.View;
import android.view.animation.RotateAnimation;
import android.widget.ImageView;
import android.widget.Toast;

public class MainActivity extends AppCompatActivity implements View.OnClickListener {

    private static int SPLASH_TIME_OUT =5100;
    private ImageView Gotologin;


    Login l=new Login();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);




        Gotologin=findViewById(R.id.goToLogin);
        Gotologin.setOnClickListener(this);

        RotateAnimation rotateAnimation=new RotateAnimation(0,360,RotateAnimation.RELATIVE_TO_SELF,.5f,RotateAnimation.RELATIVE_TO_SELF,.5f);
        rotateAnimation.setDuration(5000);
        Gotologin.startAnimation(rotateAnimation);

        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {


                Intent inC=new Intent(MainActivity.this,Login.class);
                startActivity(inC);
                finish();
            }
        },SPLASH_TIME_OUT);

    }

    @Override
    public void onClick(View v) {

    }

    }

