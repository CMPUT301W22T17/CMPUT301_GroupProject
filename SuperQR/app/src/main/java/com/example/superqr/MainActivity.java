package com.example.superqr;


import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.preference.PreferenceManager;
import com.example.superqr.databinding.ActivityMainBinding;

public class MainActivity extends AppCompatActivity {

    private ActivityMainBinding binding;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        checkNewUser();

        // All fragments are launched from this main activity.
        // When clicking on the navigation buttons, we open a new fragment to display
        binding = ActivityMainBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        //we make the default "home" screen profile
        replaceFragment(new ProfileFragment());

        //https://www.youtube.com/watch?v=Bb8SgfI4Cm4
        binding.bottomNav.setOnItemSelectedListener(menuItem -> {
            switch (menuItem.getItemId()) {
                case R.id.profile:
                    replaceFragment(new ProfileFragment());
                    break;
                case R.id.map:
                    replaceFragment(new MapFragment());
                    break;
                case R.id.scan:
                    replaceFragment(new ScanFragment());
                    break;
                case R.id.browse:
                    replaceFragment(new BrowseFragment());
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


    // TODO: ACTUALLY IMPLEMENT PROPERLY
    public void checkNewUser(){
        SharedPreferences sharedPrefs = PreferenceManager.getDefaultSharedPreferences(MainActivity.this);
        //https://stackoverflow.com/questions/35681693/checking-if-shared-preferences-exist
        boolean ranBefore = sharedPrefs.getBoolean("ranBefore", false);
        if (!ranBefore) {
            SharedPreferences.Editor editor = sharedPrefs.edit();
            editor.putBoolean("ranBefore", true);
            editor.commit();
            Intent intent = new Intent(MainActivity.this, LogInActivity.class);
            startActivity(intent);
        }

    }

}