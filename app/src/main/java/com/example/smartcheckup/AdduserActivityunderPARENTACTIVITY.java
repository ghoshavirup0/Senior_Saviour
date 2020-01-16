package com.example.smartcheckup;

import android.content.Intent;
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

public class AdduserActivityunderPARENTACTIVITY extends AppCompatActivity {


    DatabaseReference mref;
    EditText usern,eusere,userp;
    String u,p;
    FirebaseAuth auth,a;
    int count=0;
    String childnames;
    static int childcount;
    static int done;
    String name;
    String useremail;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_adduser_activityunder_parentactivity);
        Intent i=getIntent();
        u=i.getStringExtra("user");
        p=i.getStringExtra("pass");
        usern=findViewById(R.id.user_name);
        eusere=findViewById(R.id.useremailid);
        userp=findViewById(R.id.user_pass);
        useremail=eusere.getText().toString();
        a=FirebaseAuth.getInstance();
        a.signInWithEmailAndPassword(u,p).addOnCompleteListener(new OnCompleteListener<AuthResult>() {
            @Override
            public void onComplete(@NonNull Task<AuthResult> task) {
                if(task.isSuccessful())
                {
                    final DatabaseReference childuser=FirebaseDatabase.getInstance().getReference().child("Parents").child(a.getUid()).child("NumberofChild");
                    childuser.addValueEventListener(new ValueEventListener() {
                        @Override
                        public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                            try {
                                String aa = dataSnapshot.getValue().toString();
                                childcount=Integer.parseInt(aa);
                            }catch (NullPointerException e)
                            {
                                childcount=0;
                            }

                        }

                        @Override
                        public void onCancelled(@NonNull DatabaseError databaseError) {

                        }
                    });
                }
            }
        });

    }




    public void adduser(View view) {
        done=1;

          auth=FirebaseAuth.getInstance();
        auth.signInWithEmailAndPassword(u,p)
                .addOnCompleteListener(new OnCompleteListener<AuthResult>() {
            @Override
            public void onComplete(@NonNull Task<AuthResult> task) {
                if(task.isSuccessful())
                {

                    mref= FirebaseDatabase.getInstance().getReference().child("Parents");

                    StoreparentdatatoFirebase user_object=new StoreparentdatatoFirebase(usern.getText().toString(),   //class file To Store VALUES
                            eusere.getText().toString(),userp.getText().toString(),4);

                             final FirebaseUser firebaseUser=auth.getCurrentUser();


                    if (firebaseUser != null) {
                        childcount++;
                        final String em = eusere.getText().toString().trim();
                        if (!em.contains("@")) {
                            Toast.makeText(AdduserActivityunderPARENTACTIVITY.this,"Not A Valid Email Address",Toast.LENGTH_SHORT).show();
                            eusere.setText("");
                            usern.setText("");
                            userp.setText("");
                        } else
                        {
                            mref.child(firebaseUser.getUid()).child("children").child(em.substring(0, em.indexOf("@"))).setValue(user_object)
                                    .addOnCompleteListener(new OnCompleteListener<Void>() {
                                        @Override
                                        public void onComplete(@NonNull Task<Void> task) {
                                            if (task.isSuccessful()) {

                                                mref.child(firebaseUser.getUid()).child("Children_names").addValueEventListener(new ValueEventListener() {
                                                    @Override
                                                    public void onDataChange(@NonNull DataSnapshot dataSnapshot) {

                                                        try {
                                                            name = dataSnapshot.getValue().toString();
                                                        }
                                                        catch (NullPointerException e)
                                                        {
                                                            name="";
                                                        }
                                                        if(done==1) {
                                                            name=name+" "+em.substring(0, em.indexOf("@"));

                                                            mref.child(firebaseUser.getUid()).child("Children_names").setValue(name);
                                                            done=0;
                                                        }
                                                    }

                                                    @Override
                                                    public void onCancelled(@NonNull DatabaseError databaseError) {

                                                    }
                                                });


                                                DatabaseReference mre = FirebaseDatabase.getInstance().getReference().child("Parents").child(firebaseUser.getUid()).child("NumberofChild");
                                                mre.setValue(childcount);
                                                usern.setText("");
                                                eusere.setText("");
                                                userp.setText("");
                                                Toast.makeText(AdduserActivityunderPARENTACTIVITY.this, "NEW USER ADDED", Toast.LENGTH_SHORT).show();


                                            } else {
                                                Toast.makeText(AdduserActivityunderPARENTACTIVITY.this, "FIREBASE FAILED DUE TO UNKNOWN REASONS", Toast.LENGTH_SHORT).show();
                                            }

                                        }
                                    });
                    }
                    }
                }
                else
                {
                    Toast.makeText(AdduserActivityunderPARENTACTIVITY.this,"FIREBASE FAILED",Toast.LENGTH_SHORT).show();

                }
            }
        });

    }

    public void cancel(View view) {
        finish();
    }

    public void showpassword(View view)     {
        count++;
        if(count%2==0)
        {
            userp.setTransformationMethod(PasswordTransformationMethod.getInstance());
        }
        else
            userp.setTransformationMethod(HideReturnsTransformationMethod.getInstance());

    }
}
