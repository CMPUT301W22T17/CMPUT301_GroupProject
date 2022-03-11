package com.example.superqr;


import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.util.Log;
import android.widget.Toast;

import com.example.superqr.databinding.ActivityMainBinding;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

import java.lang.reflect.Type;
import java.util.ArrayList;


public class MainActivity extends AppCompatActivity {

    private ActivityMainBinding binding;
    Player player = new Player("steve", "123", "123");
    FirebaseFirestore db;
    Fragment newFragment;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        db = FirebaseFirestore.getInstance();
        // Get a top level reference to the collection

        loadData();

        // All fragments are launched from this main activity.
        // When clicking on the navigation buttons, we open a new fragment to display
        binding = ActivityMainBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        //we make the default "home" screen profile
        newFragment = ProfileFragment.newInstance(player);
        replaceFragment(newFragment);

        //https://www.youtube.com/watch?v=Bb8SgfI4Cm4
        binding.bottomNav.setOnItemSelectedListener(menuItem -> {
            switch (menuItem.getItemId()) {
                case R.id.profile:
                    newFragment = ProfileFragment.newInstance(player);
                    replaceFragment(newFragment);
                    break;
                case R.id.map:
                    newFragment = MapFragment.newInstance(player);
                    replaceFragment(newFragment);
                    break;
                case R.id.scan:
                    newFragment = ScanFragment.newInstance(player);
                    replaceFragment(newFragment);
                    break;
                case R.id.browse:
                    newFragment = BrowseFragment.newInstance(player);
                    replaceFragment(newFragment);
                    break;
            }
            return true;
        });

    }

    /**
     * Places a fragment on frame_layout in the main activity
     * @param fragment
     */
    private void replaceFragment(Fragment fragment){
        FragmentManager fragmentManager = getSupportFragmentManager();
        FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();
        fragmentTransaction.replace(R.id.frame_layout, fragment);
        fragmentTransaction.commit();
    }
  
    /*public void checkNewUser(){
        SharedPreferences sharedPrefs = PreferenceManager.getDefaultSharedPreferences(MainActivity.this);
        //https://stackoverflow.com/questions/35681693/checking-if-shared-preferences-exist
        boolean ranBefore = sharedPrefs.getBoolean("ranBefore", false);
        if (!ranBefore) {
            SharedPreferences.Editor editor = sharedPrefs.edit();
            editor.putBoolean("ranBefore", true);
            editor.commit();
            Intent intent = new Intent(MainActivity.this, LogInActivity.class);
            startActivity(intent);
        }*/

    private void loadData() {
        SharedPreferences sharedPreferences = getSharedPreferences("shared preferences", MODE_PRIVATE);
        //Gson gson = new Gson();
        String userName = sharedPreferences.getString("user", "");
        //Log.d("userName:", userName);
        if (userName == "") {
            Intent intent = new Intent(MainActivity.this, LogInActivity.class);
            startActivity(intent);
        } else {
            Log.d("username", userName);
            DocumentReference docRef = db.collection("users").document(userName);
            docRef.get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
                @Override
                public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                    Log.d(TAG, "onComplete: executing");
                    if (task.isSuccessful()) {
                        DocumentSnapshot document = task.getResult();
                        if (document.exists()) {
                            Log.d(TAG, "onComplete: data does exist");
                            player = document.toObject(Player.class);
                        } else {
                            // should handle this
                            Log.d(TAG, "onComplete: data not exist");
                        }
                    } else {
                        Log.d(TAG, "get failed with", task.getException());
                    }
                }
            });
        }
    }

    //https://stackoverflow.com/questions/48499310/how-to-return-a-documentsnapshot-as-a-result-of-a-method
}