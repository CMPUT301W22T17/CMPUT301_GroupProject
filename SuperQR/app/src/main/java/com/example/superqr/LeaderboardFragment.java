package com.example.superqr;

import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ListView;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

/**
 * A simple {@link Fragment} subclass.
 * Use the {@link LeaderboardFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class LeaderboardFragment extends Fragment {


    private static final String playerKey= "playerKey";
    private Player player;
    private ListView leaderboardList;
    private ArrayAdapter<Player> playerAdapter;
    private static ArrayList<Player> playersList;
    private Player obj;
    FirebaseFirestore db;
    Task<QuerySnapshot> query;
    List<DocumentSnapshot> x;
    public LeaderboardFragment() {
        // Required empty public constructor
    }

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     *
     * @return A new instance of fragment leaderboardFragment.
     */
    // TODO: Rename and change types and number of parameters
    public static LeaderboardFragment newInstance(Player player) {
        LeaderboardFragment fragment = new LeaderboardFragment();
        Bundle bundle = new Bundle();
        bundle.putParcelable(playerKey, player);
        fragment.setArguments(bundle);
        return fragment;
    }


    /**
     * Runs on the creation of the leaderboard fragment
     */
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }


    /**
     * Gets Players from the Database and creates the View for the Leaderboard Fragment
     * @return View of the leaderboard
     */
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = LayoutInflater.from(getActivity()).inflate(R.layout.fragment_leaderboard,null);

        player = (Player) getArguments().getParcelable(playerKey);

        leaderboardList = view.findViewById(R.id.leaderboard_list);
        playersList = new ArrayList<>();
        db = FirebaseFirestore.getInstance();
        db.collection("users")
                .get()
                .addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<QuerySnapshot> task) {
                        if(task.isSuccessful()){
                            for (QueryDocumentSnapshot document : task.getResult()){
                                obj = document.toObject(Player.class);
                                playersList.add(obj);
                            }
                            SortArray();
                        }
                    }
                });
        return view;
    }

    /**
     * Sort PlayerList Array and Set The Adapter for the view
     */
    public void SortArray(){
        Collections.sort(playersList);
        playerAdapter = new LeaderboardListView(getActivity(), playersList);
        leaderboardList.setAdapter(playerAdapter);
    }


}