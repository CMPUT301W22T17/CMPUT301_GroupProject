package com.example.superqr;


import static android.content.ContentValues.TAG;

import androidx.activity.result.ActivityResult;
import androidx.activity.result.ActivityResultCallback;
import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;

import android.app.Activity;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.util.Log;
import com.example.superqr.databinding.ActivityMainBinding;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;


public class MainActivity extends AppCompatActivity {

    private ActivityMainBinding binding;
    Player player;
    FirebaseFirestore db;
    Fragment newFragment;

    // from: https://stackoverflow.com/questions/62671106/onactivityresult-method-is-deprecated-what-is-the-alternative
    // author: https://stackoverflow.com/users/4147849/muntashir-akon
    ActivityResultLauncher<Intent> resultLauncher = registerForActivityResult(
            new ActivityResultContracts.StartActivityForResult(),
            new ActivityResultCallback<ActivityResult>() {
                @Override
                public void onActivityResult(ActivityResult result) {
                    if (result.getResultCode() == Activity.RESULT_OK) {
                        Intent data = result.getData();
                        player = (Player) data.getParcelableExtra("player");
                        loadFragments();
                    }
                }
            }
    );
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        db = FirebaseFirestore.getInstance();
        // Get a top level reference to the collection

        loadData();
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


    private void loadData() {
        SharedPreferences sharedPreferences = getSharedPreferences("shared preferences", MODE_PRIVATE);
        String userName = sharedPreferences.getString("user", "");
        if (userName == "") {
            Intent intent = new Intent(MainActivity.this, LogInActivity.class);
            resultLauncher.launch(intent);
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
                            //Load fragments after getting data, as Firestore is slow
                            loadFragments();
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

    public void loadFragments(){
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

}