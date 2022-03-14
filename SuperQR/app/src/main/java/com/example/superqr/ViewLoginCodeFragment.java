package com.example.superqr;

import android.app.AlertDialog;
import android.app.Dialog;
import android.graphics.Bitmap;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.ImageView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.DialogFragment;

import com.google.zxing.BarcodeFormat;
import com.google.zxing.MultiFormatWriter;
import com.google.zxing.common.BitMatrix;
import com.journeyapps.barcodescanner.BarcodeEncoder;
//import com.journeyapps.barcodescanner.BarcodeEncoder;


public class ViewLoginCodeFragment extends DialogFragment {

    private String username;
    private ImageView loginQRImage;

    public ViewLoginCodeFragment(String username) {
        this.username = username;
    }

    @NonNull
    @Override
    public Dialog onCreateDialog(@Nullable Bundle savedInstanceState) {
        View loginCodeView = LayoutInflater.from(getActivity()).inflate(R.layout.login_qr_code_fragment, null);

        //https://www.youtube.com/watch?v=-IPI93Szwzk
        //https://stackoverflow.com/questions/55909804/duplicate-class-android-support-v4-app-inotificationsidechannel-found-in-modules
        // fixed gradle
        loginQRImage = (ImageView) loginCodeView.findViewById(R.id.loginQRCodeImageView);
        MultiFormatWriter multiFormatWriter = new MultiFormatWriter();
        try {
            //https://stackoverflow.com/questions/36428241/adding-dependency-zxing-android-embedded
            BitMatrix bitMatrix = multiFormatWriter.encode(username, BarcodeFormat.QR_CODE, 300, 300);
            BarcodeEncoder barcodeEncoder = new BarcodeEncoder();
            Bitmap bitmap = barcodeEncoder.createBitmap(bitMatrix);
            loginQRImage.setImageBitmap(bitmap);

        }catch (Exception e){e.printStackTrace();}


        AlertDialog.Builder builder = new AlertDialog.Builder(getContext());
        return builder
                .setView(loginCodeView)
                .setNegativeButton("BACK", null)
                .create();
    }
}
