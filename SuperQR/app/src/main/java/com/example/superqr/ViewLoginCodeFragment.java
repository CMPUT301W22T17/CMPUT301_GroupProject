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

/**
 * This is a DialogFragment for user to view their profile QR code
 * The QR code is placed in a image view, and it is created using
 * bit map. This is adapted from zxing library.
 * There is a "Back" button for user to click,
 * when clicked, user will be brought back to main activity
 */
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

        // fixed gradle
        /* From: youtube.com
         * At: https://www.youtube.com/watch?v=-IPI93Szwzk
         * Author: zainsoft https://www.youtube.com/channel/UCzE5gg5Z3wivPBatj8-doig
         *
         * From: stackoverflow.com
         * At: https://stackoverflow.com/questions/55909804/duplicate-class-android-support-v4-app-inotificationsidechannel-found-in-modules
         * Author: Anice Jahanjoo https://stackoverflow.com/users/5282127/anice-jahanjoo
         */
        loginQRImage = (ImageView) loginCodeView.findViewById(R.id.loginQRCodeImageView);
        MultiFormatWriter multiFormatWriter = new MultiFormatWriter();
        try {
            /* From: stackoverflow.com
             * At: https://stackoverflow.com/questions/36428241/adding-dependency-zxing-android-embedded
             * Author: Joanne Dela Cruz https://stackoverflow.com/users/12377369/joanne-dela-cruz
             */
            BitMatrix bitMatrix = multiFormatWriter.encode(username, BarcodeFormat.QR_CODE, 300, 300);
            BarcodeEncoder barcodeEncoder = new BarcodeEncoder();
            Bitmap bitmap = barcodeEncoder.createBitmap(bitMatrix);
            loginQRImage.setImageBitmap(bitmap);

        }
        catch (Exception e){e.printStackTrace();}

        AlertDialog.Builder builder = new AlertDialog.Builder(getContext());
        return builder
                .setView(loginCodeView)
                .setNegativeButton("BACK", null)
                .create();
    }
}
