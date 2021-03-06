
package no.ntnu.klubbhuset.viewmodels;
import android.app.Application;
import android.content.Context;
import android.content.SharedPreferences;
import android.graphics.Bitmap;

import androidx.lifecycle.AndroidViewModel;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;

import com.google.zxing.BarcodeFormat;
import com.google.zxing.MultiFormatWriter;
import com.google.zxing.WriterException;
import com.google.zxing.common.BitMatrix;

import java.util.List;

import no.ntnu.klubbhuset.data.Resource;
import no.ntnu.klubbhuset.data.model.Club;
import no.ntnu.klubbhuset.data.repository.OrganizationRepository;

import static android.graphics.Color.BLACK;
import static android.graphics.Color.WHITE;


public class MyMemberhipsViewModel extends AndroidViewModel {
    private static final String TAG = "MyMemberhipsViewModel";
    private MutableLiveData<Bitmap> QRCode;
    private OrganizationRepository organizationRepository;

    public MyMemberhipsViewModel(Application context) {
        super(context);
        this.organizationRepository = OrganizationRepository.getInstance(context);
    }

    public LiveData<Resource<List<Club>>> getClubs() {
        return organizationRepository.getOrgsWhereUserIsMember();
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
        SharedPreferences preferences = getApplication().getSharedPreferences("login", Context.MODE_PRIVATE);
        return preferences.getString("token", null);
    }

    private void generateQRCode() {
        String token = getIdToken();
        if (token != null) {
            Bitmap bitmap = null;
            bitmap = encodeAsBitmap(token);
            if (bitmap != null) {
                QRCode.setValue(bitmap);
            } else {

            }
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

    public LiveData<Resource<List<Club>>> refreshClubs() {
        return organizationRepository.getOrgsWhereUserIsMember(true);
    }
}
