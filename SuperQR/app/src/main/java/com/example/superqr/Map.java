package com.example.superqr;

import android.location.Location;

import androidx.annotation.NonNull;

import com.example.superqr.Player;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.Query;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;

import java.io.Serializable;
import java.lang.reflect.Array;
import java.util.ArrayList;

public class Map implements Serializable {
    protected ArrayList<LocationStore> QRCodeLocations;
    protected Player player;

    /**
     * Initializes a map class by creating a list of QR codes
     */
    public Map() {
        this.QRCodeLocations = new ArrayList<LocationStore>();
    }

    public Map(Player player) {
        this.QRCodeLocations = new ArrayList<LocationStore>();
        this.player = player;
    }

    /**
     * Adds a group of locations of nearby QR codes to the map's database
     */

    public void addQRLocations() {
        // https://firebase.google.com/docs/firestore/query-data/queries#java_2
        // https://stackoverflow.com/questions/53747054/firebase-get-an-arraylist-field-from-all-documents

        // get all QRCodes from database

        FirebaseFirestore db = FirebaseFirestore.getInstance();

        ArrayList<QRCode> QRCodes = new ArrayList<>();
        db.collection("qrcodes")
                .get()
                .addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<QuerySnapshot> task) {
                        if(task.isSuccessful()) {
                            for (QueryDocumentSnapshot document : task.getResult()) {
                                QRCode code = document.toObject(QRCode.class);
                                QRCodes.add(code);
                            }
                        }
                    }
                });

        for (QRCode code : QRCodes) {
            if ((Math.abs(code.getStoreLocation().getLatitude() - player.getPlayerLocation().getLatitude()) < 0.5) &&
                    (Math.abs(code.getStoreLocation().getLongitude() - player.getPlayerLocation().getLongitude()) < 0.5)) {
                QRCodeLocations.add(code.getStoreLocation());
            }
        }
    }

    public ArrayList<LocationStore> getQRLocations() {
        return QRCodeLocations;
    }
}
