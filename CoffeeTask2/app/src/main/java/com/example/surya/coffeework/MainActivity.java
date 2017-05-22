package com.example.surya.coffeework;

import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.location.LocationManager;
import android.media.audiofx.BassBoost;
import android.provider.Settings;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.Toast;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

public class MainActivity extends AppCompatActivity {

    private Button BtnSubmit;
    private RadioGroup radioGroup;
    private EditText EtName;
    private int i =1;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        BtnSubmit = (Button) findViewById(R.id.btn_submit);
        radioGroup = (RadioGroup) findViewById(R.id.radio_group);
        EtName = (EditText) findViewById(R.id.et_name);



        BtnSubmit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                LocationManager locationManager = (LocationManager) getSystemService(Context.LOCATION_SERVICE);
                boolean gpsEnabled = false;
                boolean networkEnabled = false;

                try{
                    gpsEnabled = locationManager.isProviderEnabled(LocationManager.GPS_PROVIDER);
                }catch (Exception ex){}
                try{
                    networkEnabled = locationManager.isProviderEnabled(LocationManager.NETWORK_PROVIDER);
                }catch (Exception ex){}

                if(!gpsEnabled && !networkEnabled){
                    AlertDialog.Builder dialog = new AlertDialog.Builder(MainActivity.this);
                    dialog.setMessage("Your Location is off!");
                    dialog.setPositiveButton("Open Settings", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            Intent intent = new Intent(Settings.ACTION_LOCATION_SOURCE_SETTINGS);
                            MainActivity.this.startActivity(intent);
                        }
                    });

                    dialog.setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            dialog.dismiss();
                        }
                    });
                    dialog.show();

                }

            if(gpsEnabled && networkEnabled){

                int selectedId = radioGroup.getCheckedRadioButtonId();
                String name = EtName.getText().toString();
                if((name.length() > 0)  && (selectedId > 0))
                {
                    RadioButton radioButton = (RadioButton) findViewById(selectedId);
                    String choice = radioButton.getText().toString();

                    if(choice.equals("User")) {

                        DatabaseReference userNode  = FirebaseDatabase.getInstance().getReference().getRoot().child("User");
                        userNode.setValue(null);
                        DatabaseReference root = FirebaseDatabase.getInstance().getReference();
                        DatabaseReference mUsers = root.child("User");
                        mUsers.child("Name").setValue(name);
                        Intent intent = new Intent(MainActivity.this , MapsActivityUser.class);
                        startActivity(intent);
                    }

                    if(choice.equals("Driver")){
                        DatabaseReference driverNode  = FirebaseDatabase.getInstance().getReference().getRoot().child("Driver");
                        driverNode.setValue(null);

                        DatabaseReference root = FirebaseDatabase.getInstance().getReference();
                        DatabaseReference mDrivers = root.child("Driver");
                        mDrivers.child("Name").setValue(name);
                        DatabaseReference mUserLocationUpdate = root.child("User").child("Location");
                        mUserLocationUpdate.addValueEventListener(new ValueEventListener() {
                            @Override
                            public void onDataChange(DataSnapshot dataSnapshot) {
                                double latitude = (double) dataSnapshot.child("Latitude").getValue();
                                double longitude = (double) dataSnapshot.child("Longitude").getValue();
                                Intent intent = new Intent(MainActivity.this,MapsActivityDriver.class);
                                intent.putExtra("Latitude" , latitude);
                                intent.putExtra("Longitude" , longitude);
                                startActivity(intent);
                                }

                            @Override
                            public void onCancelled(DatabaseError databaseError) {

                            }
                        });
                    }
                }
                else
                {
                    Toast.makeText(MainActivity.this,"Provide the Credentials" , Toast.LENGTH_LONG).show();
                }
            }
            }
        });

    }
}
