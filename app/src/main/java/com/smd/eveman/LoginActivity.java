package com.smd.eveman;

import android.app.ProgressDialog;
import android.content.Intent;
import androidx.annotation.NonNull;
import com.google.android.material.textfield.TextInputLayout;
import androidx.appcompat.app.AppCompatActivity;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

public class LoginActivity extends AppCompatActivity {

    private TextInputLayout memail;
    private TextInputLayout mpassword;
    private Button msubmit;
    private ProgressDialog mLoginProgDialog;
    private FirebaseAuth mAuth;
    private DatabaseReference mUsersDataBase;



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        mAuth = FirebaseAuth.getInstance();
        mUsersDataBase = FirebaseDatabase.getInstance().getReference().child("Users");

        mLoginProgDialog = new ProgressDialog(this);
        memail = (TextInputLayout) findViewById(R.id.log_email);
        mpassword = (TextInputLayout) findViewById(R.id.log_pass);
        msubmit = (Button) findViewById(R.id.log_login);


        msubmit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String email = memail.getEditText().getText().toString();
                String password = mpassword.getEditText().getText().toString();

                if( TextUtils.isEmpty(email) || TextUtils.isEmpty(password))
                {

                }
                else
                {
                    mLoginProgDialog.setTitle("Logging in");
                    mLoginProgDialog.setMessage("Please wait while we check your credentials.");
                    mLoginProgDialog.show();
                    login_user(email,password);
                }
            }
        });

    }

    private void login_user(String email, String password) {

        mAuth.signInWithEmailAndPassword(email, password)
                .addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {

                        if (task.isSuccessful()) {



                            mLoginProgDialog.dismiss();
                            Intent mainIntent = new Intent(LoginActivity.this , MainActivity.class);
                            mainIntent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                            startActivity(mainIntent);
                            finish();


                        }
                        else
                        {
                            mLoginProgDialog.dismiss();
                            Toast.makeText(LoginActivity.this, "Error. Please try again", Toast.LENGTH_SHORT).show();
                        }

                    }
                });

    }
}
