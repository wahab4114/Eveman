package com.smd.eveman;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.ListView;

import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;

public class User extends AppCompatActivity {


    private FirebaseAuth mAuth;
    private FloatingActionButton fab;

    ArrayList<Event> EventArrayList;
    ListView userLV;
    usersAdap eventAdapter;
    FirebaseUser currentUser;
    DatabaseReference dBref;
    private ProgressDialog mRegProgressDialog;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_user);

        mAuth = FirebaseAuth.getInstance();

        currentUser = mAuth.getCurrentUser();

        userLV = (ListView) findViewById(R.id.userLV);
        mRegProgressDialog = new ProgressDialog(this);


        EventArrayList = new ArrayList<Event>();

        eventAdapter = new usersAdap(this,EventArrayList);

        userLV.setAdapter(eventAdapter);

        dBref = FirebaseDatabase.getInstance().getReference().child("Users");
        dBref.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {

                Log.e("user data change","---------------------------->");

                String title="";
                String place="";
                String desc="";
                Double lat=0.0;
                Double lng=0.0;

                mRegProgressDialog.setTitle("Searching nearby events");
                mRegProgressDialog.setMessage("Please wait.....");
                mRegProgressDialog.setCanceledOnTouchOutside(false);
                mRegProgressDialog.show();


                DataSnapshot dss =  dataSnapshot.child("events");

                for(DataSnapshot ds : dataSnapshot.getChildren()){

                    for(DataSnapshot dskeys : ds.getChildren())
                    {
                        for (DataSnapshot dsudata: dskeys.getChildren())
                        {
                            if(dskeys.getKey().toString().contentEquals("events"))
                            {

                                for (DataSnapshot dsevent: dsudata.getChildren())
                                {

                                            if(dsevent.getKey().toString().contentEquals("eventTitle"))
                                            {
                                                title = dsevent.getValue().toString();
                                            }
                                            else if (dsevent.getKey().toString().contentEquals("eventDescription"))
                                            {
                                                desc = dsevent.getValue().toString();
                                            }
                                            else if (dsevent.getKey().toString().contentEquals("placeName"))
                                            {
                                                place = dsevent.getValue().toString();
                                            }
                                            else if(dsevent.getKey().toString().contentEquals("lat"))
                                            {
                                                lat = Double.valueOf(dsevent.getValue().toString());
                                            }
                                            else
                                            {
                                                lng = Double.valueOf(dsevent.getValue().toString());
                                            }


                                }

                                EventArrayList.add(new Event(lat,lng,desc,place,title));


                            }
                        }
                    }




                }



                mRegProgressDialog.dismiss();
                eventAdapter.notifyDataSetChanged();


            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });


    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu, menu);
        return super.onCreateOptionsMenu(menu);
    }

    // handle button activities
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();

        if (id == R.id.logout) {
            mAuth.signOut();
            FirebaseUser usr = mAuth.getCurrentUser();
            startActivity(new Intent(User.this,MainActivity.class));
            finish();
        }
        return super.onOptionsItemSelected(item);
    }


}
