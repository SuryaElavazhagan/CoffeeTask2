package com.example.surya.coffeework;

import com.google.android.gms.maps.model.LatLng;


public class Steps {
    public Distance distance;
    public Duration duration;
    public LatLng startLocation , endLocation;

    public Steps(){
        distance = null;
        duration = null;
        startLocation = null;
        endLocation = null;
    }
}
