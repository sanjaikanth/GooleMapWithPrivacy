package com.example.mapzfromscratch;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.text.TextUtils;
import android.view.View;
import android.widget.Toast;
import android.os.Bundle;

import android.Manifest;
import android.content.pm.PackageManager;
import android.graphics.Color;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.AsyncTask;
import android.os.Build;
import android.util.Log;
import android.widget.Toast;

import android.widget.EditText;
import android.view.View;
import android.view.KeyEvent;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.UUID;
import java.util.Iterator;
import java.util.List;
import java.util.ArrayList;

public class MainActivity2 extends AppCompatActivity {
    private DatabaseReference mFirebaseDatabase;
    private FirebaseDatabase mFirebaseInstance;
    private String userId;
    private static final String TAG = MainActivity2.class.getSimpleName();
    private EditText inputName, inputAddress;
    private boolean isPresent;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main2);

        inputName = (EditText) findViewById(R.id.inputName);
        inputAddress = (EditText) findViewById(R.id.inputAddress);

        mFirebaseInstance = FirebaseDatabase.getInstance();
        // Getting reference to 'users' node
        mFirebaseDatabase = mFirebaseInstance.getReference("users");
        // Storing app title to 'app_title' node
        mFirebaseInstance.getReference("app_title").setValue("Realtime Database");
       //  checkAndInsertUser();
      //  Log.e("IsPresent",  String.valueOf(isPresent));
        //createUser("Sanjai", "String address");
    }
    public void CancelClick(View view) {
        Intent intent = new Intent(getApplicationContext(), MainActivity.class);
        startActivity(intent);
    }
    public void SaveFriend(View view) {
        isUpdted=false;
       // createUser(name, address);
        checkAndInsertUser();

    }

    private void createUser(String name, String address) {
        // TODO
        // In real apps this userId should be fetched by using auth
        if (TextUtils.isEmpty(userId))
            userId = mFirebaseDatabase.push().getKey();
        User user = new User(name, address);
        //mFirebaseDatabase.child( userId ).setValue( user );
        String newGuid = java.util.UUID.randomUUID().toString();

        mFirebaseDatabase.child(newGuid).setValue(user);
        // mFirebaseDatabase.child( userId ).setValue( user );
        //mFirebaseDatabase.setValue(user);
        //addUserChangeListener();
    }
boolean isUpdted=false;
    private void checkAndInsertUser() {
        String name = inputName.getText().toString();
        String address = inputAddress.getText().toString();
        DatabaseReference ref = FirebaseDatabase.getInstance().getReference().child("users");
        ref.orderByChild("name").equalTo(name).addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                Iterator<DataSnapshot> dataSnapshots = dataSnapshot.getChildren().iterator();
                List<User> users = new ArrayList<>();
                User user=null ;
                String strGUID="";
                if (dataSnapshots.hasNext()) {
                    DataSnapshot dataSnapshotChild = dataSnapshots.next();
                    user = dataSnapshotChild.getValue(User.class);
                    // users.add(user);
                    isPresent= true;
                    strGUID=dataSnapshotChild.getKey() ;
                    Log.e("IsPresent Inside: ",  String.valueOf(isPresent));
                }
                else {
                    isPresent = false;
                    Log.e("IsPresent Inside: ",  String.valueOf(isPresent));
                }
                if(isPresent && !isUpdted)
                {
                  //  String strAddress = inputAddress.getText().toString();
                    user.address=address;
                    isUpdted=true;
                    mFirebaseDatabase.child(strGUID).setValue(user);

                }
                else if(!isUpdted) {
                    Toast.makeText(MainActivity2.this, "Clickerd", Toast.LENGTH_SHORT).show();
                    isUpdted=true;
                    createUser(name,address);
                }
                Intent intent = new Intent(getApplicationContext(), MainActivity.class);
                intent.putExtra("key",""+address);
                startActivity(intent);
                // for (DataSnapshot datas : dataSnapshot.getChildren()) {
                //    String keys = datas.getKey();
                //}
            }

            @Override
            public void onCancelled(DatabaseError error) {


            }
        });
    }

    private void addUserChangeListener() {
        mFirebaseDatabase.child(userId).addValueEventListener(
                new ValueEventListener() {
                    @Override
                    public void onDataChange(DataSnapshot dataSnapshot) {
                        User user = dataSnapshot.getValue(User.class);
                        // Check for null
                        if (user == null) {
                            Log.e(TAG, "User data is null!");
                            return;
                        }
                        Log.e(TAG, "User data is changed!" + user.name + ", " + user.address);
                        // Displaying newly updated name and email
                        // txtDetails.setText( user.name + ", " + user.address );
                        // Clearing edit text
                        //inputEmail.setText( "" );
                        //inputName.setText ( "" );
                        //toggleButton( );
                    }

                    @Override
                    public void onCancelled(DatabaseError error) {
                        // Failed to read value
                        Log.e(TAG, "Failed to read user", error.toException());
                    }
                }
        );
    }

}