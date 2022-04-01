package com.example.superqr;

import android.app.Activity;
import android.content.Context;
import android.graphics.Color;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
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
    ImageView objectImage;
    String hash;

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

        objectImage = view.findViewById(R.id.object_image);
        String link = imageList.get(position);
        String[] isPlaceholder = link.split("-");

        if (imageList != null) {
            if (isPlaceholder[0].equals("placeholder")) { // No photo in FireStorage
                Picasso.get().load(R.drawable.image_placeholder).into(objectImage);
            }
            else { // Has photo in FireStorage
                Picasso.get().load(link).into(objectImage);
            }
        }
        return view;
    }
}
