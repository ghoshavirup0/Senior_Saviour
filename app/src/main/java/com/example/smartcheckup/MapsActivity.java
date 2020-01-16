package com.example.smartcheckup;

import android.content.Intent;
import android.location.Address;
import android.location.Geocoder;
import android.support.annotation.NonNull;
import android.support.v4.app.FragmentActivity;
import android.os.Bundle;
import android.widget.Toast;

import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.io.IOException;
import java.security.spec.ECField;
import java.util.List;
import java.util.Locale;

public class MapsActivity extends FragmentActivity implements OnMapReadyCallback {

    private GoogleMap mMap;
    double lon,lat;
    int s=0;
    String add;
    Address obj;
    String accountholder,uid;
    double a,b;
    List<Address> addresses;
    Geocoder geocoder;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_maps);
        // Obtain the SupportMapFragment and get notified when the map is ready to be used.
        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager()
                .findFragmentById(R.id.map);
        mapFragment.getMapAsync(this);

        Intent i=getIntent();
        uid=i.getStringExtra("uidd");
        accountholder=i.getStringExtra("id");
        a=i.getDoubleExtra("lon",120);
        b=i.getDoubleExtra("lat",120);
    }


    @Override
    public void onMapReady(GoogleMap googleMap) {
        mMap = googleMap;

        /* geocoder=new Geocoder(getApplicationContext(), Locale.getDefault());
        try{
             addresses = geocoder.getFromLocation(b, a,1);
            obj = addresses.get(0);
            add = obj.getAddressLine(0);
            add = add + "\n" + obj.getPostalCode();
            add = add + "\n" + obj.getSubAdminArea();
            add = add + "\n" + obj.getLocality();
            add = add + "\n" + obj.getSubThoroughfare();
        }
        catch (Exception e)
        {
            e.printStackTrace();
            Toast.makeText(MapsActivity.this, e.getMessage(), Toast.LENGTH_SHORT).show();
        }
*/
        // Add a marker in Sydney and move the camera

       final DatabaseReference m= FirebaseDatabase.getInstance().getReference().child("Parents").child(uid).child("children").child(accountholder);
        m.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                double a = Double.parseDouble(dataSnapshot.child("userloc_LONGITUDE").getValue().toString());
                double b = Double.parseDouble(dataSnapshot.child("userloc_LATITUDE").getValue().toString());
                Geocoder geocoder=new Geocoder(MapsActivity.this, Locale.getDefault());
                m.removeEventListener(this);

                try{
                    addresses = geocoder.getFromLocation(b, a,1);
                    obj = addresses.get(0);

                    add = obj.getAddressLine(0);
                    add = add + "\n" + obj.getPostalCode();
                    add = add + "\n" + obj.getSubAdminArea();
                    add = add + "\n" + obj.getLocality();
                    add = add + "\n" + obj.getSubThoroughfare();
                }
                catch (Exception e)
                {
                    e.printStackTrace();
                    Toast.makeText(MapsActivity.this, e.getMessage()+"\nNO DATA AVAILABLE", Toast.LENGTH_SHORT).show();
                }


                LatLng sydney = new LatLng(b,a);
                mMap.addMarker(new MarkerOptions().position(sydney).title("Marker in "+obj.getLocality()));

                mMap.animateCamera(CameraUpdateFactory.newLatLngZoom(sydney, 16.8f));
                Toast.makeText(MapsActivity.this,"CURRENT POSITION:\n"+add,Toast.LENGTH_LONG).show();
                //mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(sydney, 16.8f));
                //Toast.makeText(MapsActivity.this,"CURRENT POSITION:\n"+add,Toast.LENGTH_LONG).show();
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
                finish();
            }
        });

        /*
        m.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {

                    double a = Double.parseDouble(dataSnapshot.child("userloc_LONGITUDE").getValue().toString());
                    double b = Double.parseDouble(dataSnapshot.child("userloc_LATITUDE").getValue().toString());

                    Geocoder geocoder=new Geocoder(MapsActivity.this, Locale.getDefault());

                    try{
                         addresses = geocoder.getFromLocation(b, a,1);
                        obj = addresses.get(0);

                        add = obj.getAddressLine(0);
                        add = add + "\n" + obj.getPostalCode();
                        add = add + "\n" + obj.getSubAdminArea();
                        add = add + "\n" + obj.getLocality();
                        add = add + "\n" + obj.getSubThoroughfare();
                    }
                    catch (Exception e)
                    {
                        e.printStackTrace();
                        Toast.makeText(MapsActivity.this, e.getMessage()+"\nNO DATA AVAILABLE", Toast.LENGTH_SHORT).show();
                    }


                    LatLng sydney = new LatLng(b,a);
                    mMap.addMarker(new MarkerOptions().position(sydney).title("Marker in "+obj.getLocality()));

                        mMap.animateCamera(CameraUpdateFactory.newLatLngZoom(sydney, 16.8f));
                        Toast.makeText(MapsActivity.this,"CURRENT POSITION:\n"+add,Toast.LENGTH_LONG).show();
                        //mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(sydney, 16.8f));
                    //Toast.makeText(MapsActivity.this,"CURRENT POSITION:\n"+add,Toast.LENGTH_LONG).show();

                }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
                finish();

            }
        });
*/

            /*LatLng sydney = new LatLng(b, a);
            mMap.addMarker(new MarkerOptions().position(sydney).title("Marker in " + obj.getLocality()));
            mMap.animateCamera(CameraUpdateFactory.newLatLngZoom(sydney, 16.8f));
            Toast.makeText(MapsActivity.this, "CURRENT POSITION:\n" + add, Toast.LENGTH_LONG).show();
*/


    }

    @Override
    public void onBackPressed() {
        finish();
    }

    @Override
    protected void onDestroy() {
        finish();
        super.onDestroy();
    }
}
