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

public class MyRankFragment extends DialogFragment {
    private TextView totalScoring, highestScoring, totalQR;
    private int totalScore,highestScore,totalQrScanned;



    public MyRankFragment(int totalScore, int highestScore, int totalQrScanned){
        this.totalScore = totalScore;
        this.highestScore = highestScore;
        this.totalQrScanned = totalQrScanned;
    }


    @NonNull
    @Override
    public Dialog onCreateDialog(@Nullable Bundle savedInstanceState) {
        View view_rank_view = LayoutInflater.from(getActivity()).inflate(R.layout.view_rank_fragment,null);
        totalScoring = view_rank_view.findViewById(R.id.totalScoring);
        totalScoring.setText(String.valueOf(totalScore));
        highestScoring = view_rank_view.findViewById(R.id.highestScoring);
        highestScoring.setText(String.valueOf(highestScore));
        totalQR = view_rank_view.findViewById(R.id.totalQR);
        totalQR.setText(String.valueOf(totalQrScanned));

        AlertDialog.Builder builder = new AlertDialog.Builder(getContext());
        return builder
                .setView(view_rank_view)
                .setNegativeButton("BACK",null)
                .create();

    }
}
