package no.ntnu.klubbhuset.data.repository;

import android.content.Context;
import android.util.Log;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;

import java.util.HashMap;
import java.util.Map;

import no.ntnu.klubbhuset.data.cache.Cache;
import no.ntnu.klubbhuset.data.Resource;
import no.ntnu.klubbhuset.data.cache.VippsCache;
import no.ntnu.klubbhuset.util.CommunicationConfig;
import no.ntnu.klubbhuset.util.PreferenceUtils;

import static no.ntnu.klubbhuset.data.model.VippsJsonProperties.CLIENT_ID;
import static no.ntnu.klubbhuset.data.model.VippsJsonProperties.CLIENT_SECRET;
import static no.ntnu.klubbhuset.data.model.VippsJsonProperties.OCP_APIM_SUBSCRIPTION_KEY;
import static no.ntnu.klubbhuset.util.PreferenceUtils.PREF_NO_FILE_FOUND;

public class VippsRepository {
    private static VippsRepository ourInstance;
    private RequestQueue requestQueue;
    private static final String TAG = "VippsRepository";
    private Context context;


    private Cache cache = Cache.getInstance();
    private VippsCache vippsCache = VippsCache.getInstance();

    public static VippsRepository getInstance(Context context) {
        if (ourInstance == null) {
            ourInstance = new VippsRepository(context);
        }
        return ourInstance;
    }

    private VippsRepository(Context context) {
        this.requestQueue = Volley.newRequestQueue(context);
        this.context = context;
    }

    /**
     * Retrieves the vippsToken from Vipps, saves the value to the MutableLiveData and prefs
     */
    public LiveData<Resource<String>> getToken() {
        String url = CommunicationConfig.getVippsTokenURL();
        String prefToken = PreferenceUtils.getVippsToken(context.getApplicationContext());
        MutableLiveData<Resource<String>> cached = vippsCache.getVippsToken();
        if (!prefToken.equals(PREF_NO_FILE_FOUND)) {
            // no token is found in prefs
            loadVippsToken(cached, url);
        } else if (tokenIsExpired(prefToken)) {
            // token has expired
            loadVippsToken(cached, url);
        } else {
            // prefs token is valid
            cached.setValue(Resource.success(prefToken));
        }
        return cached;
    }

    private void loadVippsToken(MutableLiveData cached, String url) {
        StringRequest request = new StringRequest(Request.Method.POST, url,
                response -> {
                    cached.setValue(Resource.success(response));
                    PreferenceUtils.setVippsToken(context.getApplicationContext(), response);
                    if (response.isEmpty()) {
                        Log.e(TAG, "Got response from server, but token was empty");
                    } else {
                        Log.i(TAG, "Successful fetch");
                    }
                }, error -> {
            cached.setValue(Resource.error(String.valueOf(error.networkResponse.statusCode), error));
            Log.e(TAG, (String.valueOf(error.networkResponse.statusCode)));
        }) {
            @Override
            public Map<String, String> getHeaders() {
                Map<String, String> map = new HashMap<>();
                map.put(CLIENT_ID, CommunicationConfig.getInstance().retrieveClientID());
                map.put(CLIENT_SECRET, CommunicationConfig.getInstance().retrieveClientSecret());
                map.put(OCP_APIM_SUBSCRIPTION_KEY, CommunicationConfig.getInstance().retrieveOcpApimSubscriptionKey());
                return map;
            }
        };
        requestQueue.add(request);
    }


    /**
     * returns a boolean describing whether the token has expired or not
     *
     * @param token vippsToken
     * @return a boolean describing whether the token has expired or not
     */
    private boolean tokenIsExpired(String token) {
        // TODO: 23.11.2019 Implement
        return false;
    }
}
