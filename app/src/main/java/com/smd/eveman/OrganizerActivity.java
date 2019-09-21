package com.smd.eveman;

import android.app.ProgressDialog;
import android.content.Intent;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.floatingactionbutton.FloatingActionButton;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.location.Location;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.ListView;
import android.widget.Toast;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.UUID;

public class OrganizerActivity extends AppCompatActivity {

    private FirebaseAuth mAuth;
    private FloatingActionButton fab;

    ArrayList<Event> EventArrayList;
    ListView eventLV;
    EventAdapter eventAdapter;
    FirebaseUser currentUser;
    DatabaseReference dBref;
    private ProgressDialog mRegProgressDialog;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_organizer);

        Log.e("organizer oncreate","---------------------------->");


        mRegProgressDialog = new ProgressDialog(this);


        mAuth = FirebaseAuth.getInstance();

        currentUser = mAuth.getCurrentUser();


        fab= (FloatingActionButton) findViewById(R.id.fab) ;

        eventLV = (ListView) findViewById(R.id.eventLV);

        EventArrayList = new ArrayList<Event>();

        eventAdapter = new EventAdapter(this,EventArrayList);


        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                Intent intent = new Intent(OrganizerActivity.this,MapsActivity.class);
                //intent.putExtra("array",PinableLocationArrayList);
                startActivityForResult(intent,1);

            }
        });

        eventLV.setAdapter(eventAdapter);

            dBref = FirebaseDatabase.getInstance().getReference().child("Users").child(currentUser.getUid()).child("events");
            dBref.addValueEventListener(new ValueEventListener() {
                @Override
                public void onDataChange(@NonNull DataSnapshot dataSnapshot) {

                    Log.e("organizer data change","---------------------------->");

                    String title="";
                    String place="";
                    String desc="";
                    Double lat=0.0;
                    Double lng=0.0;



                    for(DataSnapshot ds : dataSnapshot.getChildren()){

                          for(DataSnapshot ds2 : ds.getChildren())
                          {

                               if(ds2.getKey().toString().contentEquals("eventTitle"))
                               {
                                   title = ds2.getValue().toString();
                               }
                               else if (ds2.getKey().toString().contentEquals("eventDescription"))
                               {
                                   desc = ds2.getValue().toString();
                               }
                               else if (ds2.getKey().toString().contentEquals("placeName"))
                               {
                                   place = ds2.getValue().toString();
                               }
                               else if(ds2.getKey().toString().contentEquals("lat"))
                               {
                                   lat = Double.valueOf(ds2.getValue().toString());
                               }
                               else
                               {
                                   lng = Double.valueOf(ds2.getValue().toString());
                               }
                          }

                          EventArrayList.add(new Event(lat,lng,desc,place,title));

                    }
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
            startActivity(new Intent(OrganizerActivity.this,MainActivity.class));
            finish();
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {

        Log.e("organizer On act result","---------------------------->");


        if(requestCode==1)
        {
            if(resultCode==RESULT_OK)
            {
                String title =  data.getStringExtra("eventTitle");
                String name =   data.getStringExtra("placeName");
                String desc =   data.getStringExtra("eventDescription");
                Double  lat =   data.getDoubleExtra("lat",0.0);
                Double  lng =   data.getDoubleExtra("lng",0.0);


                EventArrayList.clear();

                /*EventArrayList.add(new Event(lat,lng,desc,name,title));
                eventAdapter.notifyDataSetChanged();*/


                String key = UUID.randomUUID().toString();

                dBref = FirebaseDatabase.getInstance().getReference().child("Users").child(currentUser.getUid()).child("events")
                        .child(key);

                HashMap<String,String> EventsMap = new HashMap<String, String>();
                EventsMap.put("eventTitle",title);
                EventsMap.put("placeName",name);
                EventsMap.put("eventDescription",desc);
                EventsMap.put("lat",String.valueOf(lat));
                EventsMap.put("lng",String.valueOf(lng));
                mRegProgressDialog.setTitle("Uploading data");
                mRegProgressDialog.setMessage("Please wait....");
                mRegProgressDialog.setCanceledOnTouchOutside(false);
                mRegProgressDialog.show();
                dBref.setValue(EventsMap).addOnCompleteListener(new OnCompleteListener<Void>() {
                    @Override
                    public void onComplete(@NonNull Task<Void> task) {

                    mRegProgressDialog.dismiss();


                    }
                });




            }
        }


    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        Log.e("organizer Ondestroy","---------------------------->");

    }

    @Override
    protected void onStart() {
        Log.e("organizer OnStart","---------------------------->");

        super.onStart();


    }

    @Override
    protected void onPause() {
        super.onPause();
        Log.e("organizer OnPause","---------------------------->");

    }

    @Override
    protected void onRestart() {
        super.onRestart();
        Log.e("organizer OnRestart","---------------------------->");

    }
}
