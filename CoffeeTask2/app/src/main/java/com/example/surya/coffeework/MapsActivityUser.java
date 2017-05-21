package com.example.surya.coffeework;

import android.Manifest;
import android.content.Context;
import android.content.pm.PackageManager;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.FragmentActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

import com.google.android.gms.maps.CameraUpdate;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

public class MapsActivityUser extends FragmentActivity implements OnMapReadyCallback, LocationListener {

    private GoogleMap mMap;
    private LocationManager locationManager;
    private Button userbtn;

    public DatabaseReference root = FirebaseDatabase.getInstance().getReference();
    public DatabaseReference locationUpdates = root.child("LocationUpdates");

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_maps_user);
        // Obtain the SupportMapFragment and get notified when the map is ready to be used.
        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager()
                .findFragmentById(R.id.map);
        mapFragment.getMapAsync(this);
        userbtn = (Button) findViewById(R.id.button);
        userbtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Toast.makeText(MapsActivityUser.this,"Request Successful",Toast.LENGTH_LONG).show();
                userbtn.setText("Driver Reached my location");
            }
        });

        locationUpdates.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                String distance = dataSnapshot.child("Distance").getValue(String.class);
                String duration = dataSnapshot.child("Duration").getValue(String.class);
                Toast.makeText(MapsActivityUser.this, distance + "\n" + duration, Toast.LENGTH_SHORT).show();
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });

    }
    @Override
    public void onMapReady(GoogleMap googleMap) {
        mMap = googleMap;

        locationManager = (LocationManager) getSystemService(Context.LOCATION_SERVICE);
        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            return;
        }
        locationManager.requestLocationUpdates(LocationManager.NETWORK_PROVIDER, 0, 0, MapsActivityUser.this);
        Location location = locationManager.getLastKnownLocation(LocationManager.NETWORK_PROVIDER);

        double latitude = location.getLatitude();
        double longitude = location.getLongitude();

        DatabaseReference root = FirebaseDatabase.getInstance().getReference();
        DatabaseReference mUserLocationUpdate = root.child("User").child("Location");

        mUserLocationUpdate.child("Latitude").setValue(latitude);
        mUserLocationUpdate.child("Longitude").setValue(longitude);

        LatLng userLatLng = new LatLng(location.getLatitude(),location.getLongitude());
        MarkerOptions markerOptions = new MarkerOptions().position(userLatLng).title("You are here!");
        mMap.addMarker(markerOptions);

        CameraUpdate userLocation = CameraUpdateFactory.newLatLngZoom(userLatLng , 20.0f);
        mMap.animateCamera(userLocation);
        }

    @Override
    public void onLocationChanged(Location location) {

    }

    @Override
    public void onStatusChanged(String provider, int status, Bundle extras) {

    }

    @Override
    public void onProviderEnabled(String provider) {

    }

    @Override
    public void onProviderDisabled(String provider) {

    }
}
