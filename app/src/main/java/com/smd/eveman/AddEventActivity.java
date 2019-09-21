package com.smd.eveman;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.MultiAutoCompleteTextView;
import android.widget.Toast;

import com.google.android.material.textfield.TextInputLayout;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

public class AddEventActivity extends AppCompatActivity {

    private FirebaseAuth mAuth;
    private String placeName;
    private TextInputLayout eventTitle;
    private TextInputLayout eventPlace;
    private MultiAutoCompleteTextView eventDescription;
    private Button addButton;
    private double lat;
    private double lng;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_event);
        mAuth = FirebaseAuth.getInstance();
        placeName = getIntent().getStringExtra("name");
        lat = getIntent().getDoubleExtra("lat",0.0);
        lng = getIntent().getDoubleExtra("long",0.0);

        eventTitle = (TextInputLayout) findViewById(R.id.eventTitle);

        eventPlace = (TextInputLayout) findViewById(R.id.eventPlace);

        eventDescription = (MultiAutoCompleteTextView) findViewById(R.id.eventDescription);

        addButton = (Button) findViewById(R.id.addEvent);

        if(TextUtils.equals(placeName,""))
        {
            eventPlace.getEditText().setHint(" place name can't be fetched ");

        }
        else
            eventPlace.getEditText().setHint(placeName);

        addButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                String title = eventTitle.getEditText().getText().toString();
                String desc = eventDescription.getText().toString();



                if(TextUtils.isEmpty(title) || TextUtils.isEmpty(desc))
                {
                    Toast.makeText(AddEventActivity.this,"Title or description field can't be empty",Toast.LENGTH_SHORT).show();
                }
                else {

                    Intent returnIntent = new Intent();
                    returnIntent.putExtra("eventTitle",title);
                    returnIntent.putExtra("placeName",placeName);
                    returnIntent.putExtra("eventDescription",desc);
                    returnIntent.putExtra("lat",lat);
                    returnIntent.putExtra("lng",lng);

                    setResult(RESULT_OK,returnIntent);

                    finish();

                }

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
            startActivity(new Intent(AddEventActivity.this,MainActivity.class));
            finish();
        }
        return super.onOptionsItemSelected(item);
    }

}
