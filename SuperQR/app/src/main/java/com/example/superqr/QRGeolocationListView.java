package com.example.superqr;

import android.content.Context;
import android.location.Location;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;

import java.util.ArrayList;

public class QRGeolocationListView extends ArrayAdapter<LocationStore> {


    public QRGeolocationListView(Context context, ArrayList<LocationStore> QRCodes) {
        super(context, 0, QRCodes);

    }

    public View getView(int position, @Nullable View convertView, @NonNull ViewGroup parent) {
        View view = convertView;

        if (view == null){
            view = LayoutInflater.from(getContext()).inflate(R.layout.qr_geolocation_content,parent,false);
        }

        LocationStore codeLocation = getItem(position);
        TextView codeLatitudeText = view.findViewById(R.id.qr_latitude_text);
        TextView codeLongitudeText = view.findViewById(R.id.qr_longitude_text);

        double codeLatitude = codeLocation.getLatitude();
        double codeLongitude = codeLocation.getLongitude();

        codeLatitudeText.setText(Double.toString(codeLatitude));
        codeLongitudeText.setText(Double.toString(codeLongitude));

        return view;
    }


}
