package no.ntnu.klubbhuset.ui.managerviews.barcode;

import android.content.Context;
import android.graphics.Bitmap;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModelProviders;

import android.util.Base64;
import android.util.Log;

import com.google.android.gms.tasks.Task;
import com.google.firebase.ml.vision.FirebaseVision;
import com.google.firebase.ml.vision.barcode.FirebaseVisionBarcode;
import com.google.firebase.ml.vision.barcode.FirebaseVisionBarcodeDetector;
import com.google.firebase.ml.vision.barcode.FirebaseVisionBarcodeDetectorOptions;
import com.google.firebase.ml.vision.common.FirebaseVisionImage;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jws;
import io.jsonwebtoken.JwtException;
import io.jsonwebtoken.JwtParser;
import io.jsonwebtoken.Jwts;
import no.ntnu.klubbhuset.data.model.Club;
import no.ntnu.klubbhuset.util.PreferenceUtils;
import no.ntnu.klubbhuset.util.mlkit.CameraImageGraphic;
import no.ntnu.klubbhuset.util.mlkit.FrameMetadata;
import no.ntnu.klubbhuset.util.mlkit.GraphicOverlay;

import java.io.IOException;
import java.security.KeyFactory;
import java.security.PublicKey;
import java.security.spec.X509EncodedKeySpec;
import java.util.List;

/**
 * Barcode Detector Demo.
 */
public class BarcodeScanningProcessor extends VisionProcessorBase<List<FirebaseVisionBarcode>> {

    private static final String TAG = "BarcodeScanProc";

    private final FirebaseVisionBarcodeDetector detector;
    private final Context context;
    private final BarcodeViewModel barcodeViewModel;
    private final BarcodeScannerActivity activity;
    private Club club;

    public BarcodeScanningProcessor(Context context, BarcodeScannerActivity barcodeScannerActivity, Club club) {
        this.context = context;
        this.activity = barcodeScannerActivity;
        FirebaseVisionBarcodeDetectorOptions options =
                new FirebaseVisionBarcodeDetectorOptions.Builder()
                        .setBarcodeFormats(FirebaseVisionBarcode.FORMAT_QR_CODE)
                        .build();
        detector = FirebaseVision.getInstance().getVisionBarcodeDetector(options);
        this.club = club;
        barcodeViewModel = ViewModelProviders.of(barcodeScannerActivity).get(BarcodeViewModel.class);

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
                // Retrieves the public key object from string
                PublicKey pk = getKey(publickey);

                // Get Barcode
                FirebaseVisionBarcode barcode = barcodes.get(i);
                String token = barcode.getDisplayValue();

                // Retrieve claims from QR-Code
                Jws<Claims> jws;
                String email = "";
                try {
                    JwtParser parser = Jwts.parser().setSigningKey(pk);
                    jws = parser.parseClaimsJws(token);
                    email = jws.getBody().getSubject();
                } catch (JwtException je) {
                    je.printStackTrace();
                }

                // Retrieve the membership status of user
                barcodeViewModel.getUserPaymentStatus(email, club).observe(activity, s -> {
                    // Draw graphic onto the screen
                    BarcodeGraphic barcodeGraphic = new BarcodeGraphic(graphicOverlay, barcode, s);
                    graphicOverlay.add(barcodeGraphic);
                });
            }
        }
        graphicOverlay.postInvalidate();
    }


    @Override
    protected void onFailure(@NonNull Exception e) {
        Log.e(TAG, "Barcode detection failed " + e);
    }

    /**
     * Creates a PublicKey object from a Base64Encoded string (key), also checking if the key
     * contains some common unnecessary values. This method converts the given key to a byte[],
     * creates a new X509EncodedKeySpec of the byte[], retrieves a new KeyFactory instance for
     * RSA and then generates the Public Key object.
     *
     * @param key public key in string format
     * @return Public Key object
     */
    private static PublicKey getKey(String key) {
        if (key.contains("-----BEGIN PUBLIC KEY-----")
                || key.contains("-----END PUBLIC KEY-----")) {
            key = key.replace("-----BEGIN PUBLIC KEY-----", "");
            key = key.replace("-----END PUBLIC KEY-----", "");
        }
        try {
            byte[] byteKey = Base64.decode(key.getBytes(), Base64.DEFAULT);
            X509EncodedKeySpec X509publicKey = new X509EncodedKeySpec(byteKey);
            KeyFactory kf = KeyFactory.getInstance("RSA");

            return kf.generatePublic(X509publicKey);
        } catch (Exception e) {
            e.printStackTrace();
        }

        return null;
    }
}
