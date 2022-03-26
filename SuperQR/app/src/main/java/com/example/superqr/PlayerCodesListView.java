package com.example.superqr;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.drawable.Drawable;
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

import java.util.ArrayList;

public class PlayerCodesListView extends ArrayAdapter<QRCode> {
    QRCode qrCode;
    String playerID;
    ImageView objectImage;
    StorageReference playerRef;
    TextView textView;
    Bitmap bitmap;
    ProgressBar loadingImage;
    ArrayList<Bitmap> bitmaps;

    public PlayerCodesListView(@NonNull Context context, ArrayList<QRCode> qrCodes, ArrayList<Bitmap> bitmaps) {
        super(context, 0, qrCodes);
        this.bitmaps = bitmaps;
    }

    @NonNull
    @Override
    public View getView(int position, @Nullable View convertView, @NonNull ViewGroup parent) {
        View view = convertView;
        if (view == null) {
            view = LayoutInflater.from(getContext()).inflate(R.layout.player_codes_content, parent, false);
        }

        qrCode = getItem(position);
        objectImage = view.findViewById(R.id.idIVimage);
        textView = view.findViewById(R.id.idTVtext);
        objectImage.setImageBitmap(bitmaps.get(position));
        textView.setText(qrCode.getHash());
        loadingImage = view.findViewById(R.id.loading_image_browse);


        return view;
    }

    private void setViews(Bitmap bitmap) {
        Drawable placeHolder = ResourcesCompat.getDrawable(getContext().getResources(), R.drawable.image_placeholder, null);
        textView.setText(qrCode.getHash());
        objectImage.setImageDrawable(placeHolder);
        objectImage.setImageBitmap(Bitmap.createScaledBitmap(bitmap, 150, 150, false));
    }
}
