package com.example.smartcheckup;

import android.app.AlertDialog;
import android.app.Notification;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.app.ProgressDialog;
import android.app.TimePickerDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.media.RingtoneManager;
import android.net.Uri;
import android.os.Build;
import android.os.Handler;
import android.support.annotation.NonNull;
import android.support.annotation.RequiresApi;
import android.support.v4.app.DialogFragment;
import android.support.v4.app.NotificationCompat;
import android.support.v4.app.NotificationManagerCompat;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.style.AbsoluteSizeSpan;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.AbsoluteLayout;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.PopupMenu;
import android.widget.TextView;
import android.widget.TimePicker;
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

import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;
import java.util.StringTokenizer;

import static android.support.v4.app.NotificationCompat.DEFAULT_LIGHTS;
import static android.support.v4.app.NotificationCompat.DEFAULT_SOUND;
import static android.support.v4.app.NotificationCompat.DEFAULT_VIBRATE;
import static android.support.v4.app.NotificationCompat.FLAG_SHOW_LIGHTS;

public class parentactivity extends AppCompatActivity implements TimePickerDialog.OnTimeSetListener, ExampleDialouge.Exampledialougelistner,Sendsmstowatchdialog.SendsmstowatchdialogListner{

    Animation a1,a2,a3;
    LinearLayout l1,l2,l3,l4;
    FirebaseDatabase database = FirebaseDatabase.getInstance();
    DatabaseReference myRef = database.getReference();
    DatabaseReference mref;
    String user,pass;
    FirebaseAuth auth,a;
    int index=0;
    static PopupMenu menu;
    ProgressDialog pro;
    String name;
    FirebaseUser firebaseUser;
    static String accountholder;
    static int childcount;
    static String []list;
    int start=0;
    android.os.Handler customHandler;
    private boolean setwarning=false;
    private NotificationManagerCompat notificationManagerCompat;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_parentactivity);
        l1 = (LinearLayout) findViewById(R.id.l1);
        l2 = (LinearLayout) findViewById(R.id.l2);
        l3 = (LinearLayout) findViewById(R.id.adduser);
        l4 = (LinearLayout) findViewById(R.id.userchangeicon);
        a1 = AnimationUtils.loadAnimation(parentactivity.this, R.anim.onlyleftsidemove);
        a2 = AnimationUtils.loadAnimation(parentactivity.this, R.anim.onlyrightisdemove);
        a3 = AnimationUtils.loadAnimation(parentactivity.this, R.anim.downdown);
        l1.setAnimation(a1);
        l2.setAnimation(a2);
        l3.setAnimation(a3);
        l4.setAnimation(a2);
        Intent i = getIntent();

        user = i.getStringExtra("username");
        pass = i.getStringExtra("password");



        pro=new ProgressDialog(parentactivity.this);
        auth = FirebaseAuth.getInstance();
        auth.signInWithEmailAndPassword(user, pass);
        mref = FirebaseDatabase.getInstance().getReference().child("Parents");
        firebaseUser = auth.getCurrentUser();

        a = FirebaseAuth.getInstance();
        a.signInWithEmailAndPassword(user, pass).addOnCompleteListener(new OnCompleteListener<AuthResult>() {
            @Override
            public void onComplete(@NonNull Task<AuthResult> task) {   //CALCULATING NUMBER OF CHILDREN
                if (task.isSuccessful()) {
                    DatabaseReference childuser = FirebaseDatabase.getInstance().getReference().child("Parents");
                    final FirebaseUser firebaseUser = auth.getCurrentUser();
                    childuser.child(firebaseUser.getUid()).child("NumberofChild").
                            addValueEventListener(new ValueEventListener() {
                                @Override
                                public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                                    try {
                                        String aa = dataSnapshot.getValue().toString();
                                        childcount = Integer.parseInt(aa);
                                    } catch (NullPointerException e) {
                                        childcount = 0;
                                    }

                                }

                                @Override
                                public void onCancelled(@NonNull DatabaseError databaseError) {

                                }
                            });
                }
            }
        });


        pro.setMessage("Getting All Users...");
        pro.show();
       listitem();
       new Handler().postDelayed(new Runnable() {
                @Override
                public void run() {     //GETTING USERS READY

                    new Handler().postDelayed(new Runnable() {
                        @Override
                        public void run() {

                            if (start == 0) {
                                try {
                                    list = name.split(" ");
                                }
                                catch (NullPointerException e)
                                {
                                    listitem();
                                   // list = name.split(" ");
                                }
                                try {
                                    accountholder = list[0];
                                }catch (Exception e)
                                {
                                    listitem();
                                }
                                start = 1;
                                pro.cancel();
                                Toast.makeText(parentactivity.this, "Current User: " + accountholder, Toast.LENGTH_SHORT).show();
                                if (accountholder.isEmpty()) //FOR NEW ACCOUNTS TO ADD USERS
                                {
                                    pro.setMessage("You Need To Have Atleast One User");
                                    pro.show();
                                    new Handler().postDelayed(new Runnable() {
                                        @Override
                                        public void run() {
                                            Intent ii = new Intent(parentactivity.this, AdduserActivityunderPARENTACTIVITY.class);
                                            ii.putExtra("user", user);
                                            ii.putExtra("pass", pass);
                                            pro.cancel();
                                            startActivity(ii);
                                            finish();
                                        }
                                    }, 1500);

                                }

                                customHandler = new android.os.Handler();
                                customHandler.postDelayed(updateTimerThread, 0);


                        }

                        }
                    }, 1000);

                }
            }, 2500);


       //notificationManagerCompat = NotificationManagerCompat.from(parentactivity.this);
    }

    private Runnable updateTimerThread = new Runnable()    ///ALL TIME RUNNING
    {
        public void run()
        {
            DatabaseReference m=mref.child(firebaseUser.getUid()).child("children").child(accountholder);
            m.addListenerForSingleValueEvent(new ValueEventListener() {
                @RequiresApi(api = Build.VERSION_CODES.O)
                @Override
                public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                    try{
                        String st=dataSnapshot.child("Emergency_Status").child("Fall").getValue().toString();
                    if(st.equals("true")||st.equals("True")&&index==0)
                    {

                            NotificationManager mNotificationManager;

                            NotificationCompat.Builder mBuilder = new NotificationCompat.Builder(parentactivity.this, "notify_001");


                            NotificationCompat.BigTextStyle bigText = new NotificationCompat.BigTextStyle();
                            bigText.setBigContentTitle("EMERGENCY");

                            setwarning = true;
                            //customHandler.removeCallbacks(updateTimerThread);
                            //mBuilder.setContentIntent(pendingIntent);
                            mBuilder.setSmallIcon(R.drawable.emergency);
                            mBuilder.setContentTitle("EMERGENCY");
                            mBuilder.setContentText("User " + accountholder + " is in danger. Please open application");
                            mBuilder.setPriority(Notification.PRIORITY_MAX);
                            mBuilder.setStyle(bigText);
                            mBuilder.setDefaults(DEFAULT_VIBRATE);
                            mBuilder.setDefaults(DEFAULT_LIGHTS);
                            mBuilder.setDefaults(FLAG_SHOW_LIGHTS);
                            mBuilder.setDefaults(DEFAULT_SOUND);

                            mNotificationManager = (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);
                            String channelId = "Your_channel_id";
                            NotificationChannel notificationChannel = new NotificationChannel(channelId, "TITLE", NotificationManager.IMPORTANCE_HIGH);
                            mNotificationManager.createNotificationChannel(notificationChannel);
                            mBuilder.setChannelId(channelId);
                            mNotificationManager.notify(1, mBuilder.build());
                            index=1;

                    }
                        if(setwarning&&index==1)
                        {

                            try{
                                index++;
                             AlertDialog.Builder alert=new AlertDialog.Builder(parentactivity.this,R.style.TimePickerTheme);
                            alert.setMessage("CHECK USER STATUS IMMEDIATELY\nPress 'OK' multiple times to acknowledge").setPositiveButton("OK", new DialogInterface.OnClickListener() {
                                @Override
                                public void onClick(DialogInterface dialog, int which) {
                                    setwarning=false;
                                    dialog.cancel();
                                    index=0;
                                    DatabaseReference m=mref.child(firebaseUser.getUid()).child("children").child(accountholder).child("Emergency_Status").child("Fall");
                                    m.setValue("false");

                                }
                            }).create();
                            alert.show();}catch (Exception e){

                            }
                        }
                    }
                    catch (NullPointerException e)
                    {

                    }
                }

                @Override
                public void onCancelled(@NonNull DatabaseError databaseError) {

                }
            });
            customHandler.postDelayed(this, 5000);
        }
    };





    public void location(View view) {

        DatabaseReference m=mref.child(firebaseUser.getUid()).child("children").child(accountholder);
        m.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                try {
                    double a = Double.parseDouble(dataSnapshot.child("userloc_LONGITUDE").getValue().toString());
                    double b= Double.parseDouble(dataSnapshot.child("userloc_LATITUDE").getValue().toString());

                    Intent maps = new Intent(parentactivity.this, MapsActivity.class);
                    maps.putExtra("id", accountholder);
                    maps.putExtra("uidd",firebaseUser.getUid().toString());
                    maps.putExtra("lat",b);
                    maps.putExtra("lon",a);
                    startActivity(maps);

                }
                catch (Exception e)
                {
                    Toast.makeText(parentactivity.this,"NO DATA AVAILABLE",Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });




    }    //option

    public void message(View view) {
        /*Intent i=new Intent(parentactivity.this,sendsms.class);
        startActivity(i);*/

        Sendsmstowatchdialog sendsmstowatchdialog=new Sendsmstowatchdialog();
        sendsmstowatchdialog.show(getSupportFragmentManager(),"Example");
    }  //option

    public void health(View view) {

        //Toast.makeText(parentactivity.this,"UNDER DEVELOPMENT",Toast.LENGTH_SHORT).show()
        Intent i=new Intent(parentactivity.this,remote_health_PARENT.class);
        i.putExtra("id", accountholder);
        i.putExtra("uidd",firebaseUser.getUid().toString());
        startActivity(i);

    }  //option

    public void remainder(View view) {



        PopupMenu p=new PopupMenu(parentactivity.this,view );
        p.getMenuInflater().inflate(R.menu.remainderpickuppopup,p.getMenu());
        p.setOnMenuItemClickListener(new PopupMenu.OnMenuItemClickListener() {
            @Override
            public boolean onMenuItemClick(MenuItem item) {
                if(item.getTitle().toString().contains("Set New Remainder"))
                {
                    DialogFragment timepicker=new TimePickerFragment();
                    timepicker.show(getSupportFragmentManager(),"Select Time");
                }
                else if(item.getTitle().toString().contains("Show Remainders"))
                {
                    //Toast.makeText(useractivity.this,"The Alarm is set for: "+ msg.child("").getKey() +":"+msg2.child("").getKey(),Toast.LENGTH_SHORT).show();
                    DatabaseReference m=FirebaseDatabase.getInstance().getReference().child("Parents").child(firebaseUser.getUid()).child("children").child(accountholder).child("Parent_AlarmDetails");
                    m.addValueEventListener(new ValueEventListener() {
                        @Override
                        public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                            try {
                                int a = Integer.parseInt(dataSnapshot.child("parentalarmhour").getValue().toString());
                                int b = Integer.parseInt(dataSnapshot.child("parentalarmminute").getValue().toString());
                                if(a==200&&b==200)
                                {
                                    Toast.makeText(parentactivity.this, "No Alarm Preset", Toast.LENGTH_SHORT).show();
                                }
                                else {
                                    if (a < 10 && b > 9)
                                        Toast.makeText(parentactivity.this, "Your Alarm Is Set At: 0" + dataSnapshot.child("parentalarmhour").getValue().toString() + ":" + dataSnapshot.child("parentalarmminute").getValue().toString(), Toast.LENGTH_SHORT).show();
                                    else if (b < 10 && a > 9)
                                        Toast.makeText(parentactivity.this, "Your Alarm Is Set At: " + dataSnapshot.child("parentalarmhour").getValue().toString() + ":0" + dataSnapshot.child("parentalarmminute").getValue().toString(), Toast.LENGTH_SHORT).show();
                                    else if (a < 10 && b < 10)
                                        Toast.makeText(parentactivity.this, "Your Alarm Is Set At: 0" + dataSnapshot.child("parentalarmhour").getValue().toString() + ":0" + dataSnapshot.child("parentalarmminute").getValue().toString(), Toast.LENGTH_SHORT).show();
                                    else
                                        Toast.makeText(parentactivity.this, "Your Alarm Is Set At: " + dataSnapshot.child("parentalarmhour").getValue().toString() + ":" + dataSnapshot.child("parentalarmminute").getValue().toString(), Toast.LENGTH_SHORT).show();
                                }
                            }
                            catch (NullPointerException e)
                            {
                                Toast.makeText(parentactivity.this, "No Alarm Preset", Toast.LENGTH_SHORT).show();
                            }
                        }

                        @Override
                        public void onCancelled(@NonNull DatabaseError databaseError) {

                        }
                    });
                }
                else
                {
                    //parentstoreremainder user_object=new parentstoreremainder(0,0,"");
                    parentalarmtopic p=new parentalarmtopic("");
                    mref.child(firebaseUser.getUid()).child("children").child(accountholder).child("Parent_AlarmDetails").child("alarm topic").child("topic").setValue("");
                    mref.child(firebaseUser.getUid()).child("children").child(accountholder).child("Parent_AlarmDetails").child("parentalarmhour").setValue(200);
                    mref.child(firebaseUser.getUid()).child("children").child(accountholder).child("Parent_AlarmDetails").child("parentalarmminute").setValue(200);
                }
                return true;
            }
        });
        p.show();



    }  //option

    @Override
    public void onTimeSet(TimePicker view, final int hourOfDay, final int minute) {


        auth=FirebaseAuth.getInstance();
        auth.signInWithEmailAndPassword(user,pass)
                .addOnCompleteListener(new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        if(task.isSuccessful())
                        {
                            String s="AM";
                            if(hourOfDay>12)
                                s="PM";
                            mref= FirebaseDatabase.getInstance().getReference().child("Parents");
                            parentstoreremainder user_object=new parentstoreremainder(hourOfDay,minute,s);
                            FirebaseUser firebaseUser=auth.getCurrentUser();
                            if (firebaseUser != null) {
                                mref.child(firebaseUser.getUid()).child("children").child(accountholder).child("Parent_AlarmDetails").setValue(user_object)
                                        .addOnCompleteListener(new OnCompleteListener<Void>() {
                                            @Override
                                            public void onComplete(@NonNull Task<Void> task) {
                                                if(task.isSuccessful())
                                                {
                                                    //Toast.makeText(parentactivity.this,"TIME SET",Toast.LENGTH_SHORT).show();
                                                }
                                                else{
                                                    Toast.makeText(parentactivity.this,"FIREBASE FAILED DUE TO UNKNOWN REASONS",Toast.LENGTH_SHORT).show();
                                                }

                                            }
                                        });
                            }
                        }
                        else
                        {
                            Toast.makeText(parentactivity.this,"FIREBASE FAILED",Toast.LENGTH_SHORT).show();

                        }
                    }
                });

        openDialouge();
    }



    private void openDialouge() {
        ExampleDialouge exampleDialouge=new ExampleDialouge();
        exampleDialouge.show(getSupportFragmentManager(),"Example");
    }

    @Override
    public void applyText(String username) {

        parentalarmtopic p=new parentalarmtopic(username);
        mref.child(firebaseUser.getUid()).child("children").child(accountholder).child("Parent_AlarmDetails").child("alarm topic").setValue(p);
        Toast.makeText(parentactivity.this,"Remainder Set Succesfully",Toast.LENGTH_SHORT).show();
    }   //FOR SENDING ALARM REMAINDER TO WATCH DIALOGUE

    public void adduser(View view) {

        Intent i=new Intent(parentactivity.this,AdduserActivityunderPARENTACTIVITY.class);
        i.putExtra("user",user);
        i.putExtra("pass",pass);
        startActivity(i);
    }  //option

    @Override
    public void applysms(String username) {          //FOR SENDING SMS TO WATCH DIALOGUE
        if(username.equals(""))
        {
            mref.child(firebaseUser.getUid()).child("children").child(accountholder).child("PARENT_SMS").child("SMS").setValue("");
        }
        else {
            parentsendsmstowatch p = new parentsendsmstowatch(username);
            mref.child(firebaseUser.getUid()).child("children").child(accountholder).child("PARENT_SMS").setValue(p);
            Toast.makeText(parentactivity.this, "Message Sent Succesfully", Toast.LENGTH_SHORT).show();
        }
    } //Submitting SMS



