package com.example.superqr;

import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.fragment.app.DialogFragment;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentTransaction;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

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
public class LeaderboardFragment extends Fragment implements View.OnClickListener {
    private static final String playerKey= "playerKey";
    private Player player;
    private ListView leaderboardList;
    private ArrayAdapter<Player> playerAdapter;
    private static ArrayList<Player> playersList;
    private static ArrayList<Integer> totalScoreList, totalQRList, highestScoringList;
    private Player obj;
    private Button myRankButton;
    private TextView titleText;
    private TextView rankText;
    private TextView userNameText;
    private TextView scoreText;
    private FirebaseFirestore db;
    private Task<QuerySnapshot> query;
    private List<DocumentSnapshot> x;
    private int myRank, totalQR, highestScoring;

    public LeaderboardFragment() {
        // Required empty public constructor
    }

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @return A new instance of fragment leaderboardFragment.
     */
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
        player = getArguments().getParcelable(playerKey);

        myRankButton = (Button) view.findViewById(R.id.myRank);
        myRankButton.setOnClickListener(this);
        leaderboardList = view.findViewById(R.id.leaderboard_list);
        titleText = view.findViewById(R.id.leaderboard_title);
        rankText = view.findViewById(R.id.rank_title);
        userNameText = view.findViewById(R.id.username_title);
        scoreText = view.findViewById(R.id.score_title);

        playersList = new ArrayList<>();
        totalScoreList = new ArrayList();
        totalQRList = new ArrayList();
        highestScoringList= new ArrayList();

        //From: firebase.google.com
        //At: https://firebase.google.com/docs/database/android/lists-of-data
        //Author: Firebase


        db = FirebaseFirestore.getInstance();
        db.collection("users")
                .get()
                .addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<QuerySnapshot> task) {    //getting all players from the database
                        if(task.isSuccessful()){
                            for (QueryDocumentSnapshot document : task.getResult()){     //getting the different stats into an array at the same time for easy sorting
                                obj = document.toObject(Player.class);
                                playersList.add(obj);
                                totalScoreList.add(obj.getStats().getTotalScore());
                                totalQRList.add(obj.getStats().getCounts());
                                highestScoringList.add(obj.getStats().getHighestScore().getScore());
                            }
                            sortArray();
                        }
                    }
                });
        leaderboardList.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            /**
             * Displays another players profile when you click on them in the leaderboard.
             * @param adapterView
             * @param view
             * @param i
             * @param l
             */
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
                leaderboardList.setVisibility(View.GONE);
                myRankButton.setVisibility(View.GONE);
                titleText.setVisibility(View.GONE);
                rankText.setVisibility(View.GONE);
                userNameText.setVisibility(View.GONE);
                scoreText.setVisibility(View.GONE);
                Player otherPlayer = (Player) adapterView.getItemAtPosition(i);
                DisplaySearchPlayerFragment fragment = DisplaySearchPlayerFragment.newInstance(player, otherPlayer);
                displayFragment(fragment);
            }
        });
        return view;
    }

    /**
     * Sort PlayerList Array and Set The Adapter for the view
     */
    public void sortArray(){
        Collections.sort(playersList);
        playerAdapter = new LeaderboardListView(getActivity(), playersList);
        leaderboardList.setAdapter(playerAdapter);
    }

    /**
     * This runs when the fragment is resumed
     */
    @Override
    public void onResume() {
        super.onResume();
    }

    /**
     * On click listener for the estimate My Rank Button
     * @param view
     */
    @Override
    public void onClick(View view) {
        switch (view.getId())
        {
            case R.id.myRank:
                player = (Player) getArguments().getParcelable(playerKey);
                myRank = findRank(player);
                totalQR = findTotalQR(player);
                highestScoring = findHighest(player);
                DialogFragment myRankFragment = new MyRankFragment(myRank+1,highestScoring+1,totalQR+1);
                myRankFragment.show(getActivity().getSupportFragmentManager(),"my_rank");
        }
    }

    /**
     * Finds the rank that player is.
     * @param player whose rank to search for.
     * @return rank
     */

    //From: BeginnersBook.com
    //At: https://beginnersbook.com/2013/12/sort-arraylist-in-descending-order-in-java/
    //Author: Chaitanya Singh
    public int findRank(Player player){
        int rank;
        Collections.sort(totalScoreList, Collections.reverseOrder());
        rank = totalScoreList.indexOf(player.getStats().getTotalScore());
        Log.d("///ranksize",String.valueOf(player.getStats().getTotalScore()));
        return rank;
    }

    /**
     * Finds the estimate of the totalQR scanned rank
     * @param player whose rank to estimate
     * @return totalqr rank
     */
    public int findTotalQR(Player player){
        int qr;
        Collections.sort(totalQRList, Collections.reverseOrder());
        Log.d("///totalQR",totalQRList.toString());
        qr = totalQRList.indexOf(player.getStats().getCounts());
        return qr;
    }

    /**
     * Finds the estimate for the highest ranking qr code scanned rank
     * @param player whose rank to estimate
     * @return highest scoring qr code rank
     */
    public int findHighest(Player player){
        int highest;
        Collections.sort(highestScoringList, Collections.reverseOrder());
        highest = highestScoringList.indexOf(player.getStats().getHighestScore().getScore());
        return  highest;
    }

    /**
     * Places a fragment on frame_layout in the main activity
     * @param fragment
     */
    public void displayFragment(Fragment fragment) {
        FragmentTransaction fragmentTransaction = getActivity().getSupportFragmentManager().beginTransaction();
        fragmentTransaction.replace(R.id.browse_container, fragment);
        fragmentTransaction.commit();
    }
}