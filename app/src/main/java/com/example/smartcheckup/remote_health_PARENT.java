package com.example.smartcheckup;

import android.app.ProgressDialog;
import android.content.Intent;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.view.animation.AnimationUtils;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import static com.github.ybq.android.spinkit.animation.AnimationUtils.start;

public class remote_health_PARENT extends AppCompatActivity {

    String accountholder,uid;
    TextView t;
    ProgressDialog progressDoalog;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_remote_health__parent);

        Intent i=getIntent();
        uid=i.getStringExtra("uidd");
        accountholder=i.getStringExtra("id");



        t=findViewById(R.id.textView);
        t.setText("Heart Rate is shown here");

        FirebaseDatabase database=FirebaseDatabase.getInstance();
        DatabaseReference m=database.getReference().child("Parents").child(uid).child("children").child(accountholder).child("bpmrequest").child("value");
        m.setValue(1);

        collect();

    }


    @Override
    public void onBackPressed() {
        FirebaseDatabase database=FirebaseDatabase.getInstance();
        DatabaseReference m=database.getReference().child("Parents").child(uid).child("children").child(accountholder).child("bpmrequest").child("value");
        m.setValue(0);
        super.onBackPressed();
    }

    public void collect()
{
    t.setText("Fetching Data");

    progressDoalog = new ProgressDialog(remote_health_PARENT.this);
    progressDoalog.setMessage("Fetching Data...");
    progressDoalog.show();
    new Thread(new Runnable() {
        @Override
        public void run() {
            try{
                Thread.sleep(3000);
                DatabaseReference mref=FirebaseDatabase.getInstance().getReference().child("Parents").child(uid).child("children").child(accountholder).child("bpmrequest").child("value");
                mref.addValueEventListener(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                        float f= Float.parseFloat( dataSnapshot.getValue().toString());
                        if(f==1)
                            t.setText("DATA NOT FOUND");
                        else
                            t.setText(f+"BPM");
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError databaseError) {

                    }
                });

            }
            catch (Exception e)
            {
                t.setText("TECHNICAL ERROR");
            }
        }
    }).start();
    progressDoalog.cancel();
}

    public void openstats(View view) {

        Toast.makeText(remote_health_PARENT.this,"UNDER DEVELOPMENT",Toast.LENGTH_LONG).show();

    }

    public void refresh(View view) {
        collect();
    }
}
