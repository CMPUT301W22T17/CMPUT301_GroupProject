package com.example.superqr;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import java.util.ArrayList;

/**
 * Custom List view adapter for leaderboard
 */
public class LeaderboardListView extends ArrayAdapter<Player> {
    private ArrayList<Player> players;
    private Context context;
    private TextView userId;
    private TextView score;
    private TextView Rank;

    /**
     * Class constructor
     * @param context
     * @param players array list containing players
     */
    public LeaderboardListView(Context context, ArrayList<Player> players){
        super(context,0,players);
        this.players = players;
        this.context = context;
    }

    /**
     * Creates the custom adapter to display the ranks
     * @param position Rank of a player
     * @return view of the array adapter
     */
    @NonNull
    @Override
    public View getView(int position, @Nullable View convertView, @NonNull ViewGroup parent) {
        View view = convertView;
        if (view ==null){
            view = LayoutInflater.from(context).inflate(R.layout.leaderboard_content, parent, false);
        }
        Player player = players.get(position);
        userId = view.findViewById(R.id.userId);
        score = view.findViewById(R.id.score);
        Rank = view.findViewById(R.id.Rank);
        Rank.setText(String.valueOf(position+1));
        userId.setText(player.getSettings().getUsername());
        score.setText(String.valueOf(player.getStats().getTotalScore()));
        return view;
    }
}