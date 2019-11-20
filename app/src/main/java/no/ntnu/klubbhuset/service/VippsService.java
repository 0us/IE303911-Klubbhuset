package no.ntnu.klubbhuset.service;

import android.content.Context;
import android.content.SharedPreferences;

import com.android.volley.AuthFailureError;
import com.android.volley.RequestQueue;
import com.android.volley.toolbox.JsonObjectRequest;

import java.util.HashMap;
import java.util.Map;

public class VippsService {
    public static final String CLIENT_ID = ""; // todo get id
    public static final String CLIENT_SECRET = ""; // todo
    public static final String OCP_APIM_SUBSCRIPTION_KEY = ""; //todo
    public static final String VIPPS_API_URL = "https://apitest.vipps.no/";

    private RequestQueue queue;
    private Context context; // calling class needs to give context;

    public VippsService(Context context) {
        this.context = context;
    }

    /**
     * Gets access token from vipps and stores in the sharedPreferenses vipps under token. This is basically an json object stored as a string
     */
    public void getAccessToken() {
        final String METHOD_URL = VIPPS_API_URL + "/accessToken/get";
        JsonObjectRequest request = new JsonObjectRequest(METHOD_URL, null,
                response -> {
                    SharedPreferences.Editor editor = context.getSharedPreferences("vipps", Context.MODE_PRIVATE).edit();
                    editor.putString("token", response.toString());
                },
                error -> {}) // todo implement error handling
        {
            @Override
            public Map<String, String> getHeaders() throws AuthFailureError {
                Map<String, String> headers = new HashMap<>();
                headers.put("client_id", CLIENT_ID);
                headers.put("client_secret", CLIENT_SECRET);
                headers.put("Ocp-Apim-Subscription-Key", OCP_APIM_SUBSCRIPTION_KEY);
                return headers;
            }
        };
    }

    

}
