package com.example.surya.coffeework;

import android.content.Intent;
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

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        BtnSubmit = (Button) findViewById(R.id.btn_submit);
        radioGroup = (RadioGroup) findViewById(R.id.radio_group);
        EtName = (EditText) findViewById(R.id.et_name);

        DatabaseReference userNode  = FirebaseDatabase.getInstance().getReference().getRoot().child("User");
        userNode.setValue(null);

        DatabaseReference driverNode  = FirebaseDatabase.getInstance().getReference().getRoot().child("Driver");
        driverNode.setValue(null);


        BtnSubmit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                int selectedId = radioGroup.getCheckedRadioButtonId();
                String name = EtName.getText().toString();
                if((name.length() > 0)  && (selectedId > 0))
                {
                    RadioButton radioButton = (RadioButton) findViewById(selectedId);
                    String choice = radioButton.getText().toString();

                    if(choice.equals("User")) {
                        DatabaseReference root = FirebaseDatabase.getInstance().getReference();
                        DatabaseReference mUsers = root.child("User");
                        mUsers.child("Name").setValue(name);
                        Intent intent = new Intent(MainActivity.this , MapsActivityUser.class);
                        startActivity(intent);
                    }

                    if(choice.equals("Driver")){
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
        });
    }
}
