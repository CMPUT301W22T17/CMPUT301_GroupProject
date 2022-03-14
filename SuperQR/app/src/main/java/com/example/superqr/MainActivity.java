package com.example.superqr;

import static android.content.ContentValues.TAG;
import android.Manifest;
import android.app.Activity;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.location.Location;
import android.location.LocationManager;
import android.net.Uri;
import android.os.Bundle;
import android.provider.Settings;
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
import java.util.HashMap;
import java.util.Map;
import java.io.ByteArrayOutputStream;
import java.util.ArrayList;

public class MainActivity extends AppCompatActivity implements EditInfoFragment.OnFragmentInteractionListener, ScanFragment.ScanFragmentListener {
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
                        loadFragments();
                    }
                }
            }
    );

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == REQUEST_IMAGE_CAPTURE && resultCode == RESULT_OK) { // Photo taken
            String userName = player.getSettings().getUsername();
            ArrayList<QRCode> qrCodes = player.getStats().getQrCodes();
            StorageReference qrcodes = mStorageRef.child(String.format("%s/%s", userName, qrCodes.get(qrCodes.size() - 1).getHash()));

            // Get data as Bitmap and convert it into byte[] to upload with putBytes
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

        mStorageRef = FirebaseStorage.getInstance().getReference();
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

    /**
     * Load user profile from database.
     */
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

    /**
     * Load fragments for main activity, and handle requests for location and such.
     */
    public void loadFragments(){
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
     * Adds QRCcode to the player,updates the database, and update's the player's
     * QRCode stats as necessary.
     * @param qrCode
     */
    @Override
    public void onQRScanned(QRCode qrCode) {


        qrCode.setLocation(player.getPlayerLocation().getLatitude(), player.getPlayerLocation().getLongitude());

        PlayerStats playerStats = player.getStats();
        Log.d("debug", String.valueOf(playerStats.getQrCodes()));
        playerStats.addQrCode(qrCode);
        playerStats.setCounts();
        playerStats.setTotalScore(qrCode.getScore());

        int highScore = playerStats.getQrCodes().get(0).getScore();
        int lowScore = playerStats.getQrCodes().get(0).getScore();
        for (int i = 0; i < playerStats.getQrCodes().size(); i++) {
            if (playerStats.getQrCodes().get(i).getScore() > highScore) {
                highScore = playerStats.getQrCodes().get(i).getScore();
            }
            else if (playerStats.getQrCodes().get(i).getScore() < lowScore) {
                lowScore = playerStats.getQrCodes().get(i).getScore();
            }
        }

        playerStats.setHighestScore(highScore);
        playerStats.setLowestScore(lowScore);

        Log.d("deb", String.valueOf(playerStats.getQrCodes()));
        player.setStats(playerStats);
        db.collection("users").document(player.getSettings().getUsername()).update(
                "stats.qrCodes", FieldValue.arrayUnion(qrCode));

        // put QRCode into firestore
        db.collection("codes").document(qrCode.getHash()).set(qrCode);
        db.collection("codes").document(qrCode.getHash())
                .update(
                        "hash", qrCode.getHash(),
                        "score", qrCode.getScore(),
                        "location.latitude", qrCode.getStoreLocation().getLatitude(),
                        "location.longitude", qrCode.getStoreLocation().getLongitude(),
                        "scanned", qrCode.getScanned()
                );

        // remove storeLocation
        DocumentReference ref = db.collection("codes").document(qrCode.getHash());
        Map<String, Object> updates = new HashMap<>();
        updates.put("storeLocation", FieldValue.delete());
        ref.update(updates).addOnCompleteListener(new OnCompleteListener<Void>() {
            @Override
            public void onComplete(@NonNull Task<Void> task) {
                Log.d("Message: ", "REMOVED storeLocation field");
            }
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
     * Used to request location from user. If gps is enabled, update player location, else, enable GPS.
     */
    private void requestLocation(){
        ActivityCompat.requestPermissions( this,
                new String[] {Manifest.permission.ACCESS_FINE_LOCATION}, 1);
        locationManager = (LocationManager) getSystemService(Context.LOCATION_SERVICE);
        if (!locationManager.isProviderEnabled(LocationManager.GPS_PROVIDER)) {
            OnGPS();
        }
        else {
            getLocation();
        }
    }

    /**
     * Gets location from GPS, then updates the player location.
     */
    private void getLocation() {
        if (ActivityCompat.checkSelfPermission(
                MainActivity.this,Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(
                MainActivity.this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.ACCESS_FINE_LOCATION}, 1);
        } else {
            Location locationGPS = locationManager.getLastKnownLocation(LocationManager.GPS_PROVIDER);
            if (locationGPS != null) {
                player.setPlayerLocation(locationGPS.getLatitude(), locationGPS.getLongitude());
            } else {
                Toast.makeText(this, "Unable to find location.", Toast.LENGTH_SHORT).show();
            }
        }
    }

    /**
     * Checks if user has enabled GPS or not
     */
    private void OnGPS() {
        final AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setMessage("Enable GPS").setCancelable(false).setPositiveButton("Yes", new  DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                startActivity(new Intent(Settings.ACTION_LOCATION_SOURCE_SETTINGS));
            }
        }).setNegativeButton("No", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                dialog.cancel();
            }
        });
        final AlertDialog alertDialog = builder.create();
        alertDialog.show();
    }
}