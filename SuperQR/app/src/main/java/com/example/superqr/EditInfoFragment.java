package com.example.superqr;

import android.app.AlertDialog;
import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.EditText;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.DialogFragment;

public class EditInfoFragment extends DialogFragment {

    private static final String TAG = "EditInfoFragment";

    private EditText editUsernameEditText;
    private EditText editEmailEditText;
    private EditText editPhoneEditText;
    private Player player;
    private OnFragmentInteractionListener listener;

    public EditInfoFragment(Player player) {
        this.player = player;

    }


    public interface OnFragmentInteractionListener {
        public void onOkPressed(String newUsername, String newEmail, String newPhone);
    }


    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        if (context instanceof OnFragmentInteractionListener){
            listener = (OnFragmentInteractionListener) context;
        } else {
            throw new RuntimeException(context.toString()
                    + " must implement OnFragmentInteractionListener");
        }
    }

    @NonNull
    @Override
    public Dialog onCreateDialog(@Nullable Bundle savedInstanceState) {
        View view = LayoutInflater.from(getActivity()).inflate(R.layout.fragment_edit_info, null);


        editUsernameEditText = view.findViewById(R.id.edit_username_editText);
        editEmailEditText = view.findViewById(R.id.edit_email_editText);
        editPhoneEditText = view.findViewById(R.id.edit_phone_editText);

        editUsernameEditText.setText(player.getSettings().getUsername());
        editEmailEditText.setText(player.getSettings().getEmail());
        editPhoneEditText.setText(player.getSettings().getPhone());

        AlertDialog.Builder builder = new AlertDialog.Builder(getContext());
        builder.setView(view);
        builder.setTitle("EDIT INFORMATION");
        builder.setNegativeButton("Cancel", null);

        builder.setPositiveButton("OK", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
                String newUsername = editUsernameEditText.getText().toString();
                String newEmail = editEmailEditText.getText().toString();
                String newPhone = editPhoneEditText.getText().toString();
                listener.onOkPressed(newUsername, newEmail, newPhone);
            }
        });
        return builder.create();
    }
}
