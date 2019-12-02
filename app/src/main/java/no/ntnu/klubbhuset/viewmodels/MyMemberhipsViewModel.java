
package no.ntnu.klubbhuset.viewmodels;
import android.app.Application;
import android.content.Context;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.util.Log;

import androidx.lifecycle.AndroidViewModel;
import androidx.lifecycle.LifecycleOwner;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.google.zxing.BarcodeFormat;
import com.google.zxing.MultiFormatWriter;
import com.google.zxing.WriterException;
import com.google.zxing.common.BitMatrix;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import no.ntnu.klubbhuset.data.Resource;
import no.ntnu.klubbhuset.data.model.Club;
import no.ntnu.klubbhuset.data.repository.OrganizationRepository;
import no.ntnu.klubbhuset.data.repository.VippsRepository;
import no.ntnu.klubbhuset.util.CommunicationConfig;
import no.ntnu.klubbhuset.util.PreferenceUtils;

import static android.graphics.Color.BLACK;
import static android.graphics.Color.WHITE;
import static no.ntnu.klubbhuset.data.model.VippsJsonProperties.CLIENT_ID_STRING;
import static no.ntnu.klubbhuset.data.model.VippsJsonProperties.CLIENT_SECRET_STRING;
import static no.ntnu.klubbhuset.data.model.VippsJsonProperties.OCP_APIM_SUBSCRIPTION_KEY_STRING;
import static no.ntnu.klubbhuset.util.PreferenceUtils.PREF_NO_FILE_FOUND;


public class MyMemberhipsViewModel extends AndroidViewModel {
    private static final String TAG = "MyMemberhipsViewModel";
    private RequestQueue requestQueue;
    private MutableLiveData<Bitmap> QRCode;
    private VippsRepository vippsRepository;
    private OrganizationRepository organizationRepository;

    public MyMemberhipsViewModel(Application context) {
        super(context);
        this.requestQueue = Volley.newRequestQueue(context);
        this.vippsRepository = VippsRepository.getInstance(context);
        this.organizationRepository = OrganizationRepository.getInstance(context);
    }

    public LiveData<Resource<List<Club>>> getClubs(LifecycleOwner owner) {
        return organizationRepository.getOrgsWhereUserIsMember(owner);
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


    /**
     * returns a boolean describing whether the token has expired or not
     *
     * @param token vippsToken
     * @return a boolean describing whether the token has expired or not
     */
    private boolean tokenIsExpired(JSONObject token) {
        String expires_on = null;
        try {
            expires_on = token.get("expires_on").toString();
        } catch (JSONException e) {
            e.printStackTrace();
        }
        if(expires_on == null) {
            return true;
        }
        return System.currentTimeMillis() > Long.valueOf(expires_on);
    }

    /**
     * Tries to retrieve the VippsToken, this is either done by grabbing it from memory as existing
     * object if not then from SharedPreference, but that will only work if the token has been previously
     * placed there and is not expired.
     *
     * @return Returns the vippsToken or null if no vippsToken could be retrieved, check the log
     * for figuring out exactly why.
     */
    public LiveData<Resource<String>> getVippsToken() {
        return vippsRepository.getToken();
    }
}
