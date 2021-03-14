package com.example.navigationapp;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

import android.Manifest;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Bundle;
import android.util.Patterns;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;


import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;


import java.util.HashMap;
import java.util.Map;
import java.util.Objects;


public class Registeration extends AppCompatActivity {

    private Button regi;
    private EditText fn,ln,mail,pass,confpass;
    EditText number;
    private FirebaseAuth mAuth;
    private FirebaseDatabase database;
    private int STORAGE_PERMISSION_CODE=1;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_registeration);

        fn = findViewById(R.id.firstName);
        ln = findViewById(R.id.secondName);
        mail = findViewById(R.id.mail);
        pass = findViewById(R.id.pass);
        number = findViewById(R.id.number);
        confpass = findViewById(R.id.confpass);

        mAuth = FirebaseAuth.getInstance();
        regi = findViewById(R.id.Signupbtn);

        regi.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(ContextCompat.checkSelfPermission(Registeration.this, Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED)
                {
                    Toast.makeText(Registeration.this,"You have already granted this permission!",Toast.LENGTH_SHORT).show();
                }
                else
                {
                    requestStoragePermission();
                }


                 String fname = fn.getText().toString();
                 String lname = ln.getText().toString();
                 String email = mail.getText().toString();
                 String numb = number.getText().toString();

                 String password = pass.getText().toString();
                 String conpassword = confpass.getText().toString();


                mAuth.fetchSignInMethodsForEmail(email);
                if (fname.isEmpty() || lname.isEmpty() || numb.isEmpty() || email.isEmpty() || password.isEmpty() || conpassword.isEmpty())
                {
                    Toast.makeText(Registeration.this, "Please Verify All Fields", Toast.LENGTH_SHORT).show();
                    return;
                }
                if(!Patterns.EMAIL_ADDRESS.matcher(email).matches())
                {
                    Toast.makeText(Registeration.this,"Email Incorrect",Toast.LENGTH_LONG).show();
                    return;

                }

                if(numb.length()<10 || numb.length()>=16)
                {
                    Toast.makeText(Registeration.this,"Number Incorrect",Toast.LENGTH_LONG).show();
                    return;
                }
                if(password.length()<6){
                    Toast.makeText(Registeration.this,"Password too short",Toast.LENGTH_LONG).show();
                    return;
                }

                if(password.equals(conpassword)) {
                    CreateUserAccount(fname, lname, numb+"@gmail.com", email, password);
                }else
                {
                    Toast.makeText(Registeration.this,"Wrong Confirm Password",Toast.LENGTH_LONG).show();
                    return;
                }
            }
        });

    }
    private void CreateUserAccount(final String fn, final String ln, final String num, final String mail,final String pass)
    {
        mAuth.createUserWithEmailAndPassword(num,pass).addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
            @Override
            public void onComplete(@NonNull Task<AuthResult> task) {
                if(task.isSuccessful())
                {
                    String userid=mAuth.getCurrentUser().getUid();
                    String mop=number.getText().toString();
                    Toast.makeText(Registeration.this,"Account Created",Toast.LENGTH_SHORT).show();
                    DatabaseReference current_User_db=FirebaseDatabase.getInstance().getReference().child("Users").child(userid);
                    Map newPost=new HashMap();
                    newPost.put("Name",fn + ln);
                    newPost.put("Number",mop);
                    newPost.put("Email",mail);
                    newPost.put("Password",pass);

                    current_User_db.setValue(newPost);

                    open_verify();
                }
                else
                {
                    Toast.makeText(Registeration.this,"Account Creation failed"+ Objects.requireNonNull(task.getException()).getMessage(),Toast.LENGTH_SHORT).show();
                }

            }
        });

    }
    private void open_verify()
    {
        Intent ver=new Intent(Registeration.this,VerifyMobile.class);

        final String numb =number.getText().toString();
        validNo(numb);
        ver.putExtra("mobile",numb);
        startActivity(ver);
    }


    private void open_map()
    {
        Intent reg=new Intent(Registeration.this,MapsActivity.class);
        startActivity(reg);
    }
    private void validNo(String no){
        if(no.isEmpty() || no.length() < 10){
            number.setError("Enter a valid mobile");
            number.requestFocus();
            return;
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
