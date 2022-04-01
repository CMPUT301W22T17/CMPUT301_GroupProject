package com.example.superqr;

import android.Manifest;
import android.app.Activity;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.provider.Settings;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;

import com.budiyev.android.codescanner.CodeScanner;
import com.budiyev.android.codescanner.CodeScannerView;
import com.budiyev.android.codescanner.DecodeCallback;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.Query;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;
import com.google.zxing.Result;
import com.karumi.dexter.Dexter;
import com.karumi.dexter.MultiplePermissionsReport;
import com.karumi.dexter.PermissionToken;
import com.karumi.dexter.listener.DexterError;
import com.karumi.dexter.listener.PermissionRequest;
import com.karumi.dexter.listener.PermissionRequestErrorListener;
import com.karumi.dexter.listener.multi.MultiplePermissionsListener;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;


/**
 * A simple {@link Fragment} subclass.
 * Use the {@link ScanFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class ScanFragment extends Fragment {
    // repositories:
    // https://github.com/yuriy-budiyev/code-scanner
    // https://github.com/Karumi/Dexter
    //initialize variables and key used to pass through
    private static final String playerKey = "playerKey";
    private final int REQUEST_IMAGE_CAPTURE = 1;
    private Player player;
    private CodeScanner codeScanner;
    private boolean cameraDenied; // permission permanently denied
    private ScanFragmentListener listener;
    private ScanFragmentListener1 listener1;
    private int scanAction;
    private QRCode qrCode;
    private FirebaseFirestore db;

    // https://stackoverflow.com/questions/35091857/passing-object-from-fragment-to-activity
    public interface ScanFragmentListener {
        void onQRScanned(QRCode qrCode, boolean geo);
    }

    public interface ScanFragmentListener1 {
        void onQRScanned1(String username);
    }

    public ScanFragment() {
        // Required empty public constructor
    }

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @param player
     *      Current player of the game
     * @param scanAction
     *      An integer indicating what the scanner is being used for.
     *      0: Scanning QR code for points
     *      1: Scanning QR code to login
     *      2: Scanning QR code to see profile
     *
     * @return
     *      A new instance of ScanFragment.
     */
    public static ScanFragment newInstance(Player player, int scanAction) {
        ScanFragment fragment = new ScanFragment();
        Bundle bundle = new Bundle();
        bundle.putParcelable(playerKey, player);
        bundle.putInt("scanAction", scanAction);
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
        // https://www.youtube.com/watch?v=Iuj4CuWjYF8
        player = (Player) getArguments().getParcelable(playerKey);
        scanAction = getArguments().getInt("scanAction");
        final Activity activity = getActivity();
        View root = inflater.inflate(R.layout.fragment_scan, container, false);
        CodeScannerView codeScannerView = root.findViewById(R.id.scanner_view);
        codeScanner = new CodeScanner(activity, codeScannerView);
        codeScanner.setDecodeCallback(new DecodeCallback() {
            @Override
            public void onDecoded(@NonNull Result result) {
                activity.runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        // Testing to see the string of the scanned QR code
                        // Change code later to create a QRCode object or scan user's QR codes
                        boolean sameHash = false;
                        if (scanAction == 0) { // generate QR code scan
                            qrCode = new QRCode(result.getText());
                            ArrayList<QRCode> playerCodes = player.getStats().getQrCodes();
                            for (int i = 0; i < playerCodes.size(); i++) {
                                if (qrCode.getHash().equals(playerCodes.get(i).getHash())) {
                                    sameHash = true;
                                    Toast.makeText(activity, "Cannot scan, QR code has already been scanned", Toast.LENGTH_SHORT).show();
                                }
                            }
                            if (!sameHash) {
                                // https://firebase.google.com/docs/firestore/query-data/queries
                                Query beenScanned = db.collection("codes").whereIn("hash", Arrays.asList(qrCode.getHash()));
                                beenScanned
                                        .get()
                                        .addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                                            @Override
                                            public void onComplete(@NonNull Task<QuerySnapshot> task) {
                                                if (task.isSuccessful()) {
                                                    for (QueryDocumentSnapshot document : task.getResult()) {
                                                        if (document.getId().equals(qrCode.getHash()))
                                                            qrCode.isScanned();
                                                    }
                                                }
                                                Toast.makeText(activity, result.getText(), Toast.LENGTH_SHORT).show();
                                                showQRStats(qrCode);
                                            }
                                        });
                            }
                        }

                        else if (scanAction == 1) { // Login scan
                            Toast.makeText(activity, result.getText(), Toast.LENGTH_SHORT).show();
                            listener1.onQRScanned1(result.getText()); // sends result back to LoginActivity
                        }

                        else if (scanAction == 2) { // Show player profile
                            Toast.makeText(activity, result.getText(), Toast.LENGTH_SHORT).show();
                            listener1.onQRScanned1(result.getText()); // sends result back to LoginActivity
                        }

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
    public void onAttach(@NonNull Context context) {
        super.onAttach(context);
        if (context instanceof ScanFragmentListener) {
            listener = (ScanFragmentListener) context;
        }
        if (context instanceof ScanFragmentListener1) {
            listener1 = (ScanFragmentListener1) context;
        }
    }

    @Override
    public void onDetach() {
        listener = null;
        listener1 = null;
        super.onDetach();
    }

    @Override
    public void onResume() {
        super.onResume();
        if (cameraDenied == false) {
            requestForCamera();
        }
    }

    @Override
    public void onPause() {
        codeScanner.releaseResources();
        super.onPause();
    }

    // Asks user for camera permissions
    // https://www.geeksforgeeks.org/easy-runtime-permissions-in-android-with-dexter/
    private void requestForCamera() {
        final Activity activity = getActivity();
        Dexter.withContext(activity).withPermissions(Manifest.permission.CAMERA)
        .withListener(new MultiplePermissionsListener() {
            @Override
            public void onPermissionsChecked(MultiplePermissionsReport multiplePermissionsReport) {
                // Starts scanner when permission is accepted
                if (multiplePermissionsReport.areAllPermissionsGranted()) {
                    codeScanner.startPreview();
                }
                /*
                 * Opens dialog box for user to access settings
                 * Older versions: "Don't ask again" is checked
                 * Newer versions: Denied request for permission 2 times
                 */
                if (multiplePermissionsReport.isAnyPermissionPermanentlyDenied()) {
                    showSettingsDialog();
                    cameraDenied = true;
                }
            }

            // Continues request for permission
            @Override
            public void onPermissionRationaleShouldBeShown(List<PermissionRequest> list, PermissionToken permissionToken) {
                permissionToken.continuePermissionRequest();
            }
        }).withErrorListener(new PermissionRequestErrorListener() {
            @Override
            public void onError(DexterError dexterError) {
                Toast.makeText(activity.getApplicationContext(), "Error occurred!", Toast.LENGTH_SHORT).show();
            }
        }).onSameThread().check();
    }

    /**
     * Shows how many points was the QR code, prompts user if they want to take photo of the object,
     * store geolocation and executes those actions if they are checked off.
     * @param qrCode
     */
    public void showQRStats(QRCode qrCode) {
        final Activity activity = getActivity();

        String[] options = {"Store photo", "Record geolocation"};
        boolean[] checkedOptions = {true, true};

        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
        builder.setTitle(String.format("You got %d points!", qrCode.getScore()));
        if (qrCode.getScanned()) {
            View scanned = getLayoutInflater().inflate(R.layout.fragment_scanned, null);
            builder.setView(scanned);
        }
        builder.setMultiChoiceItems(options, checkedOptions, new DialogInterface.OnMultiChoiceClickListener(){
            @Override
            public void onClick(DialogInterface dialogInterface, int i, boolean b) {
                switch (i) {
                    case 0:
                        if (b) {
                            checkedOptions[0] = true;
                        }
                        else {
                            checkedOptions[0] = false;
                        }
                    break;
                    case 1:
                        if (b) {
                            checkedOptions[1] = true;
                        }
                        else {
                            checkedOptions[1] = false;
                        }
                }
            }
        });
        builder.setPositiveButton("OK", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
                dialogInterface.cancel();
                if (checkedOptions[0]) { // Take photo
                    // https://developer.android.com/training/camera/photobasics
                    Intent takePictureIntent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
                    activity.startActivityForResult(takePictureIntent, REQUEST_IMAGE_CAPTURE); // Camera
                }
                // checkedOptions[1]: true; store geolocation
                listener.onQRScanned(qrCode, checkedOptions[1]); // Sends QRCode object to MainActivity
            }
        });


        builder.show();
    }

    // Prompts player to change their camera permission to use the QR scan feature
    public void showSettingsDialog() {
        final Activity activity = getActivity();

        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());

        builder.setTitle("Need Camera Permissions");

        builder.setMessage("SuperQR needs camera permission to use this feature. You can grant them in app settings.");
        builder.setPositiveButton("GOTO SETTINGS", new DialogInterface.OnClickListener() {
            // Redirects user to settings of the app
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
                dialogInterface.cancel();
                Intent intent = new Intent(Settings.ACTION_APPLICATION_DETAILS_SETTINGS);
                Uri uri = Uri.fromParts("package", activity.getPackageName(), null);
                intent.setData(uri);
                activity.startActivityForResult(intent, 101);
            }
        });
        builder.setNegativeButton("CANCEL", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
                dialogInterface.cancel();
            }
        });
        builder.show();
    }
}