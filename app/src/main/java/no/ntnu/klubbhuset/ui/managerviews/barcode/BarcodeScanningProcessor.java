package no.ntnu.klubbhuset.ui.managerviews.barcode;

import android.content.Context;
import android.content.SharedPreferences;
import android.graphics.Bitmap;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import android.preference.PreferenceManager;
import android.util.Log;
import android.widget.Toast;

import com.android.volley.Request;
import com.android.volley.toolbox.StringRequest;
import com.google.android.gms.tasks.Task;
import com.google.firebase.ml.vision.FirebaseVision;
import com.google.firebase.ml.vision.barcode.FirebaseVisionBarcode;
import com.google.firebase.ml.vision.barcode.FirebaseVisionBarcodeDetector;
import com.google.firebase.ml.vision.barcode.FirebaseVisionBarcodeDetectorOptions;
import com.google.firebase.ml.vision.common.FirebaseVisionImage;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Header;
import io.jsonwebtoken.Jwt;
import io.jsonwebtoken.Jwts;
import no.ntnu.klubbhuset.data.CommunicationConfig;
import no.ntnu.klubbhuset.util.PreferenceUtils;
import no.ntnu.klubbhuset.util.mlkit.CameraImageGraphic;
import no.ntnu.klubbhuset.util.mlkit.FrameMetadata;
import no.ntnu.klubbhuset.util.mlkit.GraphicOverlay;

import java.io.IOException;
import java.util.List;

import static no.ntnu.klubbhuset.data.CommunicationConfig.API_URL;

/**
 * Barcode Detector Demo.
 */
public class BarcodeScanningProcessor extends VisionProcessorBase<List<FirebaseVisionBarcode>> {

    private static final String TAG = "BarcodeScanProc";

    private final FirebaseVisionBarcodeDetector detector;
    private final Context context;

    public BarcodeScanningProcessor(Context context) {
        this.context = context;
        FirebaseVisionBarcodeDetectorOptions options =
                new FirebaseVisionBarcodeDetectorOptions.Builder()
                        .setBarcodeFormats(FirebaseVisionBarcode.FORMAT_QR_CODE)
                        .build();
        detector = FirebaseVision.getInstance().getVisionBarcodeDetector(options);
    }

    @Override
    public void stop() {
        try {
            detector.close();
        } catch (IOException e) {
            Log.e(TAG, "Exception thrown while trying to close Barcode Detector: " + e);
        }
    }

    @Override
    protected Task<List<FirebaseVisionBarcode>> detectInImage(FirebaseVisionImage image) {
        return detector.detectInImage(image);
    }

    @Override
    protected void onSuccess(
            @Nullable Bitmap originalCameraImage,
            @NonNull List<FirebaseVisionBarcode> barcodes,
            @NonNull FrameMetadata frameMetadata,
            @NonNull GraphicOverlay graphicOverlay) {
        graphicOverlay.clear();
        if (originalCameraImage != null) {
            CameraImageGraphic imageGraphic = new CameraImageGraphic(graphicOverlay, originalCameraImage);
            graphicOverlay.add(imageGraphic);
        }
        if (barcodes.size() > 0) {
            for (int i = 0; i < barcodes.size(); ++i) {

                // Get public key
                String publickey = PreferenceUtils.getPublicKey(context);
                if (publickey.equals(PreferenceUtils.PREF_NO_FILE_FOUND)) {
                    Log.e("BarcodeScanner", "No public key to be found");
                    continue;
                }

                // Get Barcode
                FirebaseVisionBarcode barcode = barcodes.get(i);

                // Retrieve claims from QR-Code
                Jwt<Header, Claims> claims = Jwts.parser().setSigningKey(publickey).parseClaimsJwt(barcode.getDisplayValue());

                String email = claims.getBody().getIssuer();
                retrieveMemberShipStatus(email);

                BarcodeGraphic barcodeGraphic = new BarcodeGraphic(graphicOverlay, barcode);
                graphicOverlay.add(barcodeGraphic);
            }
        }
        graphicOverlay.postInvalidate();
    }

    private void retrieveMemberShipStatus(String email) {

    }

    @Override
    protected void onFailure(@NonNull Exception e) {
        Log.e(TAG, "Barcode detection failed " + e);
    }
}
