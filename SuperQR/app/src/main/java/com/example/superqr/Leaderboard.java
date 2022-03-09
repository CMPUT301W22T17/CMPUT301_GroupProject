package com.example.superqr;

import android.os.Bundle;
import android.os.PersistableBundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import android.app.Fragment;
import java.lang.reflect.Array;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.Collections;

/**
 * This class will host a Leaderboard of the global game which will be stored on firestore
 */
public class Leaderboard extends Fragment {
    // make an online fire store database to host leaderboards
    private ListView leaderboardList;
    private ArrayAdapter<Player> playerAdapter;
    private ArrayList<Player> playerList;
    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";


    public Leaderboard(){

    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        View view = LayoutInflater.from(getActivity()).inflate(R.layout.leaderboardview,null);
        leaderboardList = view.findViewById(R.id.leaderboard_list);
        playerList = new ArrayList<>();

        playerList.add(new Player("Bri",450));
        playerList.add(new Player("Chris",650));
        playerList.add(new Player("Rob",250));
        playerList.add(new Player("Scarlet",500));


        Collections.sort(playerList);
        playerAdapter = new LeaderboardListView(getActivity(), playerList);
        leaderboardList.setAdapter(playerAdapter);

    }

    public static Leaderboard newInstance(String param1, String param2) {
        Leaderboard fragment = new Leaderboard();
        Bundle args = new Bundle();
        args.putString(ARG_PARAM1, param1);
        args.putString(ARG_PARAM2, param2);
        fragment.setArguments(args);
        return fragment;
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater,  ViewGroup container,  Bundle savedInstanceState) {
        return inflater.inflate(R.layout.leaderboardview, container, false);
    }
}
