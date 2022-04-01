package com.example.superqr;

import static android.content.ContentValues.TAG;

import android.content.Context;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.InputMethodManager;
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

public class SearchPlayerFragment extends Fragment {

    private static final String playerKey = "playerKey";
    private Player player;
    private FirebaseFirestore db;

    private ImageButton searchButton;
    private EditText searchEditText;

    public SearchPlayerFragment() {
        // Required empty public constructor
    }


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

        searchButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String playerName = searchEditText.getText().toString();
                InputMethodManager imm = (InputMethodManager)getActivity().getSystemService(Context.INPUT_METHOD_SERVICE);
                imm.hideSoftInputFromWindow(view.getWindowToken(), 0);
                if (playerName.equals("")) {
                    Toast.makeText(getActivity(), "Please enter a name.", Toast.LENGTH_SHORT).show();
                }
                else {
                    DocumentReference docRef = db.collection("users").document(playerName);
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
        });

        return profileView;
    }

    private void hideViews() {
        searchEditText.setVisibility(View.INVISIBLE);
        searchButton.setVisibility(View.INVISIBLE);
    }

    private void replaceFragment(Player player) {
        hideViews();
        Fragment displaySearchPlayer = DisplaySearchPlayerFragment.newInstance(player);
        FragmentManager fragmentManager = getChildFragmentManager();
        FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();
        fragmentTransaction.replace(R.id.player_profile_container, displaySearchPlayer);
        fragmentTransaction.addToBackStack(null);
        fragmentTransaction.commit();
    }

}
