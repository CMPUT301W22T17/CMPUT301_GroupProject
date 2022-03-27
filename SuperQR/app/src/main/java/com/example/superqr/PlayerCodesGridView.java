package com.example.superqr;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.drawable.Drawable;
import android.net.Uri;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.core.content.res.ResourcesCompat;


import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageException;
import com.google.firebase.storage.StorageReference;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;

public class PlayerCodesGridView extends ArrayAdapter<String> {
    ArrayList<String> imageList;
    String playerID;
    ImageView objectImage;

    public PlayerCodesGridView(@NonNull Context context, ArrayList<String> imageList, String playerID) {
        super(context, 0, imageList);
        this.imageList = imageList;
        this.playerID = playerID;
    }

    @NonNull
    @Override
    public View getView(int position, @Nullable View convertView, @NonNull ViewGroup parent) {
        View view = convertView;
        if (view == null) {
            view = LayoutInflater.from(getContext()).inflate(R.layout.player_codes_content, parent, false);
        }

        objectImage = view.findViewById(R.id.object_image);

        if (imageList != null) {
            if (imageList.get(position).equals("placeholder")) { // No photo in FireStorage
                Picasso.get().load(R.drawable.image_placeholder).into(objectImage);
            }
            else { // Has photo in FireStorage
                Picasso.get().load(imageList.get(position)).into(objectImage);
            }
        }

        view.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                // TODO: Open fragment to comment and be able to delete if owner of QR code
                Toast.makeText(getContext(), imageList.get(position), Toast.LENGTH_SHORT).show(); // Testing
            }
        });

        return view;
    }
}
