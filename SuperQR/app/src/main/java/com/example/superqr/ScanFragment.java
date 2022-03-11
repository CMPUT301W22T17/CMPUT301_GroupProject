package com.example.superqr;

import android.Manifest;
import android.app.Activity;
import android.content.pm.PackageManager;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.Fragment;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import android.widget.TextView;
import android.widget.Toast;

import com.budiyev.android.codescanner.CodeScanner;
import com.budiyev.android.codescanner.CodeScannerView;
import com.budiyev.android.codescanner.DecodeCallback;
import com.google.zxing.Result;
import com.karumi.dexter.Dexter;
import com.karumi.dexter.PermissionToken;
import com.karumi.dexter.listener.PermissionDeniedResponse;
import com.karumi.dexter.listener.PermissionGrantedResponse;
import com.karumi.dexter.listener.PermissionRequest;
import com.karumi.dexter.listener.single.PermissionListener;

import java.util.Scanner;


/**
 * A simple {@link Fragment} subclass.
 * Use the {@link ScanFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class ScanFragment extends Fragment {
    //initialize variables and key used to pass through
    private static final String playerKey = "playerKey";
    private Player player;
    private CodeScanner codeScanner;


    public ScanFragment() {
        // Required empty public constructor
    }

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @param player current player of the game
     * @return A new instance of fragment ScanFragment.
     */
    // TODO: Rename and change types and number of parameters
    public static ScanFragment newInstance(Player player) {
        ScanFragment fragment = new ScanFragment();
        Bundle bundle = new Bundle();
        bundle.putParcelable(playerKey, player);
        fragment.setArguments(bundle);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        player = (Player) getArguments().getParcelable(playerKey);
        final Activity activity = getActivity();
        View root = inflater.inflate(R.layout.fragment_scan, container, false);
        CodeScannerView codeScannerView = root.findViewById(R.id.scanner_view);
        TextView testTextView = root.findViewById(R.id.qr_scan_testing);
        codeScanner = new CodeScanner(activity, codeScannerView);
        codeScanner.setDecodeCallback(new DecodeCallback() {
            @Override
            public void onDecoded(@NonNull Result result) {
                activity.runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        // Testing to see the string of the scanned QR code
                        Toast.makeText(activity, result.getText(), Toast.LENGTH_SHORT).show();
                    }
                });
            }
        });
        codeScannerView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                codeScanner.startPreview();
            }
        });
        return root;
    }

    @Override
    public void onResume() {
        super.onResume();
        requestForCamera();
    }

    // Asks user for camera permissions
    private void requestForCamera() {
        final Activity activity = getActivity();
        Dexter.withContext(activity).withPermission(Manifest.permission.CAMERA)
                .withListener(new PermissionListener() {
            @Override
            public void onPermissionGranted(PermissionGrantedResponse permissionGrantedResponse) {
                codeScanner.startPreview();
            }

            @Override
            public void onPermissionDenied(PermissionDeniedResponse permissionDeniedResponse) {
                // Bugs when declining permission, need to fix
                Toast.makeText(activity, "Camera Permission is Required",
                        Toast.LENGTH_SHORT).show();
            }

            @Override
            public void onPermissionRationaleShouldBeShown(PermissionRequest permissionRequest,
                                                           PermissionToken permissionToken) {
                permissionToken.continuePermissionRequest();
            }
        }).check();
    }

    @Override
    public void onPause() {
        codeScanner.releaseResources();
        super.onPause();
    }
}