package no.ntnu.klubbhuset.ui.managerviews;

import android.app.Activity;
import android.content.Intent;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.navigation.Navigation;

import android.provider.MediaStore;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.Toast;


import com.google.android.gms.vision.CameraSource;
import com.google.firebase.ml.vision.FirebaseVision;
import com.google.firebase.ml.vision.barcode.FirebaseVisionBarcode;
import com.google.firebase.ml.vision.barcode.FirebaseVisionBarcodeDetector;
import com.google.firebase.ml.vision.barcode.FirebaseVisionBarcodeDetectorOptions;
import com.google.firebase.ml.vision.common.FirebaseVisionImage;

import java.util.Objects;

import no.ntnu.klubbhuset.R;


public class ManagerFragment extends Fragment {

    private static final int TAKE_PICTURE = 1;
    private ManagerViewModel mViewModel;
    private CameraSource cameraSource;
    private FirebaseVisionBarcodeDetector detector;

    public static ManagerFragment newInstance() {
        return new ManagerFragment();
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {

        // Creates a option object for the Detector to use, here we just configuring it for QR_CODE
        FirebaseVisionBarcodeDetectorOptions options = new FirebaseVisionBarcodeDetectorOptions.Builder().setBarcodeFormats(
                FirebaseVisionBarcode.FORMAT_QR_CODE
        ).build();

        // Creates a new detector for detecting images
        detector = FirebaseVision.getInstance().getVisionBarcodeDetector(options);

        return inflater.inflate(R.layout.manager_fragment, container, false);
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        Button createNewOrgBtn = Objects.requireNonNull(getView()).findViewById(R.id.manage_create_new_org);
        createNewOrgBtn.setOnClickListener(l -> {
            Navigation.findNavController(getView()).navigate(R.id.action_managerFragment_to_createOrganizationForm);
        });

        Button scanQrCodeBtn = Objects.requireNonNull(getView()).findViewById(R.id.manage_scan_qr_code);
        scanQrCodeBtn.setOnClickListener(l -> {
            Intent cameraIntent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
            startActivityForResult(cameraIntent, TAKE_PICTURE);
        });
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        switch (requestCode) {
            case TAKE_PICTURE:
                if (resultCode == Activity.RESULT_OK) {
                    // We create a FireBaseVisionImage from the camera
                    if (data != null && data.getExtras() != null) {
                        Bitmap imageBitmap = (Bitmap) data.getExtras().get("data");

                        assert imageBitmap != null;
                        FirebaseVisionImage visionImage = FirebaseVisionImage.fromBitmap(imageBitmap);

                        // Add listener to successful detection and error
                        detector.detectInImage(visionImage).addOnSuccessListener(l -> {
                            if (!l.isEmpty()) {
                                Toast.makeText(Objects.requireNonNull(
                                        getActivity()).getApplicationContext(),
                                        "Successful read of QR-CODE! YeeeHOoOoOOoOO!" + "\n"
                                                + "The message is: " + l.get(0).getDisplayValue(),
                                        Toast.LENGTH_LONG).show();
                            }
                        }).addOnFailureListener(l -> {
                            Toast.makeText(Objects.requireNonNull(
                                    getActivity()).getApplicationContext(),
                                    "Bad read of QR-CODE! NooOoOOooOooo!",
                                    Toast.LENGTH_LONG).show();
                        });
                    }
                }
        }
    }

}
