package com.smd.eveman;

import android.app.ProgressDialog;
import android.content.Intent;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

public class MainActivity extends AppCompatActivity {

    private FirebaseAuth mAuth;
    private DatabaseReference mDatabase;
    private String currentRole;
    private ProgressDialog mRegProgressDialog;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        mRegProgressDialog = new ProgressDialog(this);

        // Initialize Firebase Auth
        mAuth = FirebaseAuth.getInstance();
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
            Intent intent  = getIntent();
            finish();
            startActivity(intent);
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    public void onStart() {
        super.onStart();
        // Check if user is signed in (non-null) and update UI accordingly.
        // return null if no user is logged in



        FirebaseUser currentUser = mAuth.getCurrentUser();
        if(currentUser == null)
        {
            // go to home page(login page)
            Intent startIntent = new Intent(MainActivity.this,StartActivity.class);
            startActivity(startIntent);
            mRegProgressDialog.show();
            finish();
        }
        mRegProgressDialog.setTitle("Checking user type");
        mRegProgressDialog.setMessage("Loading user data...");
        mRegProgressDialog.setCanceledOnTouchOutside(false);
        mRegProgressDialog.show();

        try {
            mDatabase = FirebaseDatabase.getInstance().getReference().child("Users").child(currentUser.getUid()).child("role");
            mDatabase.addListenerForSingleValueEvent(new ValueEventListener() {
                @Override
                public void onDataChange(@NonNull DataSnapshot dataSnapshot) {

                    currentRole = dataSnapshot.getValue().toString();
                    Log.e("------>",currentRole);
                    if(currentRole.contains("Organizer"))
                    {
                        startActivity(new Intent(MainActivity.this,OrganizerActivity.class));
                        finish();

                    }
                    else if(currentRole.contentEquals("User"))
                    {
                        startActivity(new Intent(MainActivity.this,User.class));
                        finish();
                    }

                    mRegProgressDialog.dismiss();

                }

                @Override
                public void onCancelled(@NonNull DatabaseError databaseError) {

                }
            });
        }
        catch (Exception e)
        {

        }





    }
}
