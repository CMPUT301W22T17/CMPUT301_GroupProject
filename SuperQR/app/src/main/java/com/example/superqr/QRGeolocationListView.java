package com.example.superqr;

import android.content.Context;
import android.location.Location;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import java.util.ArrayList;

public class QRGeolocationListView extends ArrayAdapter<Location> {

    private ArrayList<Location> QRCodes;
    private Context context;

    public QRGeolocationListView(Context context, ArrayList<Location> QRCodes) {
        super(context, 0, QRCodes);
        this.QRCodes = QRCodes;
        this.context = context;
    }

    public View getView(int position, @Nullable View convertView, @NonNull ViewGroup parent) {
        View view = convertView;

        if (view == null){
            view = LayoutInflater.from(context).inflate(R.layout.qr_geolocation_content,parent,false);
        }

        Location codeLocation = QRCodes.get(position);

        TextView codeLatitudeText = view.findViewById(R.id.qr_latitude_text);
        TextView codeLongitudeText = view.findViewById(R.id.qr_longitude_text);

        double codeLatitude = codeLocation.getLatitude();
        double codeLongitude = codeLocation.getLongitude();

        codeLatitudeText.setText(Double.toString(codeLatitude));
        codeLongitudeText.setText(Double.toString(codeLongitude));

        return view;
    }


}
