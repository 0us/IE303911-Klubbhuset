package no.ntnu.klubbhuset.ui.userviews.memberships;

import android.app.Application;
import android.graphics.Bitmap;

import androidx.lifecycle.AndroidViewModel;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;

import com.android.volley.RequestQueue;
import com.android.volley.toolbox.Volley;
import com.google.zxing.BarcodeFormat;
import com.google.zxing.MultiFormatWriter;
import com.google.zxing.WriterException;
import com.google.zxing.common.BitMatrix;

import static android.graphics.Color.BLACK;
import static android.graphics.Color.WHITE;


public class MyMemberhipsViewModel extends AndroidViewModel {
    // TODO: Implement the ViewModel
    private RequestQueue requestQueue;
    private MutableLiveData<Bitmap> QRCode;
    private MutableLiveData<String> token;

    public MyMemberhipsViewModel(Application context) {
        super(context);
        this.requestQueue = Volley.newRequestQueue(context);
    }

    public LiveData<Bitmap> getQRCode() {
        if (this.QRCode == null) {
            QRCode = new MutableLiveData<>();
            generateQRCode();
        }
        return this.QRCode;
    }

    private String getIdToken() {
        // TODO get token from loggedinuser
        return "https://www.ungspiller.no/tipsOgTriks/";
    }

    private void generateQRCode() {
        String token = getIdToken();
        Bitmap bitmap = null;
        bitmap = encodeAsBitmap(token);
        if (bitmap != null) {
            QRCode.setValue(bitmap);
        }
        else {

        }
    }

    private Bitmap encodeAsBitmap(String str) {
        int WIDTH = 500;
        BitMatrix result = null;
        try {
            result = new MultiFormatWriter().encode(str,
                    BarcodeFormat.QR_CODE, WIDTH, WIDTH, null);
        } catch (IllegalArgumentException iae) {
            // Unsupported format
            return null;
        } catch (WriterException e) {
            e.printStackTrace();
        }
        if (result != null) {
            int w = result.getWidth();
            int h = result.getHeight();
            int[] pixels = new int[w * h];
            for (int y = 0; y < h; y++) {
                int offset = y * w;
                for (int x = 0; x < w; x++) {
                    pixels[offset + x] = result.get(x, y) ? BLACK : WHITE;
                }
            }
            Bitmap bitmap = Bitmap.createBitmap(w, h, Bitmap.Config.ARGB_8888);
            bitmap.setPixels(pixels, 0, WIDTH, 0, 0, w, h);
            return bitmap;
        }
        return null;
    }
}
