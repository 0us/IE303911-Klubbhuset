package no.ntnu.klubbhuset.ui.userviews.memberships;

import android.app.Application;
import android.content.Context;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.util.Log;

import androidx.lifecycle.AndroidViewModel;
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
import java.util.Map;

import no.ntnu.klubbhuset.data.CommunicationConfig;
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
    private MutableLiveData<String> token;
    private MutableLiveData<JSONObject> vippsToken;

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
    public LiveData<JSONObject> getVippsToken() {

        // Check if vippsToken object exists
        if (vippsToken == null) {
            vippsToken = new MutableLiveData<>();

            // try to retrieve the token from prefs
            String prefToken = PreferenceUtils.getVippsToken(getApplication().getApplicationContext());

            // if token from prefs is null
            if (prefToken.equals(PREF_NO_FILE_FOUND)) {
                // send request for new token
                loadVippsToken();

            } else {

                // Initialize string as JSONObject
                JSONObject jsonToken;
                try {
                    jsonToken = new JSONObject(prefToken);
                } catch (JSONException e) {
                    e.printStackTrace();
                    return vippsToken;
                }

                // Checks if the token is expired
                if (tokenIsExpired(jsonToken)) {
                    loadVippsToken();
                }

                vippsToken.setValue(jsonToken);
            }
        }
        return vippsToken;
    }


    /**
     * Retrieves the vippsToken from Vipps, saves the value to the MutableLiveData and prefs
     */
    private void loadVippsToken() {
        String url = CommunicationConfig.getVippsTokenURL();
        StringRequest request = new StringRequest(Request.Method.POST, url,
                response -> {
                    try {
                        JSONObject json = new JSONObject(response);
                        vippsToken.setValue(json);
                        PreferenceUtils.setVippsToken(getApplication().getApplicationContext(), response);
                        Log.i(TAG, "Successful fetch");
                    } catch (JSONException e) {
                        Log.e(TAG, "Got response from server, but token was empty");
                    }
                }, error -> {
            Log.e(TAG, (String.valueOf(error.networkResponse.statusCode)));
        }) {
            @Override
            public Map<String, String> getHeaders() {
                Map<String, String> map = new HashMap<>();
                map.put(CLIENT_ID_STRING, CommunicationConfig.getInstance().retrieveClientID());
                map.put(CLIENT_SECRET_STRING, CommunicationConfig.getInstance().retrieveClientSecret());
                map.put(OCP_APIM_SUBSCRIPTION_KEY_STRING, CommunicationConfig.getInstance().retrieveOcpApimSubscriptionKey());
                return map;
            }
        };
        requestQueue.add(request);
    }
}
