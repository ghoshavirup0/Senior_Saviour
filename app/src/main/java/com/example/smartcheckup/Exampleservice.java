package com.example.smartcheckup;

import android.Manifest;
import android.annotation.SuppressLint;
import android.app.Notification;
import android.app.PendingIntent;
import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.location.Location;
import android.location.LocationManager;
import android.os.Bundle;
import android.os.IBinder;
import android.support.annotation.Nullable;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.NotificationCompat;
import android.util.Log;
import android.widget.Toast;

import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationServices;

import static com.example.smartcheckup.App.channelid;

@SuppressLint("Registered")
public class Exampleservice extends Service implements  GoogleApiClient.ConnectionCallbacks, GoogleApiClient.OnConnectionFailedListener, com.google.android.gms.location.LocationListener{

    public static final String TAG = "locationservice";
    public GoogleApiClient mGoogleApiClient;
    public Location mLocation;
    public LocationManager mLocationManager;
    public LocationRequest mLocationRequest;
    public com.google.android.gms.location.LocationListener listener;
    public long UPDATE_INTERVAL = 2 * 1000;  /* 20 secs */
    public long FASTEST_INTERVAL = 2000; /* 2 sec */
    String uid,user;


    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {

        uid=intent.getStringExtra("uid");
        user=intent.getStringExtra("user");

        Intent notificationintent=new Intent(Exampleservice.this,useractivity.class);

        Toast.makeText(Exampleservice.this,"UID: "+uid,Toast.LENGTH_SHORT).show();
        PendingIntent pendingIntent=PendingIntent.getActivity(Exampleservice.this,
                0,notificationintent,0);

        Notification notification=new NotificationCompat.Builder(Exampleservice.this,channelid)
                .setContentTitle("LOCATION SERVICE").setSmallIcon(R.drawable.location).setContentIntent(pendingIntent).build();



        startForeground(1,notification);



        return START_NOT_STICKY;

    }


    @Override
    public void onCreate() {

        super.onCreate();

        mGoogleApiClient = new GoogleApiClient.Builder(this)
                .addConnectionCallbacks(this)
                .addOnConnectionFailedListener(Exampleservice.this)
                .addApi(LocationServices.API)
                .build();

        mLocationManager = (LocationManager)this.getSystemService(Context.LOCATION_SERVICE);
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
    }

    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }




    //LOCATION STARTS


    @Override
    public void onConnected(Bundle bundle) {
        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {

            return;
        }

        startLocationUpdates();

        mLocation = LocationServices.FusedLocationApi.getLastLocation(mGoogleApiClient);

        if(mLocation == null){
            startLocationUpdates();
        }
        if (mLocation != null) {

            // mLatitudeTextView.setText(String.valueOf(mLocation.getLatitude()));
            //mLongitudeTextView.setText(String.valueOf(mLocation.getLongitude()));
        } else {
            Toast.makeText(this, "Location not Detected", Toast.LENGTH_SHORT).show();
        }
    }

    @Override
    public void onConnectionSuspended(int i) {
        Log.i(TAG, "Connection Suspended");
        mGoogleApiClient.connect();
    }

    @Override
    public void onConnectionFailed(ConnectionResult connectionResult) {
        Log.i(TAG, "Connection failed. Error: " + connectionResult.getErrorCode());
    }

    protected void startLocationUpdates() {
        // Create the location request
        mLocationRequest = LocationRequest.create()
                .setPriority(LocationRequest.PRIORITY_HIGH_ACCURACY)
                .setInterval(UPDATE_INTERVAL)
                .setFastestInterval(FASTEST_INTERVAL);
        // Request location updates
        if (ActivityCompat.checkSelfPermission(Exampleservice.this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(Exampleservice.this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {

            return;
        }
        LocationServices.FusedLocationApi.requestLocationUpdates(mGoogleApiClient,
                mLocationRequest, this);

        Log.d("reque", "--->>>>");
    }

    @Override
    public void onLocationChanged(Location location) {

        //String msg = "Updated Location: " + Double.toString(location.getLatitude()) + "," +Double.toString(location.getLongitude());

        // Toast.makeText(this, msg, Toast.LENGTH_SHORT).show();
        Log.v("Lat: ",String.valueOf(location.getLatitude()));
        Toast.makeText(Exampleservice.this,"LAT: "+location.getLatitude(),Toast.LENGTH_SHORT).show();
        //locationinfo.child("userloc_LATITUDE").setValue(String.valueOf(location.getLatitude()));
        // locationinfo.child("userloc_LONGITUDE").setValue(String.valueOf(location.getLongitude()));
        // You can now create a LatLng Object for use with maps
        //LatLng latLng = new LatLng(location.getLatitude(), location.getLongitude());


    }
}
