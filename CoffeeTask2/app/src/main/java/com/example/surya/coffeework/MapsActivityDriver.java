package com.example.surya.coffeework;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Color;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.provider.ContactsContract;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.FragmentActivity;
import android.os.Bundle;
import android.widget.Toast;

import com.google.android.gms.maps.CameraUpdate;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.gms.maps.model.Polyline;
import com.google.android.gms.maps.model.PolylineOptions;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.io.UnsupportedEncodingException;
import java.util.ArrayList;
import java.util.List;

public class MapsActivityDriver extends FragmentActivity implements OnMapReadyCallback, LocationListener, DirectionFinderListener {

    private GoogleMap mMap;
    private String driverLocation,userLocation;
    public ProgressDialog progressDialog;
    private List<Marker> originMarkers = new ArrayList<>();
    private List<Marker> destinationMarkers = new ArrayList<>();
    private List<Polyline> polylinePaths = new ArrayList<>();
    public List<Steps> steps;
    public DatabaseReference root = FirebaseDatabase.getInstance().getReference();
    public DatabaseReference locationUpdates = root.child("LocationUpdates");

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_maps_driver);
        Intent intent = this.getIntent();

        double latitude = intent.getDoubleExtra("Latitude", 0);
        double longitude = intent.getDoubleExtra("Longitude", 0);

        userLocation = latitude + "," + longitude;
        Toast.makeText(this, latitude + "," + longitude, Toast.LENGTH_LONG).show();
        // Obtain the SupportMapFragment and get notified when the map is ready to be used.
        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager()
                .findFragmentById(R.id.map);
        mapFragment.getMapAsync(this);

    }
    @Override
    public void onMapReady(GoogleMap googleMap) {
        mMap = googleMap;
        LocationManager locationManager;
        locationManager = (LocationManager) getSystemService(Context.LOCATION_SERVICE);
        if (ActivityCompat.checkSelfPermission(this, android.Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(this, android.Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {

            return;
        }

        locationManager.requestLocationUpdates(LocationManager.NETWORK_PROVIDER,0,0,MapsActivityDriver.this);
        Location location = locationManager.getLastKnownLocation(LocationManager.NETWORK_PROVIDER);
        LatLng driverLatLng = new LatLng(location.getLatitude(),location.getLongitude());
        MarkerOptions markerOptions = new MarkerOptions().position(driverLatLng).title("You are here");

        mMap.addMarker(markerOptions);

        CameraUpdate cameraUpdate = CameraUpdateFactory.newLatLngZoom(driverLatLng,20.0f);
        mMap.animateCamera(cameraUpdate);

        driverLocation = driverLatLng.latitude + "," + driverLatLng.longitude;
        sendRequest();

    }

    private void sendRequest() {


        try {
            new DirectionFinder(this, driverLocation , userLocation).execute();
        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
        }

    }

    @Override
    public void onLocationChanged(Location location) {
            double distance = 0;
            double duration = 0;
            LatLng currentLocation = new LatLng(location.getLatitude(),location.getLongitude());
        for(int i=0;i < steps.size() ; i++){
            if(currentLocation == steps.get(i).startLocation)
            {
                for(int j = i  ; j< steps.size() ; j++ ){
                      distance += steps.get(j).distance.value;
                      duration += steps.get(j).duration.value;
                }

                distance = distance/1000;
                duration = duration/60;
                int x = (int)  duration;
                int y = (int) distance;
                if(x < 1){ x =1; }
                locationUpdates.child("Distance").setValue(y + " Kms");
                locationUpdates.child("Duration").setValue(x + " mins");

            }
        }

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

    @Override
    public void onDirectionFinderStart() {

        progressDialog = ProgressDialog.show(this, "Please wait.",
                "Finding direction..!", true);

        if (originMarkers != null) {
            for (Marker marker : originMarkers) {
                marker.remove();
            }
        }

        if (destinationMarkers != null) {
            for (Marker marker : destinationMarkers) {
                marker.remove();
            }
        }

        if (polylinePaths != null) {
            for (Polyline polyline:polylinePaths ) {
                polyline.remove();
            }
        }
    }

    @Override
    public void onDirectionFinderSuccess(List<Route> routes, List<Steps> steps) {

        progressDialog.dismiss();
        polylinePaths = new ArrayList<>();
        originMarkers = new ArrayList<>();
        destinationMarkers = new ArrayList<>();
        this.steps = steps;
        Toast.makeText(this, "Distance :" + routes.get(0).distance.text + "\nTime: " + routes.get(0).duration.text, Toast.LENGTH_SHORT).show();
        locationUpdates.child("Distance").setValue(routes.get(0).distance.text);
        locationUpdates.child("Duration").setValue(routes.get(0).duration.text);

        for (Route route : routes) {
            mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(route.startLocation, 20));

            originMarkers.add(mMap.addMarker(new MarkerOptions()
                    .title(route.startAddress)
                    .position(route.startLocation)));
            destinationMarkers.add(mMap.addMarker(new MarkerOptions()
                    .title(route.endAddress)
                    .position(route.endLocation)));

            PolylineOptions polylineOptions = new PolylineOptions().
                    geodesic(true).
                    color(Color.CYAN).
                    width(25);

            for (int i = 0; i < route.points.size(); i++)
                polylineOptions.add(route.points.get(i));

            polylinePaths.add(mMap.addPolyline(polylineOptions));

        }

    }
}
