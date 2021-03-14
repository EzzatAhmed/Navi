package com.example.navigationapp;

import android.Manifest;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.Context;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.widget.CheckBox;
import android.widget.EditText;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

import android.view.View;
import android.widget.Button;
import android.widget.CompoundButton;
import android.widget.TextView;
import android.widget.Toast;
import android.text.Editable;
import android.text.TextWatcher;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.util.Objects;

public class Login extends AppCompatActivity implements TextWatcher,
        CompoundButton.OnCheckedChangeListener {

    private TextView Reg;
    private EditText num, pass;
    private Button login;
    private FirebaseAuth mAuth;
    private CheckBox rememb;
    private DatabaseReference myref;
    private FirebaseDatabase mFirebaseDatabase;
    private String userID;
    private FirebaseAuth.AuthStateListener mAuthListner;

    SharedPreferences sharedPreferences;
    SharedPreferences.Editor editor;
    private static final String PREF_NAME = "prefs";
    private static final String KEY_REMEMBER = "remember";
    private static final String KEY_USERNAME = "username";
    private static final String KEY_PASS = "password";
    private int STORAGE_PERMISSION_CODE=1;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);
        Reg = findViewById(R.id.Reg);
        Reg.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                open_reg();
            }
        });

        mAuth = FirebaseAuth.getInstance();
        num = findViewById(R.id.phnumer);
        pass = findViewById(R.id.password);
        login = findViewById(R.id.login);
        rememb = findViewById(R.id.remember);

        sharedPreferences = getSharedPreferences(PREF_NAME, Context.MODE_PRIVATE);
        editor = sharedPreferences.edit();

        if (sharedPreferences.getBoolean(KEY_REMEMBER, false))
            rememb.setChecked(true);
        else
            rememb.setChecked(false);

        num.setText(sharedPreferences.getString(KEY_USERNAME, ""));
        pass.setText(sharedPreferences.getString(KEY_PASS, ""));

        num.addTextChangedListener(this);
        pass.addTextChangedListener(this);
        rememb.setOnCheckedChangeListener(this);

        login.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(ContextCompat.checkSelfPermission(Login.this, Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED)
                {
                    /*Toast.makeText(Login.this,"You have already granted this permission!",Toast.LENGTH_SHORT).show();*/
                }
                else
                {
                    requestStoragePermission();
                }

                final String number = num.getText().toString() + "@gmail.com";
                final String password = pass.getText().toString();
                if (number.isEmpty() || password.isEmpty()) {
                    Toast.makeText(Login.this, "Please Verify All Fields", Toast.LENGTH_SHORT).show();
                    login.setVisibility(View.VISIBLE);
                } else {
                    Login(number, password);

                }
            }
        });
    }

    private void Login(final String num, String pass) {
        mAuth.signInWithEmailAndPassword(num, pass).addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
            @Override
            public void onComplete(@NonNull Task<AuthResult> task) {
                if (task.isSuccessful()) {
                    Toast.makeText(Login.this, "Account Signed In", Toast.LENGTH_SHORT).show();
                    open_map();
                } else {
                    Toast.makeText(Login.this, "Account Signin failed" + Objects.requireNonNull(task.getException()).getMessage(), Toast.LENGTH_SHORT).show();
                    login.setVisibility(View.VISIBLE);
                }
            }
        });


    }

    private void open_reg() {
        Intent reg = new Intent(Login.this, Registeration.class);
        startActivity(reg);
    }

    private void open_map() {
        Intent login = new Intent(Login.this, MapsActivity.class);
        startActivity(login);

    }

    @Override
    public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

    }

    @Override
    public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {
        managePrefs();
    }

    @Override
    public void afterTextChanged(Editable editable) {

    }

    @Override
    public void onCheckedChanged(CompoundButton compoundButton, boolean b) {
        managePrefs();
    }

    private void managePrefs() {
        if (rememb.isChecked()) {
            editor.putString(KEY_USERNAME, num.getText().toString().trim());
            editor.putString(KEY_PASS, pass.getText().toString().trim());
            editor.putBoolean(KEY_REMEMBER, true);
            editor.apply();
        } else {
            editor.putBoolean(KEY_REMEMBER, false);
            editor.remove(KEY_PASS);//editor.putString(KEY_PASS,"");
            editor.remove(KEY_USERNAME);//editor.putString(KEY_USERNAME, "");
            editor.apply();
        }
    }
    public void requestStoragePermission()
    {
        if (ActivityCompat.shouldShowRequestPermissionRationale(this, Manifest.permission.ACCESS_FINE_LOCATION))
        {
            new AlertDialog.Builder(this).setTitle("Permission needed").setMessage("This permission is need because of and that").setPositiveButton("ok", new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialogInterface, int i) {

                }
            }).setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialogInterface, int i) {
                    dialogInterface.dismiss();
                }
            }).create().show();

        }
        else
        {
            ActivityCompat.requestPermissions(this,new String[]{Manifest.permission.ACCESS_FINE_LOCATION},STORAGE_PERMISSION_CODE);
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        if(requestCode==STORAGE_PERMISSION_CODE)
        {
            if(grantResults.length>0&&grantResults[0]== PackageManager.PERMISSION_GRANTED)
            {
                Toast.makeText(this,"Permission Granted",Toast.LENGTH_SHORT).show();
            }
            else
            {
                Toast.makeText(this,"Permission Denied ",Toast.LENGTH_SHORT).show();
            }
        }
    }
}