public void listitem()
{
    DatabaseReference d=FirebaseDatabase.getInstance().getReference().child("Parents").child(firebaseUser.getUid()).child("Children_names");
    d.addValueEventListener(new ValueEventListener() {
        @Override
        public void onDataChange(@NonNull DataSnapshot dataSnapshot) {

            try{
                name=dataSnapshot.getValue().toString().trim();
            }
            catch (NullPointerException e)
            {
                listitem();
            }


        }

        @Override
        public void onCancelled(@NonNull DatabaseError databaseError) {

        }
    });

}


    @Override
    public void onBackPressed() {
        super.onPause();
    }

    public void logout(View view) {

        user=pass="";

        finish();
    }


public void changechild(View view) {


        menu=new PopupMenu(parentactivity.this,view);
        /*pro.setMessage("One Moment..");
        pro.show();
        listitem();

        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {
                pro.cancel();
                list=name.split(" ");
                for (String s : list) menu.getMenu().add(s);
                menu.show();
            }
        },300);
        */
    listitem();

    list=name.split(" ");
    for (String s : list) menu.getMenu().add(s);
    menu.show();
    menu.setOnMenuItemClickListener(new PopupMenu.OnMenuItemClickListener() {

        @Override
        public boolean onMenuItemClick(MenuItem item) {
            accountholder=item.toString();
            Toast.makeText(parentactivity.this,"Current User: "+accountholder,Toast.LENGTH_SHORT).show();
            return true;
        }
    });

    }  //changing user account


}
