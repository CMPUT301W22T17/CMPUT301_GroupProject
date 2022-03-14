package com.example.superqr;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

public class PlayerProfileActivity extends AppCompatActivity {

    Player player;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_player_profile);

        setUserInfo();
        setQRInfo();

        final Button highQRScoreButton = findViewById(R.id.view_high_score_button);
        highQRScoreButton.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                // the 2 is a test number - need player's actual data to replace
                // need to pass in actual qr picture
                new ViewQRScoreFragment(player.getStats().getHighestScore()).show(getSupportFragmentManager(), "VIEW_HIGH_QR");
            }
        });

        final Button lowQRScoreButton = findViewById(R.id.view_low_score_button);
        lowQRScoreButton.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                // the 1 is a test number - need player's actual data to replace
                // need to pass in actual qr picture
                new ViewQRScoreFragment(player.getStats().getLowestScore()).show(getSupportFragmentManager(), "VIEW_LOW_QR");
            }
        });
    }

    public void setUserInfo() {
        TextView usernameText = findViewById(R.id.player_username);
        TextView emailText = findViewById(R.id.player_email);
        TextView phoneText = findViewById(R.id.player_phone);

        /* implement later
        usernameText.setText(player.getUsername());
        emailText.setText(player.getEmail());
        phoneText.setText(player.getPhone());
         */
    }

    public void setQRInfo() {
        TextView totalScannedText = findViewById(R.id.total_qr_scanned);
        TextView totalScoreText = findViewById(R.id.total_qr_score);

        /* implement later
        totalScannedText.setText(player.getTotalScanned());
        totalScoreText.setText(player.getTotalScore());
         */
    }

}