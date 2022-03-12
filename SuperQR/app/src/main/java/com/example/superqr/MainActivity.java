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
import android.widget.TextView;
import android.widget.Toast;

import com.example.superqr.databinding.ActivityMainBinding;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;

import org.w3c.dom.Text;


public class MainActivity extends AppCompatActivity implements EditInfoFragment.OnFragmentInteractionListener {

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

    @Override
    public void onOkPressed(String newUsername, String newEmail, String newPhone) {
        String name = player.getSettings().getUsername();
        if (name.equals(newUsername) || newUsername.isEmpty()) {
            player.getSettings().setEmail(newEmail);
            player.getSettings().setPhone(newPhone);
            db.collection("users").document(name)
                    .update(
                            "settings.email", newEmail,
                            "settings.phone", newPhone
                    );
            PlayerSettings ps = player.getSettings();
            ps.setEmail(newEmail);
            ps.setPhone(newPhone);
            player.setSettings(ps);
            Toast.makeText(MainActivity.this, "Successful Update...", Toast.LENGTH_LONG).show();
        }
        else {
            DocumentReference docRef = db.collection("users").document(newUsername);
            docRef.get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
                @Override
                public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                    Log.d(TAG, "onComplete: executing");
                    if (task.isSuccessful()) {
                        DocumentSnapshot document = task.getResult();
                        if (document.exists()) {
                            Log.d(TAG, "onComplete: data does exist");
                            // do not add
                            Toast.makeText(MainActivity.this, "Unsuccessful Update. Username already exists...", Toast.LENGTH_LONG).show();
                        } else {
                            // rename and add to database
                            Log.d(TAG, "onComplete: data not exist");
                            // delete existing user collection
                            DocumentReference docRefOldName = db.collection("users")
                                    .document(player.getSettings().getUsername());
                            docRefOldName.delete().addOnSuccessListener(new OnSuccessListener<Void>() {
                                @Override
                                public void onSuccess(Void unused) {
                                    Log.d(TAG, "DocumentSnapShot successfuly deleted");
                                }
                            });
                            PlayerSettings ps = player.getSettings();
                            ps.setUsername(newUsername);
                            ps.setEmail(newEmail);
                            ps.setPhone(newPhone);
                            player.setSettings(ps);
                            // create new document references to newUsername
                            db.collection("users").document(newUsername).set(player);
                            SharedPreferences sharedPreferences = getSharedPreferences("shared preferences", MODE_PRIVATE);
                            SharedPreferences.Editor editor = sharedPreferences.edit();
                            editor.putString("user", newUsername);
                            editor.apply();
                        }
                    } else {
                        Log.d(TAG, "get failed with", task.getException());
                    }
                }
            });
        }
        newFragment = ProfileFragment.newInstance(player);
        replaceFragment(newFragment);
    }
}