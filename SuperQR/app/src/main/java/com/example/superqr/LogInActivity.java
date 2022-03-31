package com.example.superqr;


import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.FrameLayout;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;

public class LogInActivity extends AppCompatActivity implements ScanFragment.ScanFragmentListener1 {

    Button newUserButton, existingUserButton, signupButton;
    EditText usernameEditText, emailEditText, phoneEditText;
    FrameLayout loginFrameLayout;
    FirebaseFirestore db;
    Player player;

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
        loginFrameLayout = findViewById(R.id.logInFrameLayout);

        // Access a Cloud Firestore instance from your Activity
        db = FirebaseFirestore.getInstance();

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
                ScanFragment scanFragment = new ScanFragment().newInstance(player, 1);
                getSupportFragmentManager().beginTransaction().replace(R.id.logInFrameLayout, scanFragment).commit();
                newUserButton.setVisibility(View.INVISIBLE);
                existingUserButton.setVisibility(View.INVISIBLE);
                loginFrameLayout.setVisibility(View.VISIBLE);
            }
        });

        signupButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                // adapt the user's information from EditTexts
                String userName = usernameEditText.getText().toString();
                String email = emailEditText.getText().toString();
                String phone = phoneEditText.getText().toString();

                if (userName.contains("/")) {
                    Toast.makeText(LogInActivity.this, "Username is invalid", Toast.LENGTH_LONG).show();
                }

                else {
                    // check if username already exists
                    DocumentReference docRef = db.collection("users").document(userName);
                    docRef.get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
                        @Override
                        public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                            if (task.isSuccessful()) {
                                DocumentSnapshot ds = task.getResult();
                                if (ds.exists()) {
                                    Toast.makeText(LogInActivity.this,
                                            "Username already exists...", Toast.LENGTH_LONG)
                                            .show();
                                } else {
                                    // create new player class
                                    player = new Player(userName, phone, email);
                                    // save class in Firestore
                                    db.collection("users").document(userName).set(player);
                                    // save username in SharedPreferences
                                    SharedPreferences sharedPreferences = getSharedPreferences("shared preferences", MODE_PRIVATE);
                                    SharedPreferences.Editor editor = sharedPreferences.edit();
                                    editor.putString("user", userName);
                                    editor.apply();

                                    // pass player back to MainActivity
                                    Intent i = new Intent(LogInActivity.this, MainActivity.class);
                                    i.putExtra("player", player);
                                    setResult(RESULT_OK, i);
                                    finish();
                                }
                            }
                        }
                    });
                }
            }
        });
    }

    @Override
    public void onQRScanned1(String username) {

        if (username.contains("/")) {
            loginFrameLayout.setVisibility(View.INVISIBLE);
            Toast.makeText(LogInActivity.this, "Invalid Username", Toast.LENGTH_LONG).show();
            newUserButton.setVisibility(View.VISIBLE);
            existingUserButton.setVisibility(View.VISIBLE);
        }
        // check if username already exists
        else {
            DocumentReference docRef = db.collection("users").document(username);
            docRef.get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
                @Override
                public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                    if (task.isSuccessful()) {
                        DocumentSnapshot document = task.getResult();
                        if (document.exists()) { // check if player is in database
                            player = document.toObject(Player.class);
                            SharedPreferences sharedPreferences = getSharedPreferences("shared preferences", MODE_PRIVATE);
                            SharedPreferences.Editor editor = sharedPreferences.edit();
                            editor.putString("user", username);
                            editor.apply();
                            // pass player back to MainActivity
                            Intent i = new Intent(LogInActivity.this, MainActivity.class);
                            i.putExtra("player", player);
                            setResult(RESULT_OK, i);
                            finish();
                        } else {
                            loginFrameLayout.setVisibility(View.INVISIBLE);
                            Toast.makeText(LogInActivity.this,
                                    "Please make an account",
                                    Toast.LENGTH_LONG).show();
                            newUserButton.setVisibility(View.VISIBLE);
                            existingUserButton.setVisibility(View.VISIBLE);
                        }
                    }
                }
            });
        }
        }

        @Override
        public void onBackPressed() {
            loginFrameLayout.setVisibility(View.INVISIBLE);
            newUserButton.setVisibility(View.VISIBLE);
            existingUserButton.setVisibility(View.VISIBLE);
            signupButton.setVisibility(View.INVISIBLE);
            usernameEditText.setVisibility(View.INVISIBLE);
            emailEditText.setVisibility(View.INVISIBLE);
            phoneEditText.setVisibility(View.INVISIBLE);
            Toast.makeText(LogInActivity.this, "MAKE A SELECTION", Toast.LENGTH_SHORT).show();
        }
}