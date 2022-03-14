package com.example.superqr;

import android.Manifest;
import android.app.Activity;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
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
import com.google.zxing.Result;
import com.karumi.dexter.Dexter;
import com.karumi.dexter.MultiplePermissionsReport;
import com.karumi.dexter.PermissionToken;
import com.karumi.dexter.listener.DexterError;
import com.karumi.dexter.listener.PermissionRequest;
import com.karumi.dexter.listener.PermissionRequestErrorListener;
import com.karumi.dexter.listener.multi.MultiplePermissionsListener;

import java.util.ArrayList;
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
    private Player player;
    private CodeScanner codeScanner;
    private boolean cameraDenied; // permission permanently denied
    private ScanFragmentListener listener;
    private ScanFragmentListener1 listener1;
    private int scanAction;

    // https://stackoverflow.com/questions/35091857/passing-object-from-fragment-to-activity
    public interface ScanFragmentListener {
        void onQRScanned(QRCode qrCode);
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
     * @param player current player of the game
     * @return A new instance of fragment ScanFragment.
     */
    // TODO: Rename and change types and number of parameters
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
                            QRCode qrCode = new QRCode(result.getText());
                            ArrayList<QRCode> playerCodes = player.getStats().getQrCodes();
                            for (int i = 0; i < playerCodes.size(); i++) {
                                if (qrCode.getHash().equals(playerCodes.get(i).getHash())) {
                                    sameHash = true;
                                    Toast.makeText(activity, "Cannot scan, QR code has already been scanned", Toast.LENGTH_SHORT).show();
                                }
                            }
                            if (!sameHash) {
                                listener.onQRScanned(qrCode); // Sends QRCode object to MainActivity
                                Toast.makeText(activity, result.getText(), Toast.LENGTH_SHORT).show();
                            }
                        }

                        else if (scanAction == 1) { // Login scan
                            Toast.makeText(activity, result.getText(), Toast.LENGTH_SHORT).show();
                            listener1.onQRScanned1(result.getText()); // sends result back to LoginActivity
                        }

                        else if (scanAction == 2) { // Show player profile

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
                startActivityForResult(intent, 101); // should use ActivityResultLauncher, but don't know what to do with result
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