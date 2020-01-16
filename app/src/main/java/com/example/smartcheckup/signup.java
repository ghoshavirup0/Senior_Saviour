package com.example.smartcheckup;

import android.app.ProgressDialog;
import android.graphics.drawable.AnimationDrawable;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.method.HideReturnsTransformationMethod;
import android.text.method.PasswordTransformationMethod;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

public class signup extends AppCompatActivity {

    LinearLayout l;
    EditText u,p;
    int count=0;
    private ProgressDialog progressBar;
    private FirebaseAuth auth;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_signup);

        RelativeLayout c = findViewById(R.id.layout);
        AnimationDrawable a = (AnimationDrawable) c.getBackground();
        a.setExitFadeDuration(2000);
        a.setEnterFadeDuration(2000);
        a.start();
        l=findViewById(R.id.buttonlaysignup);
        u=findViewById(R.id.usersignup);
        p=findViewById(R.id.passwordsignuo);
        Animation a1= AnimationUtils.loadAnimation(signup.this,R.anim.onlyrightisdemove);
        Animation a2= AnimationUtils.loadAnimation(signup.this,R.anim.onlyleftsidemove);
        Animation a3= AnimationUtils.loadAnimation(signup.this,R.anim.topdown);
        u.setAnimation(a1);
        p.setAnimation(a2);
        l.setAnimation(a3);
        progressBar=new ProgressDialog(signup.this);
    }

    public void showpass(View view) {

        count++;
        if(count%2==0)
        {
            p.setTransformationMethod(PasswordTransformationMethod.getInstance());
        }
        else
        p.setTransformationMethod(HideReturnsTransformationMethod.getInstance());

    }

    public void Submit(View view) {
        final String email=u.getText().toString().trim();
        String pass=p.getText().toString().trim();
        if(email.isEmpty())
        {
            u.setError("CAN'T BE BLANK");
            Toast.makeText(signup.this,"Enter A Valid Email ID",Toast.LENGTH_SHORT).show();
        }
        else if(pass.isEmpty())
        {
            p.setError("CAN'T BE BLANK");
            Toast.makeText(signup.this,"Enter A Password",Toast.LENGTH_SHORT).show();
        }
        else
        {
            if(p.getText().toString().length()<6)
            {
                p.setError("PASSWORD NOT ALLOWED");
                Toast.makeText(signup.this, "Password Length Should be more than 6 Characters", Toast.LENGTH_SHORT).show();
            }
            else {
                progressBar.setMessage("Registering Account");
                progressBar.show();
                auth = FirebaseAuth.getInstance();
                auth.createUserWithEmailAndPassword(email, pass).addOnCompleteListener(new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        if (task.isSuccessful()) {
                            progressBar.cancel();
                            Toast.makeText(signup.this, "Account Registered", Toast.LENGTH_SHORT).show();
                            FirebaseDatabase database = FirebaseDatabase.getInstance();
                            DatabaseReference myRef = database.getReference("ListOfParents");
                            FirebaseDatabase database2 = FirebaseDatabase.getInstance();
                            DatabaseReference mRef = database2.getReference("ListOfParents").child(email.substring(0, email.indexOf("@")));
                            mRef.setValue(auth.getUid());
                            FirebaseUser f=auth.getCurrentUser();
                            DatabaseReference m=FirebaseDatabase.getInstance().getReference().child("Parents").child(f.getUid()).child("Children_names");
                            m.setValue(" ");
                        } else {
                            progressBar.cancel();
                            Toast.makeText(signup.this, "Something Went Wrong", Toast.LENGTH_SHORT).show();
                        }
                    }
                });
            }
        }

    }

    public void cancel(View view) {
        finish();
    }
}
