package com.example.superqr;

import static android.content.ContentValues.TAG;

import android.Manifest;
import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.Bundle;
import android.util.Log;
import android.widget.Toast;

import androidx.activity.result.ActivityResult;
import androidx.activity.result.ActivityResultCallback;
import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;

import com.example.superqr.databinding.ActivityMainBinding;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FieldValue;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;

import java.io.ByteArrayOutputStream;
import java.util.ArrayList;
import java.util.List;

public class MainActivity extends AppCompatActivity implements EditInfoFragment.OnFragmentInteractionListener, ScanFragment.ScanFragmentListener, ScanFragment.ScanFragmentListener1, LocationListener {
    private static int REQUEST_IMAGE_CAPTURE = 1;
    private ActivityMainBinding binding;
    private StorageReference mStorageRef;
    Player player;
    FirebaseFirestore db;
    Fragment newFragment;
    LocationManager locationManager;

    // from: https://stackoverflow.com/questions/62671106/onactivityresult-method-is-deprecated-what-is-the-alternative
    // author: https://stackoverflow.com/users/4147849/muntashir-akon
    // used to pass Player object through into fragments.
    ActivityResultLauncher<Intent> resultLauncher = registerForActivityResult(
            new ActivityResultContracts.StartActivityForResult(),
            new ActivityResultCallback<ActivityResult>() {
                @Override
                public void onActivityResult(ActivityResult result) {
                    if (result.getResultCode() == Activity.RESULT_OK) {
                        Intent data = result.getData();
                        player = (Player) data.getParcelableExtra("player");
                    }
                }
            }
    );

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == REQUEST_IMAGE_CAPTURE && resultCode == RESULT_OK) { // Photo taken
            String playerID = player.getPlayerID();
            ArrayList<QRCode> qrCodes = player.getStats().getQrCodes();
            StorageReference qrcodes = mStorageRef.child(String.format("%s/%s", playerID, qrCodes.get(qrCodes.size() - 1).getHash()));

            // Get data as Bitmap and convert it into byte[] to upload with putBytes
            // https://stackoverflow.com/questions/56699632/how-to-upload-file-bitmap-to-cloud-firestore
            Bundle extras = data.getExtras();
            Bitmap imageBitmap = (Bitmap) extras.get("data");
            ByteArrayOutputStream baos = new ByteArrayOutputStream();
            imageBitmap.compress(Bitmap.CompressFormat.JPEG, 100, baos);
            byte[] imageData = baos.toByteArray();

            UploadTask uploadTask = qrcodes.putBytes(imageData);
            uploadTask.addOnFailureListener(new OnFailureListener() {
                @Override
                public void onFailure(@NonNull Exception e) {
                    Toast.makeText(MainActivity.this, "Failed to upload", Toast.LENGTH_SHORT);
                }
            }).addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
                @Override
                public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                    Toast.makeText(MainActivity.this, "Successful upload", Toast.LENGTH_SHORT);
                }
            });
        }
    }
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        //retrieve player from database
        mStorageRef = FirebaseStorage.getInstance().getReference();
        db = FirebaseFirestore.getInstance();
        // Get a top level reference to the collection
        loadData();
    }

    /**
     * Places a fragment on frame_layout in the main activity
     * @param fragment
     */
    private void replaceFragment(Fragment fragment) {
        FragmentManager fragmentManager = getSupportFragmentManager();
        FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();
        fragmentTransaction.replace(R.id.frame_layout, fragment);
        fragmentTransaction.commit();
    }

    /**
     * Load user profile from database.
     */
    private void loadData() {
        SharedPreferences sharedPreferences = getSharedPreferences("shared preferences", MODE_PRIVATE);
        String userName = sharedPreferences.getString("user", "");
        if (userName == "" || userName.contains("/")) {
            Intent intent = new Intent(MainActivity.this, LogInActivity.class);
            resultLauncher.launch(intent);
            loadFragments();
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
                            Toast.makeText(MainActivity.this, "Player Deleted", Toast.LENGTH_LONG);
                            Intent intent = new Intent(MainActivity.this, LogInActivity.class);
                            resultLauncher.launch(intent);
                            Log.d(TAG, "onComplete: data not exist");
                        }
                    } else {
                        Log.d(TAG, "get failed with", task.getException());
                    }
                }
            });
        }
    }

    /**
     * Load fragments for main activity, and handle requests for location and such.
     */
    public void loadFragments() {
        // All fragments are launched from this main activity.
        // When clicking on the navigation buttons, we open a new fragment to display
        binding = ActivityMainBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());
        requestLocation();

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
                    requestLocation();
                    newFragment = MapFragment.newInstance(player);
                    replaceFragment(newFragment);
                    break;
                case R.id.scan:
                    requestLocation();
                    newFragment = ScanFragment.newInstance(player, 0);
                    replaceFragment(newFragment);
                    break;
                case R.id.browse:
                    requestLocation();
                    newFragment = BrowseFragment.newInstance(player);
                    replaceFragment(newFragment);
                    break;
            }
            return true;
        });
    }

    /**
     * Adds QRCcode to the player, updates the database, and update's the player's
     * QRCode stats as necessary.
     * @param qrCode
     *      QRCode that is scanned
     */
    @Override
    public void onQRScanned(QRCode qrCode, boolean geo) {
        if (geo) {
            qrCode.setLocation(player.getLocation().getLatitude(), player.getLocation().getLongitude());
            Log.d("debug", "geo is true");
        }

        // Adds QR code to playerStats
        PlayerStats playerStats = player.getStats();
        playerStats.addQrCode(qrCode);
        player.setStats(playerStats);

        db.collection("users").document(player.getSettings().getUsername()).update(
                "stats.qrCodes", FieldValue.arrayUnion(qrCode),
                "stats.counts", (playerStats.getCounts()),
                "stats.highestScore", (playerStats.getHighestScore()),
                "stats.lowestScore", (playerStats.getLowestScore()),
                "stats.totalScore", (playerStats.getTotalScore()));

        // put QRCode into firestore
        db.collection("codes").document(qrCode.getHash()).set(qrCode);
    }

    /**
     * update user info in the database
     * @param newUsername
     *      Player's new username
     * @param newEmail
     *      Player's new email
     * @param newPhone
     */
    @Override
    public void onOkPressed(String newUsername, String newEmail, String newPhone) {
        String name = player.getSettings().getUsername();
        if (newUsername.contains("/")) {
            Toast.makeText(MainActivity.this, "Unsuccessful Update. Username inaccurate", Toast.LENGTH_LONG).show();
        }
        else if (name.equals(newUsername) || newUsername.isEmpty()) {
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
            // show new info
            newFragment = ProfileFragment.newInstance(player);
            replaceFragment(newFragment);
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
                                    Log.d(TAG, "DocumentSnapShot successfully deleted");
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
                            // display new info
                            newFragment = ProfileFragment.newInstance(player);
                            replaceFragment(newFragment);
                        }
                    } else {
                        Log.d(TAG, "get failed with", task.getException());
                    }
                }
            });
        }
    }

    /**
     * Used to request location from user. If gps is enabled, update player location
     */
    private void requestLocation() {
        ActivityCompat.requestPermissions(this,
                new String[]{Manifest.permission.ACCESS_FINE_LOCATION}, 1);
        //based on Ravi Tamada's code on https://www.androidhive.info/2015/02/android-location-api-using-google-play-services/
        try {
            locationManager = (LocationManager) getSystemService(Context.LOCATION_SERVICE);
            locationManager.requestLocationUpdates(LocationManager.GPS_PROVIDER, 5000, 5, this);
        } catch (SecurityException e) {
            e.printStackTrace();
        }
    }

    @Override
    public void onLocationChanged(@NonNull Location location) {
        player.setPlayerLocation(location.getLatitude(), location.getLongitude());
    }

    @Override
    public void onProviderDisabled(@NonNull String provider) {
        Toast.makeText(MainActivity.this, "Please enable GPS and Internet", Toast.LENGTH_SHORT).show();
    }

    //required method for LocationListener
    @Override
    public void onLocationChanged(@NonNull List<Location> locations) {
    }
    //required method for LocationListener
    @Override
    public void onFlushComplete(int requestCode) {
    }
    //required method for LocationListener
    @Override
    public void onProviderEnabled(@NonNull String provider) {
    }
    //required method for LocationListener
    @Override
    public void onPointerCaptureChanged(boolean hasCapture) {
    }
    //required method for LocationListener
    @Override
    public void onStatusChanged(String provider, int status, Bundle extras) {
    }

    /**
     * look up player in the database
     * display it if it exists in database
     * else display the search fragment
     * @param username
     */
    @Override
    public void onQRScanned1(String username) {
        if (username.contains("/")) {
            replaceOnScanned1();
        }
        else if (username.equals("")) {
            Toast.makeText(MainActivity.this, "Please enter a name.", Toast.LENGTH_SHORT).show();
        }
        else {
            DocumentReference docRef = db.collection("users").document(username);
            docRef.get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
                @Override
                public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                    Log.d(TAG, "onComplete: executing");
                    if (task.isSuccessful()) {
                        DocumentSnapshot document = task.getResult();
                        if (document.exists()) {
                            Log.d(TAG, "onComplete: data does exist");
                            Player otherPlayer = document.toObject(Player.class);
                            // go to profile view
                            Fragment displaySearchPlayer = DisplaySearchPlayerFragment.newInstance(player, otherPlayer);
                            FragmentManager fragmentManager = getSupportFragmentManager();
                            FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();
                            fragmentTransaction.replace(R.id.frame_layout, displaySearchPlayer);
                            //fragmentTransaction.addToBackStack(null);
                            fragmentTransaction.commit();
                        } else {
                            Log.d("first else", "onComplete: data not exist");
                            replaceOnScanned1();
                        }
                    } else {
                        Log.d("second else", "else");
                        replaceOnScanned1();
                    }
                }
            });
        }
    }

    /**
     * replace onScanned1() when player does not exist
     */
    private void replaceOnScanned1() {
        Toast.makeText(MainActivity.this, "Player does not exists", Toast.LENGTH_SHORT).show();
        Fragment searchPlayer = SearchPlayerFragment.newInstance(player);
        FragmentManager fragmentManager = getSupportFragmentManager();
        FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();
        fragmentTransaction.replace(R.id.frame_layout, searchPlayer);
        //fragmentTransaction.addToBackStack(null);
        fragmentTransaction.commit();
    }
}