package com.example.superqr;

import static android.content.ContentValues.TAG;
import static android.content.Context.MODE_PRIVATE;

import android.content.SharedPreferences;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.fragment.app.DialogFragment;

import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;

/**
 * A simple {@link Fragment} subclass.
 * Use the {@link ProfileFragment#newInstance} factory method to
 * create an instance of this fragment.
 */

public class ProfileFragment extends Fragment implements View.OnClickListener {

    //initialize variables, and key used to pass through
    private static final String playerKey = "playerKey";
    private Player player;
    private Button qrHighScoreButton;
    private Button qrLowScoreButton;
    private Button editInfoButton;
    private FirebaseFirestore db;
    public ProfileFragment() {
        // Required empty public constructor
    }

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @param player current player of the game
     * @return A new instance of fragment ProfileFragment.
     */
    public static ProfileFragment newInstance(Player player) {
        ProfileFragment fragment = new ProfileFragment();
        Bundle bundle = new Bundle();
        bundle.putParcelable(playerKey, player);
        fragment.setArguments(bundle);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        db = FirebaseFirestore.getInstance();

    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View profileView = inflater.inflate(R.layout.activity_player_profile, container, false);
        qrHighScoreButton = (Button) profileView.findViewById(R.id.view_high_score_button);
        qrHighScoreButton.setOnClickListener(this);

        qrLowScoreButton = (Button) profileView.findViewById(R.id.view_low_score_button);
        qrLowScoreButton.setOnClickListener(this);
        editInfoButton = (Button) profileView.findViewById(R.id.edit_player_info_button);
        editInfoButton.setOnClickListener(this);

        player = (Player) getArguments().getParcelable(playerKey);

        // https://stackoverflow.com/questions/9931993/passing-an-object-from-an-activity-to-a-fragment
        // get the object from Main
        setUserInfo(profileView);
        setQRInfo(profileView);

        return profileView;
    }

    // https://stackoverflow.com/questions/11857022/fragment-implements-onclicklistener
    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.view_high_score_button:
                // replace 2 with the high qr score player has
                // https://stackoverflow.com/questions/25887373/calling-dialogfragment-from-fragment-not-fragmentactivity
                DialogFragment highQRScoreFragment = new ViewQRScoreFragment("2");
                highQRScoreFragment.show(getActivity().getSupportFragmentManager(), "high_qr_dialog");
                break;

            case R.id.view_low_score_button:
                // replace 1 with the low qr score player has
                DialogFragment lowQRScoreFragment = new ViewQRScoreFragment("1");
                lowQRScoreFragment.show(getActivity().getSupportFragmentManager(), "high_qr_dialog");
                break;
            case R.id.edit_player_info_button:
                editInfo();
                break;
        }
    }

    private void editInfo() {
        EditInfoFragment editInfoFragment = new EditInfoFragment(player);
        editInfoFragment.show(getActivity().getSupportFragmentManager(), "edit info");
    }

    /**
     * Displays the player's personal information
     */
    public void setUserInfo(View view) {
        // https://stackoverflow.com/questions/6495898/findviewbyid-in-fragment
        TextView usernameText = view.findViewById(R.id.player_username);
        TextView emailText = view.findViewById(R.id.player_email);
        TextView phoneText = view.findViewById(R.id.player_phone);

        /* implement later
        usernameText.setText("player's username");
        emailText.setText("player's email");
        phoneText.setText("player's phone number");
         */

        String userName = player.getSettings().getUsername();
        String email = player.getSettings().getEmail();
        String phone = player.getSettings().getPhone();

        usernameText.setText(userName);
        emailText.setText(email);
        phoneText.setText(phone);

    }

    /**
     * Displays the player's QR stats about total scanned and total score for their QR codes
     */
    public void setQRInfo(View view) {
        TextView totalScannedText = getActivity().findViewById(R.id.total_qr_scanned);
        TextView totalScoreText = getActivity().findViewById(R.id.total_qr_score);

        /* implement later
        totalScannedText.setText("player's total scanned");
        totalScoreText.setText("player's total score");
         */
    }

    /*@Override
    public void onOkPressed(String newUsername, String newEmail, String newPhone) {
        if (player.getSettings().getUsername() == newUsername || newUsername.isEmpty()) {
            player.getSettings().setEmail(newEmail);
            player.getSettings().setPhone(newPhone);
            db.collection("users").document()
                    .update(
                            "settings.email", newEmail,
                            "settings.phone", newPhone
                    );
            PlayerSettings ps = player.getSettings();
            ps.setEmail(newEmail);
            ps.setPhone(newPhone);
            player.setSettings(ps);
            Toast.makeText(getActivity(), "Successful Update...", Toast.LENGTH_LONG).show();
        }
        else {
            DocumentReference docRef = db.collection("users").document(newUsername);
            docRef.get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
                @Override
                public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                    Log.d(TAG, "onComplete: executing");
                    if (task.isSuccessful()) {
                        DocumentSnapshot document = task.getResult();
                        if (document.exists()) {
                            Log.d(TAG, "onComplete: data does exist");
                            // do not add
                            Toast.makeText(getActivity(), "Unsuccessful Update. Username already exists...", Toast.LENGTH_LONG).show();
                        } else {
                            // should handle this
                            // rename and add to database
                            Log.d(TAG, "onComplete: data not exist");
                            DocumentReference docRefOldName = db.collection("users")
                                    .document(player.getSettings().getUsername());
                            docRefOldName.delete().addOnSuccessListener(new OnSuccessListener<Void>() {
                                @Override
                                public void onSuccess(Void unused) {
                                    Log.d(TAG, "DocumentSnapShot successfuly deleted");
                                }
                            });
                            PlayerSettings ps = player.getSettings();
                            ps.setUsername(newUsername);
                            ps.setEmail(newEmail);
                            ps.setPhone(newPhone);
                            player.setSettings(ps);
                            db.collection("users").document().set(player);
                            SharedPreferences sharedPreferences = getActivity().getSharedPreferences("shared preferences", MODE_PRIVATE);
                            SharedPreferences.Editor editor = sharedPreferences.edit();
                            editor.putString("user", newUsername);
                            editor.apply();
                        }
                    } else {
                        Log.d(TAG, "get failed with", task.getException());
                    }
                }
            });
        }
    }*/
}