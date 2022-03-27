package com.example.superqr;

import android.app.Activity;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.GridView;
import android.widget.ProgressBar;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;

import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;

import java.util.ArrayList;

public class ViewPlayerCodesFragment extends Fragment {
    private static final String playerKey = "playerKey";
    GridView codes;
    Player player;
    ArrayList<String> imageList;
    int numCodes;
    StorageReference playerRef;
    ProgressBar loadingImage;
    TextView noQRCodes;
    int i;

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

        codes = root.findViewById(R.id.browse_qr_codes);
        loadingImage = root.findViewById(R.id.loading_image_browse);
        noQRCodes = root.findViewById(R.id.no_qr_codes_text);

        imageList = new ArrayList<>();
        playerRef = FirebaseStorage.getInstance().getReference().child(player.getPlayerID()); // Gets path to playerID
        numCodes = player.getStats().getQrCodes().size();

        // Shows no codes text when player has no QR codes
        if (numCodes == 0) {
            loadingImage.setVisibility(View.GONE);
            noQRCodes.setVisibility(View.VISIBLE);
        }

        for (i = 0; i < numCodes; i++) {
            playerRef.child(player.getStats().getQrCodes().get(i).getHash()).getDownloadUrl().addOnSuccessListener(new OnSuccessListener<Uri>() {
                @Override
                public void onSuccess(Uri uri) {
                    imageList.add(uri.toString());
                }
            }).addOnSuccessListener(new OnSuccessListener<Uri>() {
                @Override
                public void onSuccess(Uri uri) { // Image was found
                    if (imageList.size() == numCodes) {
                        setAdapter();
                    }
                }
            }).addOnFailureListener(new OnFailureListener() {
                @Override
                public void onFailure(@NonNull Exception e) { // No image found
                    imageList.add(String.format("placeholder" + "-" + "%s", player.getStats().getQrCodes().get(i - 1).getHash()));
                    if (imageList.size() == numCodes) {
                        setAdapter();
                    }

                }
            });
        }

        return root;
    }

    /**
     * Sets the list of images to be adapted
     */
    private void setAdapter() {
        PlayerCodesGridView adapter = new PlayerCodesGridView(getContext(), imageList, player);
        codes.setAdapter(adapter);
        loadingImage.setVisibility(View.GONE);
    }

}
