package com.example.superqr;

import android.app.AlertDialog;
import android.app.Dialog;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.DialogFragment;

public class ViewQRScoreFragment extends DialogFragment {

    private TextView qrText;
    private int qrScore;

    public ViewQRScoreFragment(int score) {

        this.qrScore = score;
    }

    @NonNull
    @Override
    public Dialog onCreateDialog(@Nullable Bundle savedInstanceState) {
        View qr_score_view = LayoutInflater.from(getActivity()).inflate(R.layout.view_qr_score_fragment, null);
        qrText = qr_score_view.findViewById(R.id.qr_score_text);

        qrText.setText(Integer.toString(qrScore));

        // still need to figure out how to place in picture

        AlertDialog.Builder builder = new AlertDialog.Builder(getContext());
        return builder
                .setView(qr_score_view)
                .setNegativeButton("BACK", null)
                .create();
    }


}
