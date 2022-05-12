package com.example.mapzfromscratch;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.widget.Button;
import android.widget.LinearLayout;
import android.view.ViewGroup.LayoutParams;
import android.widget.Toast;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import android.view.View;
import android.view.KeyEvent;


public class SeeFriendsActivity extends AppCompatActivity {
    private DatabaseReference mFirebaseDatabase;
    private FirebaseDatabase mFirebaseInstance;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_see_friends);

        mFirebaseInstance = FirebaseDatabase.getInstance();
        // Getting reference to 'users' node
        mFirebaseDatabase = mFirebaseInstance.getReference("users");


        //Button btn = new Button(this);
        //btn.setText("Submit");
        //LinearLayout linearLayout = (LinearLayout)findViewById(R.id.buttonlayout);
        //LayoutParams buttonlayout = new LayoutParams(LayoutParams.MATCH_PARENT, LayoutParams.WRAP_CONTENT);
        //linearLayout.addView(btn, buttonlayout);

        getAllFriends();

    }
    boolean isUpdted=false;
    private void getAllFriends() {
        DatabaseReference ref = FirebaseDatabase.getInstance().getReference().child("users");
        ref.orderByChild("name").addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                Iterator<DataSnapshot> dataSnapshots = dataSnapshot.getChildren().iterator();
                List<User> users = new ArrayList<>();
                User user=null ;
                String strGUID="";
                LinearLayout linearLayout = (LinearLayout)findViewById(R.id.buttonlayout);
                linearLayout.setOrientation(1);
                LayoutParams buttonlayout = new LayoutParams(LayoutParams.MATCH_PARENT, LayoutParams.WRAP_CONTENT);
                List<Button> lstButton= new ArrayList<>();
                while (dataSnapshots.hasNext()) {
                    DataSnapshot dataSnapshotChild = dataSnapshots.next();
                    user = dataSnapshotChild.getValue(User.class);

                    Button btn = new Button(getApplicationContext());
                    btn.setText(user.name);
                    btn.setTag(user.address);
                    btn.setOnClickListener(new View.OnClickListener() {
                        public void onClick(View v) {
                            Intent i = new Intent(SeeFriendsActivity.this, MainActivity.class);
                           //final String strvalue=user.name;
                            i.putExtra("key",""+v.getTag());
                            startActivity(i);
                        }
                    });
                    lstButton.add(btn);
                    linearLayout.addView(btn);
                    //linearLayout.addView(btn, buttonlayout);


                    // users.add(user);
                    //isPresent= true;
                    //strGUID=dataSnapshotChild.getKey() ;
                    //Log.e("IsPresent Inside: ",  String.valueOf(isPresent));
                }
                //for(int i=0;i<lstButton.size();i++)
                //{
                 //   linearLayout.addView((Button)lstButton.get(i), buttonlayout);
                //}


              //  Intent intent = new Intent(getApplicationContext(), MainActivity.class);
             //   startActivity(intent);
                // for (DataSnapshot datas : dataSnapshot.getChildren()) {
                //    String keys = datas.getKey();
                //}
            }

            @Override
            public void onCancelled(DatabaseError error) {


            }
        });
    }
}