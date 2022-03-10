package com.example.superqr;


import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;

import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.FirebaseFirestore;

public class LogInActivity extends AppCompatActivity {

    Button newUserButton, existingUserButton, signupButton;
    EditText usernameEditText, emailEditText, phoneEditText;
    FirebaseFirestore db;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_log_in);

        newUserButton = findViewById(R.id.newUserButton);
        existingUserButton = findViewById(R.id.existingUserButton);
        signupButton = findViewById(R.id.signUpButton);
        usernameEditText = findViewById(R.id.userNameEditText);
        emailEditText = findViewById(R.id.emailEditText);
        phoneEditText = findViewById(R.id.phoneEditText);

        // Access a Cloud Firestore instance from your Activity
        db = FirebaseFirestore.getInstance();
        // Get a top level reference to the collection
        final CollectionReference collectionReference = db.collection("Users");

        newUserButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                //getSupportFragmentManager().beginTransaction().replace(R.id.logInFrameLayout, new NewUserFragment()).commit();

                newUserButton.setVisibility(View.GONE);
                existingUserButton.setVisibility(View.GONE);

                signupButton.setVisibility(View.VISIBLE);
                usernameEditText.setVisibility(View.VISIBLE);
                emailEditText.setVisibility(View.VISIBLE);
                phoneEditText.setVisibility(View.VISIBLE);

            }
        });

        existingUserButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                getSupportFragmentManager().beginTransaction().replace(R.id.logInFrameLayout, new ExistingUserFragment()).commit();
                newUserButton.setVisibility(View.GONE);
                existingUserButton.setVisibility(View.GONE);
            }
        });

        signupButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                finish();
            }
        });
    }
}