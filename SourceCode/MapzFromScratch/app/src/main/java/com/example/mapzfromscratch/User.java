package com.example.mapzfromscratch;
 import com.google.firebase.database.IgnoreExtraProperties;

@IgnoreExtraProperties
public class User {
    public String name;
    public String address;

    // Default constructor required for calls to
    // DataSnapshot.getValue( User.class )
    public User( ) { }

    public User( String name, String Address ) {
        this.name  = name;
        this.address = Address;
    }
}