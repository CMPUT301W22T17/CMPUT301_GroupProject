package com.example.superqr;

import android.location.Location;
import android.util.Log;

import androidx.annotation.NonNull;

import com.example.superqr.Player;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
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
import java.util.List;

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

    public ArrayList<LocationStore> getQRCodeLocations(){
        // https://firebase.google.com/docs/firestore/query-data/queries#java_2
        // https://stackoverflow.com/questions/53747054/firebase-get-an-arraylist-field-from-all-documents
        // https://stackoverflow.com/questions/46706433/firebase-firestore-get-data-from-collection

        FirebaseFirestore db = FirebaseFirestore.getInstance();
        ArrayList<LocationStore> codesList = new ArrayList<>();
        db.collection("codes")
                .get()
                .addOnSuccessListener(new OnSuccessListener<QuerySnapshot>() {
                    @Override
                    public void onSuccess(QuerySnapshot queryDocumentSnapshots) {
                        if (!queryDocumentSnapshots.isEmpty()) {
                            for (QueryDocumentSnapshot document : queryDocumentSnapshots) {
                                QRCode code = document.toObject(QRCode.class);
                                codesList.add(code.getLocation());

                            }
                        }
                    }
                });
        return codesList;
    }


    public ArrayList<LocationStore> getQRLocations() {
        return QRCodeLocations;
    }
}
