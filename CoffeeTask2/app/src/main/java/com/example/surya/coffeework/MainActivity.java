package com.example.surya.coffeetask;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.Toast;

import com.google.android.gms.maps.model.LatLng;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.ValueEventListener;

public class MainActivity extends AppCompatActivity {

    public Button userBtn, driverBtn;
    public EditText editText;
    public Button button;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        editText = (EditText) findViewById(R.id.edit_text);
        userBtn = (Button) findViewById(R.id.user_btn);
        driverBtn = (Button) findViewById(R.id.driver_btn);

        userBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                if(editText.getText().toString().length() > 0 ){

                    String userName = editText.getText().toString();
                DatabaseHelper.root.child("Users").child("UserName").setValue(userName);
                Intent intent = new Intent(MainActivity.this, MapsActivityUser.class);
                    intent.putExtra("user name", userName);
                startActivity(intent);

            }
            else
            {
                Toast.makeText(MainActivity.this , "Enter your name!" , Toast.LENGTH_LONG).show();
            }
            }
        });

        driverBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                if(editText.getText().toString().length() > 0 ){

                    String driverName = editText.getText().toString();
                    DatabaseHelper.root.child("Drivers").child("DriverName").setValue(driverName);
                    final Intent intent = new Intent(MainActivity.this, MapsActivityDriver.class);
                    DatabaseReference trigger = DatabaseHelper.root.child("Users").child("LatLng");
                    trigger.addListenerForSingleValueEvent(new ValueEventListener() {
                        @Override
                        public void onDataChange(DataSnapshot dataSnapshot) {
                            String data = dataSnapshot.getValue(String.class);
                      Toast.makeText(MainActivity.this, data ,Toast.LENGTH_LONG  ).show();
                            intent.putExtra("data" , data);
                            startActivity(intent);
                        }

                        @Override
                        public void onCancelled(DatabaseError databaseError) {

                        }
                    });

                }
                else
                {
                    Toast.makeText(MainActivity.this , "Enter your name!" , Toast.LENGTH_LONG).show();
                }
            }
        });

    }
}
