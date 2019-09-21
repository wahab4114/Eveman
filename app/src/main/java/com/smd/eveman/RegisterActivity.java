package com.smd.eveman;

import android.app.ProgressDialog;
import android.content.Intent;
import androidx.annotation.NonNull;
import com.google.android.material.textfield.TextInputLayout;
import androidx.appcompat.app.AppCompatActivity;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.util.HashMap;

public class RegisterActivity extends AppCompatActivity {

    private TextInputLayout mDisplayName;
    private TextInputLayout mEmail;
    private TextInputLayout mPassword;
    private Button mCreateButton;
    private RadioGroup mRadioGroup;
    private RadioButton mRadioBtn;
    private DatabaseReference mDatabase;
    private ProgressDialog mRegProgressDialog;
    int checkedID;


    private FirebaseAuth mAuth;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register);

        mRegProgressDialog = new ProgressDialog(this);

        mAuth = FirebaseAuth.getInstance();
        mDisplayName = (TextInputLayout) findViewById(R.id.reg_dispname);
        mEmail = (TextInputLayout) findViewById(R.id.reg_email);
        mPassword = (TextInputLayout) findViewById(R.id.reg_pass);
        mRadioGroup = (RadioGroup) findViewById(R.id.reg_radiogrp);
        mCreateButton = (Button) findViewById(R.id.reg_createaccount) ;

        checkedID = mRadioGroup.getCheckedRadioButtonId();

        mRadioBtn = (RadioButton) findViewById( checkedID );

        mRadioGroup.setOnCheckedChangeListener(new RadioGroup.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(RadioGroup group, int checkedId) {

                Log.e("eeeeeeeee","---------------->");
                mRadioBtn = (RadioButton) findViewById( checkedId );

            }
        });

        mCreateButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

               String dispname = mDisplayName.getEditText().getText().toString();
               String email = mEmail.getEditText().getText().toString();
               String password = mPassword.getEditText().getText().toString();
               String role = mRadioBtn.getText().toString();


                if(TextUtils.isEmpty(dispname) || TextUtils.isEmpty(email) ||TextUtils.isEmpty(password) || TextUtils.isEmpty(role) )
                {

                }

                else{

                    if(password.length()<6)
                    {
                        Toast.makeText(RegisterActivity.this, "ERROR: Password should be of 6 lenght at least!", Toast.LENGTH_SHORT).show();

                    }

                    else {
                        mRegProgressDialog.setTitle("Registering User");
                        mRegProgressDialog.setMessage("Please wait while we create your account");
                        mRegProgressDialog.setCanceledOnTouchOutside(false);
                        mRegProgressDialog.show();
                        register_user(dispname, email, password,role);
                    }


                }


            }
        });

    }
    private void register_user(final String dispname, String email, String pass, final String role)
    {
        mAuth.createUserWithEmailAndPassword(email, pass)
                .addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        if (task.isSuccessful()) {

                            mRegProgressDialog.dismiss();
                            FirebaseUser currentUser = FirebaseAuth.getInstance().getCurrentUser();
                            String UID = currentUser.getUid().toString();
                            mDatabase = FirebaseDatabase.getInstance().getReference().child("Users").child(UID);
                            // root dir                         first child   2nd child
                            HashMap<String,String> UsersMap = new HashMap<String, String>();
                            UsersMap.put("name",dispname);
                            UsersMap.put("role",role);
                            mDatabase.setValue(UsersMap).addOnCompleteListener(new OnCompleteListener<Void>() {
                                @Override
                                public void onComplete(@NonNull Task<Void> task) {
                                    mRegProgressDialog.dismiss();
                                    Toast.makeText(RegisterActivity.this, "Registration successfull", Toast.LENGTH_SHORT).show();

                                    Intent mainIntent = new Intent(RegisterActivity.this,MainActivity.class);
                                    mainIntent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                                    startActivity(mainIntent);
                                    finish();

                                }
                            });



                        } else {
                            mRegProgressDialog.dismiss();

                            Toast.makeText(RegisterActivity.this, "Registration Error", Toast.LENGTH_SHORT).show();
                        }


                    }
                });
    }
}
