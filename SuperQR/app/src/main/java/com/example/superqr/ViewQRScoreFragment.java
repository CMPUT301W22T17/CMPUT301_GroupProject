package com.example.superqr;

import android.app.AlertDialog;
import android.app.Dialog;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.core.content.res.ResourcesCompat;
import androidx.fragment.app.DialogFragment;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageException;
import com.google.firebase.storage.StorageReference;

public class ViewQRScoreFragment extends DialogFragment {
    private TextView qrText;
    private ImageView qrImage;
    private ProgressBar loadingImage;
    private QRCode qrCode;
    private StorageReference imageRef;

    public ViewQRScoreFragment(String playerID, QRCode qrCode) {
        this.imageRef = FirebaseStorage.getInstance().getReference().child(playerID).child(qrCode.getHash()); // Gets image
        this.qrCode = qrCode;
    }

    @NonNull
    @Override
    public Dialog onCreateDialog(@Nullable Bundle savedInstanceState) {
        View qr_score_view = LayoutInflater.from(getActivity()).inflate(R.layout.view_qr_score_fragment, null);
        qrText = qr_score_view.findViewById(R.id.qr_score_text);
        qrImage = qr_score_view.findViewById(R.id.qr_image);
        loadingImage = qr_score_view.findViewById(R.id.loading_image);

        qrText.setText(Integer.toString(qrCode.getScore()));
        // https://www.youtube.com/watch?v=7QnhepFaMLM
        // Retrieving image
        imageRef.getBytes(1024*1024) // One MB
                .addOnSuccessListener(new OnSuccessListener<byte[]>() {
                    @Override
                    public void onSuccess(byte[] bytes) {
                        Bitmap bitmap = BitmapFactory.decodeByteArray(bytes, 0, bytes.length);
                        qrImage.setImageBitmap(bitmap);
                        loadingImage.setVisibility(View.GONE);
                    }
                }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                // https://stackoverflow.com/questions/43567626/how-to-check-if-a-file-exists-in-firebase-storage-from-your-android-application
                int errorCode = ((StorageException) e).getErrorCode();
                if (errorCode == StorageException.ERROR_OBJECT_NOT_FOUND) {
                    // https://vectorified.com/download-image#image-placeholder-icon-7.png
                    Drawable placeHolder = ResourcesCompat.getDrawable(getResources(), R.drawable.image_placeholder, null);
                    qrImage.setImageDrawable(placeHolder);
                    loadingImage.setVisibility(View.GONE);
                }
            }
        });


        AlertDialog.Builder builder = new AlertDialog.Builder(getContext());
        return builder
                .setView(qr_score_view)
                .setNegativeButton("BACK", null)
                .create();
    }


}
