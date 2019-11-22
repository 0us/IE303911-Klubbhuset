package no.ntnu.klubbhuset.service;

import android.content.Context;
import android.content.SharedPreferences;
import android.util.Log;

import com.android.volley.AuthFailureError;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.toolbox.JsonObjectRequest;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.HashMap;
import java.util.Map;

public class VippsService {
    private static final String TAG = "VippsService";
    private static final String CLIENT_ID = ""; // todo get id
    private static final String CLIENT_SECRET = ""; // todo
    private static final String OCP_APIM_SUBSCRIPTION_KEY = ""; //todo
    private static final String VIPPS_API_URL = "https://apitest.vipps.no";
    private static final String AUTHORIZATION = "Authorization";
    private static final String OCP_APIM_SUBSCRIPTION_KEY_STRING = "Ocp-Apim-Subscription-Key";
    private static final String APPLICATION_JSON = "application/json";
    private static final String CONTENT_TYPE = "Content-Type";
    private static final String VIPPS_SRING = "vipps";
    private static final String merchantSerialNumber = "";


    private RequestQueue queue;
    private Context context; // calling class needs to give context;
    SharedPreferences preferences = context.getSharedPreferences(VIPPS_SRING, Context.MODE_PRIVATE);
    String authToken = preferences.getString("token", null);

    public VippsService(Context context) {
        this.context = context;
    }

    /**
     * Gets access token from vipps and stores in the sharedPreferenses vipps under token. This is basically an json object stored as a string
     */
    public void getAccessToken() {
        final String METHOD_URL = VIPPS_API_URL + "/accessToken/get";
        JsonObjectRequest request = new JsonObjectRequest(Request.Method.POST, METHOD_URL, null,
                response -> {
                    SharedPreferences.Editor editor = preferences.edit();
                    editor.putString("token", response.toString());
                },
                error -> {
                }) // todo implement error handling
        {
            @Override
            public Map<String, String> getHeaders() throws AuthFailureError {
                Map<String, String> headers = new HashMap<>();
                headers.put("client_id", CLIENT_ID);
                headers.put("client_secret", CLIENT_SECRET);
                headers.put(OCP_APIM_SUBSCRIPTION_KEY_STRING, OCP_APIM_SUBSCRIPTION_KEY);
                return headers;
            }
        };

        queue.add(request);
    }

    public void initiatePayment(int amount, int mobileNumber) {
        final String METHOD_URL = VIPPS_API_URL + "/ecomm/v2/payments/";
        JSONObject details = new JSONObject();

        if (authToken != null) {
            try {
                JSONObject merchantInfo = new JSONObject();
                merchantInfo.put("merchantSerialNumber", merchantSerialNumber);
                merchantInfo.put("callbackPrefix", callbackPrefix);
                merchantInfo.put("fallBack", fallBack);
                merchantInfo.put("authToken", authToken);
                merchantInfo.put("isApp", true);

                JSONObject customerInfo = new JSONObject();
                customerInfo.put("mobileNumber", mobileNumber);

                JSONObject transaction = new JSONObject();
                transaction.put("orderId", orderId);
                transaction.put("amount", amount);
                transaction.put("transactionText", transactionText);
                transaction.put("skipLandingPage", false);

                details.put("merchantInfo", merchantInfo);
                details.put("customerInfo", customerInfo);
                details.put("transaction", transaction);
            } catch (JSONException e) {
                e.printStackTrace();
            }

            JsonObjectRequest request = new JsonObjectRequest(Request.Method.POST, METHOD_URL, details,
                    response -> {
                        try {
                            String vippsURL = response.getString("url"); // todo don't know what we do with this. Can try to open it via intent or pase it to calling class
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
                    },
                    error -> {
                    }) {
                @Override
                public Map<String, String> getHeaders() {
                    Map<String, String> headers = new HashMap<>();
                    headers.put(CONTENT_TYPE, APPLICATION_JSON);
                    headers.put("Ocp-Apim-Subscription-Key", OCP_APIM_SUBSCRIPTION_KEY);
                    headers.put(AUTHORIZATION, "Bearer " + authToken);
                    return headers;
                }
            };
            queue.add(request);
        }
    }

    public void capturePayment(String orderId, int amount) {
        JSONObject body = new JSONObject();

        try {
            JSONObject merchantInfo = new JSONObject();
            merchantInfo.put("merchantSerialNumber", merchantSerialNumber);

            JSONObject transaction = new JSONObject();
            transaction.put("amount", amount);
            transaction.put("transactionText", transactionText);
            transaction.put("skipLandingPage", true);

            body.put("merchantInfo", merchantInfo);
            body.put("transaction", transaction);

        } catch (JSONException e) {
            e.printStackTrace();
        }


        final String METHOD_URL = VIPPS_API_URL + "/ecomm/v2/payments/" + orderId + "/capture";
        JsonObjectRequest request = new JsonObjectRequest(Request.Method.POST, METHOD_URL, body,
                response -> {
                    Log.d(TAG, "capturePayment: response: " + response);
                },
                error -> {
                }) // todo implement error handling
        {
            @Override
            public Map<String, String> getHeaders() {
                Map<String, String> headers = new HashMap<>();
                headers.put(CONTENT_TYPE, APPLICATION_JSON);
                headers.put(AUTHORIZATION, authToken);
                headers.put(OCP_APIM_SUBSCRIPTION_KEY_STRING, OCP_APIM_SUBSCRIPTION_KEY);
                return headers;
            }
        };

        queue.add(request);
    }

    public void getPaymentStatus(String orderId) {
        final String METHOD_URL = VIPPS_API_URL + "/ecomm/v2/payments/" + orderId + "/details";
        JsonObjectRequest request = new JsonObjectRequest(Request.Method.GET, METHOD_URL, null,
                response -> {
                    Log.d(TAG, "getPaymentStatus: response: " + response);
                },
                error -> {
                }) // todo implement error handling
        {
            @Override
            public Map<String, String> getHeaders() {
                Map<String, String> headers = new HashMap<>();
                headers.put(AUTHORIZATION, authToken);
                headers.put(OCP_APIM_SUBSCRIPTION_KEY_STRING, OCP_APIM_SUBSCRIPTION_KEY);
                return headers;
            }
        };

        queue.add(request);
    }
}
