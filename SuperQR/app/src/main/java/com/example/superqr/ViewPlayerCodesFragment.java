package com.example.superqr;

import android.app.Activity;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.GridView;

import androidx.annotation.NonNull;
import androidx.core.content.res.ResourcesCompat;
import androidx.fragment.app.Fragment;

import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageException;
import com.google.firebase.storage.StorageReference;

import java.util.ArrayList;

public class ViewPlayerCodesFragment extends Fragment {
    private static final String playerKey = "playerKey";
    GridView codes;
    Player player;
    ArrayList<QRCode> playerQRCodes;
    FirebaseFirestore db;
    StorageReference playerRef;
    ArrayList<Bitmap> bitmaps;

    public ViewPlayerCodesFragment() {
        // Required empty public constructor
    }

    /**
     *
     * @param player
     *      Current player of the game
     * @return
     *      A new instance of ViewPlayerCodes Fragment
     */
    public static ViewPlayerCodesFragment newInstance(Player player) {
        ViewPlayerCodesFragment fragment = new ViewPlayerCodesFragment();
        Bundle bundle = new Bundle();
        bundle.putParcelable(playerKey, player);
        fragment.setArguments(bundle);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        player = (Player) getArguments().getParcelable(playerKey);
        final Activity activity = getActivity();
        View root = inflater.inflate(R.layout.fragment_player_codes, container, false);

        playerQRCodes = new ArrayList<>();
        bitmaps = new ArrayList<>();
        codes = root.findViewById(R.id.browse_qr_codes);
        playerQRCodes = player.getStats().getQrCodes();

        playerRef = FirebaseStorage.getInstance().getReference().child(player.getPlayerID()); // Gets path to playerID

        // https://www.youtube.com/watch?v=7QnhepFaMLM
        // Retrieving image
        for (int i = 0; i < playerQRCodes.size(); i++) {
            String hash = playerQRCodes.get(i).getHash();
            playerRef.child(hash).getBytes(1024 * 1024) // One MB
                    .addOnSuccessListener(new OnSuccessListener<byte[]>() {
                        @Override
                        public void onSuccess(byte[] bytes) {
                            Bitmap bitmap = BitmapFactory.decodeByteArray(bytes, 0, bytes.length);
                            bitmaps.add(bitmap);
                        }
                    }).addOnFailureListener(new OnFailureListener() {
                        @Override
                        public void onFailure(@NonNull Exception e) {
                            Log.d("debug", "FAILURESE");
                            // https://stackoverflow.com/questions/43567626/how-to-check-if-a-file-exists-in-firebase-storage-from-your-android-application
                            int errorCode = ((StorageException) e).getErrorCode();
                            if (errorCode == StorageException.ERROR_OBJECT_NOT_FOUND) {
                                // https://vectorified.com/download-image#image-placeholder-icon-7.png
                                Drawable placeHolder = ResourcesCompat.getDrawable(getContext().getResources(), R.drawable.image_placeholder, null);
                            }
                        }
                    });
        }
        Log.d("debug", "HELLO");
        PlayerCodesListView adapter = new PlayerCodesListView(activity, playerQRCodes, bitmaps);
        codes.setAdapter(adapter);


        return root;
    }

}
