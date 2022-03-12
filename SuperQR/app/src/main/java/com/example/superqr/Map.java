package com.example.superqr;

import android.location.Location;

import com.example.superqr.Player;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.Query;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;

import java.io.Serializable;
import java.lang.reflect.Array;
import java.util.ArrayList;

public class Map implements Serializable {
    protected ArrayList<Location> QRCodeLocations;
    protected Player player;

    /**
     * Initializes a map class by creating a list of QR codes
     */
    public Map() {
        this.QRCodeLocations = new ArrayList<Location>();
    }

    /**
     * Adds the locations of nearby QR codes to the map's database
     * @param QRLocation
     *      Location of QR code to be added to map's list of QR code locations
     */

    public void addAQRLocation(Location QRLocation) {
        this.QRCodeLocations.add(QRLocation);
    }

    /**
     * Adds a group of locations of nearby QR codes to the map's database
     * @param codeCollection
     *      CollectionReference of the QRCodes in the firestore database
     */

    public void addQRLocations(CollectionReference codeCollection) {

        // https://firebase.google.com/docs/firestore/query-data/queries#java_2
        // https://stackoverflow.com/questions/53747054/firebase-get-an-arraylist-field-from-all-documents
        Query locationQuery = codeCollection.whereLessThanOrEqualTo("latitude", player.getPlayerLocation().getLatitude())
                                            .whereLessThanOrEqualTo("longitude", player.getPlayerLocation().getLongitude());
        locationQuery.get()
                     .addOnSuccessListener(new OnSuccessListener<QuerySnapshot>() {
                         @Override
                         public void onSuccess(QuerySnapshot queryDocumentSnapshots) {
                             for (QueryDocumentSnapshot document : queryDocumentSnapshots) {
                                 ArrayList<Location> QRList = (ArrayList<Location>) document.get("location");
                                 for (Location codeLocation : QRList) {
                                     QRCodeLocations.add(codeLocation);
                                 }

                             }
                         }
                     });
    }

    public ArrayList<Location> getQRLocations() {
        return QRCodeLocations;
    }
}
