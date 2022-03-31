package com.example.superqr;

import static android.content.ContentValues.TAG;

import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;

/**
 * A simple {@link Fragment} subclass.
 * Use the {@link SearchPlayerFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class SearchPlayerFragment extends Fragment{

    private static final String playerKey = "playerKey";
    private Player player;
    private Player otherPlayer;
    private FirebaseFirestore db;

    private ImageButton searchButton;
    private EditText searchEditText;
    private Button scanPlayerButton;

    public SearchPlayerFragment() {
        // Required empty public constructor
    }


    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @param player
     * @return
     */
    public static SearchPlayerFragment newInstance(Player player) {
        SearchPlayerFragment fragment = new SearchPlayerFragment();
        Bundle bundle = new Bundle();
        bundle.putParcelable(playerKey, player);
        fragment.setArguments(bundle);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        db = FirebaseFirestore.getInstance();
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View profileView = inflater.inflate(R.layout.fragment_search, container, false);

        player = (Player) getArguments().getParcelable(playerKey);

        searchEditText = profileView.findViewById(R.id.search_EditText);
        searchButton = profileView.findViewById(R.id.imageButton);
        scanPlayerButton = profileView.findViewById(R.id.scan_player);

        scanPlayerButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                ScanFragment scanFragment = new ScanFragment().newInstance(player, 2);
                getChildFragmentManager().beginTransaction().replace(R.id.player_profile_container, scanFragment).commit();
                hideViews();
            }
        });

        searchButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String playerName = searchEditText.getText().toString();
                lookupPlayer(playerName);
            }
        });

        return profileView;
    }

    /**
     * hide the views
     */
    private void hideViews() {
        searchEditText.setVisibility(View.INVISIBLE);
        searchButton.setVisibility(View.INVISIBLE);
        scanPlayerButton.setVisibility(View.INVISIBLE);
    }

    /**
     * replace fragments when a player exist in database
     * @param otherPlayer
     */
    private void replaceFragment(Player otherPlayer) {
        hideViews();
        Fragment displaySearchPlayer = DisplaySearchPlayerFragment.newInstance(this.player, otherPlayer);
        FragmentManager fragmentManager = getChildFragmentManager();
        FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();
        fragmentTransaction.replace(R.id.player_profile_container, displaySearchPlayer);
        fragmentTransaction.commit();
    }


    /**
     * check if the player in the database
     * @param name
     */
    private void lookupPlayer(String name) {
        if (name.contains("/")) {
            Toast.makeText(getActivity(), "Player does not exists", Toast.LENGTH_SHORT).show();

        }
        else if (name.equals("")) {
            Toast.makeText(getActivity(), "Please enter a name.", Toast.LENGTH_SHORT).show();
        }
        else {
            DocumentReference docRef = db.collection("users").document(name);
            docRef.get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
                @Override
                public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                    Log.d(TAG, "onComplete: executing");
                    if (task.isSuccessful()) {
                        DocumentSnapshot document = task.getResult();
                        if (document.exists()) {
                            Log.d(TAG, "onComplete: data does exist");
                            otherPlayer = document.toObject(Player.class);
                            // go to profile view
                            replaceFragment(otherPlayer);
                        }
                        else {
                            Toast.makeText(getActivity(), "Player does not exists", Toast.LENGTH_SHORT).show();
                            Log.d(TAG, "onComplete: data not exist");
                        }
                    }
                    else {
                        Toast.makeText(getActivity(), "Player does not exists", Toast.LENGTH_SHORT).show();
                        Log.d(TAG, "get failed with", task.getException());
                    }
                }
            });
        }
    }

}
