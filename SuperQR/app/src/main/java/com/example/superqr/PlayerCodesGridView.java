package com.example.superqr;

import android.app.Activity;
import android.content.Context;
import android.graphics.Color;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;

import com.google.firebase.storage.FirebaseStorage;
import com.squareup.picasso.Picasso;
import java.util.ArrayList;

public class PlayerCodesGridView extends ArrayAdapter<String> {
    ArrayList<String> imageList;
    Player player;
    Player otherPlayer;
    ImageView image;
    TextView score;

    public PlayerCodesGridView(@NonNull Context context, ArrayList<String> imageList, Player player, Player otherPlayer) {
        super(context, 0, imageList);
        this.imageList = imageList;
        this.player = player;
        this.otherPlayer = otherPlayer;
    }

    @NonNull
    @Override
    public View getView(int position, @Nullable View convertView, @NonNull ViewGroup parent) {
        View view = convertView;
        if (view == null) {
            view = LayoutInflater.from(getContext()).inflate(R.layout.player_codes_content, parent, false);
        }

        image = view.findViewById(R.id.object_image);
        score = view.findViewById(R.id.object_score);
        String link = imageList.get(position);
        String[] isPlaceholder = link.split("-");
        String hash, scoreText = "";
        try {
            hash = FirebaseStorage.getInstance().getReferenceFromUrl(link).getName();
        }
        catch (IllegalArgumentException e) {
            hash = isPlaceholder[1];
        }
        ArrayList<QRCode> codes = otherPlayer.getStats().getQrCodes();
        for (int j = 0; j < codes.size(); j++){
            QRCode curCode = codes.get(j);
            if (hash.equals(curCode.getHash())){
                scoreText = Integer.toString(curCode.getScore());
            }
        }

        // Setting image
        if (imageList != null) {
            if (isPlaceholder[0].equals("placeholder")) { // No photo in FireStorage
                Picasso.get().load(R.drawable.image_placeholder).into(image);
            }
            else { // Has photo in FireStorage
                Picasso.get().load(link).into(image);
            }
        }

        score.setText(String.format("Score: %s",scoreText));

        return view;
    }
}
