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

    public LeaderboardListView(Context context, ArrayList<Player> players){
        super(context,0,players);
        this.players = players;
        this.context = context;

    }

    @NonNull
    @Override
    public View getView(int position, @Nullable View convertView, @NonNull ViewGroup parent) {
        View view = convertView;
        if (view ==null){
            view = LayoutInflater.from(context).inflate(R.layout.leaderboard_content, parent, false);
        }
        Player player = players.get(position);
        TextView userId = view.findViewById(R.id.userId);
        TextView score = view.findViewById(R.id.score);

        userId.setText(player.getName());
        score.setText(String.valueOf(player.getScore()));
        return view;
    }
}
