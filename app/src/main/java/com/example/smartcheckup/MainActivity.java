package com.example.smartcheckup;

import android.Manifest;
import android.accessibilityservice.AccessibilityService;
import android.app.AlertDialog;
import android.app.Dialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.drawable.AnimationDrawable;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.provider.Settings;
import android.support.annotation.NonNull;
import android.support.constraint.ConstraintLayout;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.method.HideReturnsTransformationMethod;
import android.text.method.PasswordTransformationMethod;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.RadioButton;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

public class MainActivity extends AppCompatActivity {


    EditText user;
    EditText pass;
    EditText parentid;
    String usertype="user";
    FirebaseAuth auth;
    Animation a1,a2,a3;
    RadioButton u,p;
    LinearLayout l1,l2,l3,l4;
    DatabaseReference mref;
    String domain,aa;
    private ProgressDialog pro;
    String username;
    static int i=0;
    private int PERMISSION_CODE=1;
    int count=-2;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        ispermitted(MainActivity.this);
            if (!isConnected(MainActivity.this))
                buildDialog(MainActivity.this).show();  //CHECKS FOR INTERNET CONNECTION
            else {
                Toast.makeText(MainActivity.this, "Welcome", Toast.LENGTH_SHORT).show();
                setContentView(R.layout.activity_main);
                LinearLayout c = findViewById(R.id.layout);
                AnimationDrawable a = (AnimationDrawable) c.getBackground();
                a.setExitFadeDuration(2000);
                a.setEnterFadeDuration(2000);
                a.start();
                a1 = AnimationUtils.loadAnimation(MainActivity.this, R.anim.topdown);
                a2 = AnimationUtils.loadAnimation(MainActivity.this, R.anim.onlyrightisdemove);
                a3 = AnimationUtils.loadAnimation(MainActivity.this, R.anim.onlyleftsidemove);
                l1 = findViewById(R.id.edittextanim);
                l2 = findViewById(R.id.radioani);
                l3 = findViewById(R.id.buttonani);
                l4 = findViewById(R.id.parentidlayout);
                l1.setAnimation(a1);
                l2.setAnimation(a2);
                l3.setAnimation(a3);
                l4.setAnimation(a1);
                user = findViewById(R.id.user);
                pass = findViewById(R.id.pass);
                auth = FirebaseAuth.getInstance();  //IMPORTANT
                u = findViewById(R.id.userradio);
                p = findViewById(R.id.parentradio);
                parentid = findViewById(R.id.parentid);
                user.requestFocus();
                u.setChecked(false);
                p.setChecked(true);
                pro = new ProgressDialog(MainActivity.this);
            }
            //setContentView(R.layout.activity_main);


    }

    public void ispermitted(Context c)
    {
        if(ContextCompat.checkSelfPermission(MainActivity.this, Manifest.permission.ACCESS_FINE_LOCATION)== PackageManager.PERMISSION_GRANTED
        &&ContextCompat.checkSelfPermission(MainActivity.this, Manifest.permission.INTERNET)== PackageManager.PERMISSION_GRANTED&&
                ContextCompat.checkSelfPermission(MainActivity.this, Manifest.permission.ACCESS_COARSE_LOCATION)== PackageManager.PERMISSION_GRANTED)
        {
        }
        else
        {
             requestPermission();
        }
    }

    private void requestPermission() {

        if(ActivityCompat.shouldShowRequestPermissionRationale(this,Manifest.permission.ACCESS_FINE_LOCATION)&&ActivityCompat.shouldShowRequestPermissionRationale(this,Manifest.permission.ACCESS_COARSE_LOCATION)&&ActivityCompat.shouldShowRequestPermissionRationale(this,Manifest.permission.INTERNET))
        {
            new AlertDialog.Builder(this).setCancelable(false).setTitle("Permission Needed").setMessage("Internet and location permission is\nneeded to connect with database")
                    .setPositiveButton("OKAY", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {

                            ActivityCompat.requestPermissions(MainActivity.this,new String[]{Manifest.permission.ACCESS_FINE_LOCATION,Manifest.permission.ACCESS_FINE_LOCATION,Manifest.permission.INTERNET},PERMISSION_CODE);
                        }
                    }).setNegativeButton("CANCEL", new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int which) {
                    dialog.dismiss();
                }
            }).create().show();

        }
        else
        {
            ActivityCompat.requestPermissions(this,new String[]{Manifest.permission.ACCESS_FINE_LOCATION,Manifest.permission.ACCESS_FINE_LOCATION,Manifest.permission.INTERNET},PERMISSION_CODE);
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        if(requestCode==PERMISSION_CODE)
        {
            if(grantResults.length>0&&grantResults[0]==PackageManager.PERMISSION_GRANTED)
            {
                Toast.makeText(MainActivity.this,"PERMISSION GRANTED",Toast.LENGTH_SHORT).show();
            }
            else {
                Toast.makeText(MainActivity.this, "PERMISSION DENIED", Toast.LENGTH_SHORT).show();
            }
        }
    }

    public boolean isConnected(Context context) {

        ConnectivityManager cm = (ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo netinfo = cm.getActiveNetworkInfo();

        if (netinfo != null && netinfo.isConnectedOrConnecting()) {
            android.net.NetworkInfo wifi = cm.getNetworkInfo(ConnectivityManager.TYPE_WIFI);
            android.net.NetworkInfo mobile = cm.getNetworkInfo(ConnectivityManager.TYPE_MOBILE);

            if((mobile != null && mobile.isConnectedOrConnecting()) || (wifi != null && wifi.isConnectedOrConnecting())) return true;
            else return false;
        } else
            return false;
    }

    public android.app.AlertDialog.Builder buildDialog(Context c) {

        android.app.AlertDialog.Builder builder = new AlertDialog.Builder(c,R.style.TimePickerTheme);
        builder.setTitle("No Internet Connection");
        builder.setMessage("You need to have Mobile Data or wifi to access this. Press OK if done");
        builder.setCancelable(false);
        builder.setPositiveButton("Ok", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                if(!isConnected(MainActivity.this)) buildDialog(MainActivity.this).show();
                else {
                    Toast.makeText(MainActivity.this,"Welcome", Toast.LENGTH_SHORT).show();
                    reload();
                    //setContentView(R.layout.activity_main);
                }
            }
        }).setNegativeButton("EXIT", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                finish();
            }
        });

        return builder;
    }  //CHECKS FOR INTERNET CONNECTION

    public void reload() {
        Intent intent = getIntent();
        overridePendingTransition(0, 0);
        intent.addFlags(Intent.FLAG_ACTIVITY_NO_ANIMATION);
        finish();
        overridePendingTransition(0, 0);
        startActivity(intent);
    }  //RESTART THE MAINACTIVITY



    //OPTIONS IN MAINACTIVITY



    public void login(final View view) {
        i=0;
        pro.setMessage("Signing In");
        if(usertype.contains("parent"))    //parent login
        {
            if(user.getText().toString().trim().equals("")||pass.getText().toString().trim().equals(""))
            {
                Toast.makeText(MainActivity.this,"Fields Can Not Be Empty",Toast.LENGTH_SHORT).show();
            }
            else {
                pro.show();
                auth.signInWithEmailAndPassword(user.getText().toString(),pass.getText().toString())
                        .addOnCompleteListener(new OnCompleteListener<AuthResult>() {
                            @Override
                            public void onComplete(@NonNull Task<AuthResult> task) {
                                if(task.isSuccessful())
                                {
                                    Toast.makeText(MainActivity.this,"Signed In",Toast.LENGTH_SHORT).show();
                                    Intent i = new Intent(MainActivity.this, parentactivity.class);
                                    i.putExtra("username",user.getText().toString());
                                    i.putExtra("password",pass.getText().toString());
                                    pro.cancel();
                                    startActivity(i);
                                }
                                else
                                {
                                    pro.cancel();
                                    Toast.makeText(MainActivity.this,"Acount Not Found",Toast.LENGTH_SHORT).show();
                                    reset(view);
                                }
                            }
                        });

            }
        }


        else                         //user login
        {
            if(user.getText().toString().trim().equals("")||pass.getText().toString().trim().equals("")||parentid.getText().toString().equals(""))
            {
                Toast.makeText(MainActivity.this,"Fields Can Not Be Empty",Toast.LENGTH_SHORT).show();
            }  //field checking
            else
            {
                String email=parentid.getText().toString();
                if(!email.contains("@")) {
                    Toast.makeText(MainActivity.this, "Email ID INCORRECT", Toast.LENGTH_SHORT).show();
                    reset(view);
                }
                else {
                    pro.show();
                    domain = email.substring(0, email.indexOf("@"));
                    DatabaseReference myref=FirebaseDatabase.getInstance().getReference().child("ListOfParents").child(domain);
                    myref.addValueEventListener(new ValueEventListener() {
                        @Override
                        public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                            try {
                                 aa = dataSnapshot.getValue().toString();
                            }
                            catch (NullPointerException e)
                            {
                                aa="n";
                            }
                            if(aa.equals("n"))
                            {
                                Toast.makeText(MainActivity.this,"ACCOUNT NOT FOUND",Toast.LENGTH_SHORT).show();
                                pro.cancel();
                                reset(view);
                            }
                            else
                            {

                                DatabaseReference child=FirebaseDatabase.getInstance().getReference().child("Parents").child(aa).child("children");
                                child.addValueEventListener(new ValueEventListener() {
                                    @Override
                                    public void onDataChange(@NonNull DataSnapshot dataSnapshot) {

                                             username = user.getText().toString();
                                            username = username.substring(0, username.indexOf("@"));

                                            try {
                                                String checkchild = dataSnapshot.child(username).getValue().toString();
                                                DatabaseReference childuser = FirebaseDatabase.getInstance().getReference().child("Parents").child(aa).child("children").child(username);
                                                childuser.addValueEventListener(new ValueEventListener() {
                                                    @Override
                                                    public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                                                        String childname = dataSnapshot.child("childname").getValue().toString();
                                                        String childemail = dataSnapshot.child("childemail").getValue().toString();
                                                        String childpass = dataSnapshot.child("childpass").getValue().toString();
                                                        if (childemail.equals(user.getText().toString()) && childpass.equals(pass.getText().toString())) {
                                                            if(i==0) {
                                                                Toast.makeText(MainActivity.this, "WELCOME " + childname.toUpperCase(), Toast.LENGTH_SHORT).show();
                                                                // NEW ACTIVITY STARTS
                                                                i = 1;
                                                                Intent i = new Intent(MainActivity.this, useractivity.class);
                                                                i.putExtra("uid", aa);
                                                                i.putExtra("child", username);
                                                                pro.cancel();
                                                                startActivity(i);
                                                            }

                                                        } else if (childemail.equals(user.getText().toString())&& !childpass.equals(pass.getText().toString())) {

                                                            pro.cancel();
                                                                pass.setError("Wrong Password Entered");
                                                            reset(view);
                                                        }
                                                        else {
                                                            pro.cancel();
                                                            Toast.makeText(MainActivity.this, "NO USER FOUND", Toast.LENGTH_SHORT).show();
                                                            reset(view);
                                                        }
                                                    }

                                                    @Override
                                                    public void onCancelled(@NonNull DatabaseError databaseError) {

                                                    }
                                                });

                                            }catch (NullPointerException e) {

                                                pro.cancel();
                                                Toast.makeText(MainActivity.this, "ACCOUNT NOT FOUND", Toast.LENGTH_LONG).show();
                                                reset(view);
                                            }

                                    }

                                    @Override
                                    public void onCancelled(@NonNull DatabaseError databaseError) {

                                    }
                                });

                            }

                            }

                        @Override
                        public void onCancelled(@NonNull DatabaseError databaseError) {

                        }
                    });

                }
            }
        }

    }


    public void userchoosed(View view) {
        usertype="user";
        parentid.setVisibility(View.VISIBLE);
        u.setChecked(false);
        p.setChecked(true);
        user.setText("");
        pass.setText("");
    }

    public void parentchoosed(View view) {
        usertype="parent";
        parentid.setVisibility(View.INVISIBLE);
        u.setChecked(true);
        p.setChecked(false);
        user.setText("");
        pass.setText("");
    }

    public void reset(View view) {

        user.setText(null);
        pass.setText(null);
        parentid.setText(null);
    }

    public void signup(View view) {
        Intent i=new Intent(MainActivity.this,signup.class);
        startActivity(i);

    }

    public void showdata(View view) {
        count++;
        if(count%2==0)
        {
            pass.setTransformationMethod(PasswordTransformationMethod.getInstance());
        }
        else
            pass.setTransformationMethod(HideReturnsTransformationMethod.getInstance());

    }
}
