package no.ntnu.klubbhuset.ui.managerviews.barcode;

import android.content.Context;
import android.graphics.Bitmap;
import android.util.Base64;
import android.util.Log;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.lifecycle.ViewModelProviders;

import com.google.android.gms.tasks.Task;
import com.google.firebase.ml.vision.FirebaseVision;
import com.google.firebase.ml.vision.barcode.FirebaseVisionBarcode;
import com.google.firebase.ml.vision.barcode.FirebaseVisionBarcodeDetector;
import com.google.firebase.ml.vision.barcode.FirebaseVisionBarcodeDetectorOptions;
import com.google.firebase.ml.vision.common.FirebaseVisionImage;

import java.io.IOException;
import java.security.KeyFactory;
import java.security.PublicKey;
import java.security.spec.X509EncodedKeySpec;
import java.util.List;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jws;
import io.jsonwebtoken.JwtException;
import io.jsonwebtoken.JwtParser;
import io.jsonwebtoken.Jwts;
import no.ntnu.klubbhuset.data.Status;
import no.ntnu.klubbhuset.data.model.Club;
import no.ntnu.klubbhuset.util.PreferenceUtils;
import no.ntnu.klubbhuset.util.mlkit.CameraImageGraphic;
import no.ntnu.klubbhuset.util.mlkit.FrameMetadata;
import no.ntnu.klubbhuset.util.mlkit.GraphicOverlay;
import no.ntnu.klubbhuset.viewmodels.BarcodeViewModel;

/**
 * This class represents the processor used for processing barcodes.
 */
public class BarcodeScanningProcessor extends VisionProcessorBase<List<FirebaseVisionBarcode>> {

    private static final String TAG = "BarcodeScanProc";

    private final FirebaseVisionBarcodeDetector detector;
    private final Context context;
    private final BarcodeViewModel barcodeViewModel;
    private final BarcodeScannerActivity activity;
    private Club club;
    private PublicKey pk;

    private FirebaseVisionBarcode prevBarCode = null;
    private String prevBarCodeText;

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

        retrievePk();
    }

    /**
     * Retrieves the public key of Klubbhuset, and creates a reference to it in this class
     */
    private void retrievePk() {
        // Get public key
        String publickey = PreferenceUtils.getPublicKey(context);
        if (publickey.equals(PreferenceUtils.PREF_NO_FILE_FOUND)) {
            Log.e("BarcodeScanner", "No public key to be found");
        }
        // Retrieves the public key object from string
        pk = getKey(publickey);
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

    /**
     * This method is called every time a complete image is retrieved from the camera. This method
     * will check the image for barcodes, if it finds barcodes in the image, it will start checking
     * for tokens in the barcode. On successful retrieval of a token, it will be validated against
     * Klubbhuset public key and a status will be retrieved on payment status.
     *
     * @param originalCameraImage hold the original image from camera, used to draw the background
     * @param barcodes            The list of detected barcodes in the image
     * @param frameMetadata
     * @param graphicOverlay      The overlay used on screen to contain graphic elements
     */
    @Override
    protected void onSuccess(
            @Nullable Bitmap originalCameraImage,
            @NonNull List<FirebaseVisionBarcode> barcodes,
            @NonNull FrameMetadata frameMetadata,
            @NonNull GraphicOverlay graphicOverlay) {

        graphicOverlay.clear();
        // Add orignal image to graphicOverlay
        if (originalCameraImage != null) {
            // Retrieve image, and add it to the graphicOverlay
            CameraImageGraphic imageGraphic = new CameraImageGraphic(graphicOverlay, originalCameraImage);
            graphicOverlay.add(imageGraphic);
        }

        // Check if there are any barcodes
        if (barcodes.size() > 0) {
            for (int i = 0; i < barcodes.size(); ++i) {

                // Get Barcode
                FirebaseVisionBarcode barcode = barcodes.get(i);
                String token = barcode.getDisplayValue();

                // If this is the first run
                if (prevBarCode == null) {
                    displayTokenValue(token, barcode, graphicOverlay);

                }
                // If the previous barcode is the same as the new, set the old to display
                else if (barcode.getDisplayValue().equals(prevBarCode.getDisplayValue())) {
                    BarcodeGraphic barcodeGraphic = new BarcodeGraphic(graphicOverlay, prevBarCode, prevBarCodeText);
                    graphicOverlay.add(barcodeGraphic);

                }
                // If it's a new barcode, retrieve and display it's values
                else {
                    displayTokenValue(token, barcode, graphicOverlay);

                }
            }
        }
        graphicOverlay.postInvalidate();
    }

    /**
     * Retrieves and displays token value to the screen
     *
     * @param token          Token
     * @param barcode        Barcode
     * @param graphicOverlay The overlay
     */
    private void displayTokenValue(
            String token,
            FirebaseVisionBarcode barcode,
            GraphicOverlay graphicOverlay) {

        // Try to retrieve claims from QR-Code
        Jws<Claims> jws;
        String email = "";
        try {
            JwtParser parser = Jwts.parser().setSigningKey(pk);
            jws = parser.parseClaimsJws(token);
            email = jws.getBody().getSubject();

            // Retrieve the membership status of user
            barcodeViewModel.getUserPaymentStatus(email, club).observe(activity, response -> {
                // Draw graphic onto the screen
                if (response.getStatus() == Status.SUCCESS) {
                    String textResult = response.getData();
                    BarcodeGraphic barcodeGraphic = new BarcodeGraphic(graphicOverlay, barcode, textResult);
                    graphicOverlay.add(barcodeGraphic);

                    prevBarCode = barcode;
                    prevBarCodeText = textResult;

                } else if (response.getStatus() == Status.ERROR) {
                    String textResult = response.getError();
                    BarcodeGraphic barcodeGraphic = new BarcodeGraphic(graphicOverlay, barcode, response.getError());
                    graphicOverlay.add(barcodeGraphic);

                    prevBarCode = barcode;
                    prevBarCodeText = textResult;
                }
            });
            graphicOverlay.postInvalidate();
        } // If no token can be retrieved, print user feedback and continue parsing barcodes
        catch (JwtException je) {
            String msg = "Not Valid!";
            BarcodeGraphic barcodeGraphic = new BarcodeGraphic(graphicOverlay, barcode, msg);
            graphicOverlay.add(barcodeGraphic);

            prevBarCode = barcode;
            prevBarCodeText = msg;
        }
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
