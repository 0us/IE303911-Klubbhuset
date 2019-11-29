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
import static no.ntnu.klubbhuset.data.model.VippsJsonProperties.*;

public class VippsService {
    private static final String TAG = "VippsService";
    private static final String CLIENT_ID = ""; // todo get id
    private static final String CLIENT_SECRET = ""; // todo
    private static final String OCP_APIM_SUBSCRIPTION_KEY = ""; //todo
    private static final String transactionText = ""; // todo


    private RequestQueue queue;
    private Context context; // calling class needs to give context;
    SharedPreferences preferences = context.getSharedPreferences(VIPPS_STRING, Context.MODE_PRIVATE);
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
                headers.put(CLIENT_ID_STRING, CLIENT_ID);
                headers.put(CLIENT_SECRET_STRING, CLIENT_SECRET);
                headers.put(OCP_APIM_SUBSCRIPTION_KEY_STRING, OCP_APIM_SUBSCRIPTION_KEY);
                return headers;
            }
        };

        queue.add(request);
    }

    public void initiatePayment(double amount, long mobileNumber, String transactionText, String orderId) {
        final String METHOD_URL = VIPPS_API_URL + ECOMM_V_2_PAYMENTS;
        JSONObject details = new JSONObject();

        if (authToken != null) {
            try {
                JSONObject merchantInfo = new JSONObject();
                merchantInfo.put(MERCHANT_SERIAL_NUMBER_STRING, merchantSerialNumber);
                merchantInfo.put(CALLBACK_PREFIX_STRING, callbackPrefix);
                merchantInfo.put(FALL_BACK_STRING, fallBack);
                merchantInfo.put(AUTH_TOKEN, authToken);
                merchantInfo.put(IS_APP, true);

                JSONObject customerInfo = new JSONObject();
                customerInfo.put(MOBILE_NUMBER_STRING, mobileNumber);

                JSONObject transaction = new JSONObject();
                transaction.put("orderId", orderId);
                transaction.put(AMOUNT_STRING, amount);
                transaction.put(TRANSACTION_TEXT_STRING, transactionText);
                transaction.put(SKIP_LANDING_PAGE_STRING, false);

                details.put(MERCHANT_INFO_STRING, merchantInfo);
                details.put(CUSTOMER_INFO_STRING, customerInfo);
                details.put(TRANSACTION_STRING, transaction);
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
                    headers.put(OCP_APIM_SUBSCRIPTION_KEY_STRING, OCP_APIM_SUBSCRIPTION_KEY);
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
            merchantInfo.put(MERCHANT_SERIAL_NUMBER_STRING, merchantSerialNumber);

            JSONObject transaction = new JSONObject();
            transaction.put(AMOUNT_STRING, amount);
            transaction.put(TRANSACTION_TEXT_STRING, transactionText);
            transaction.put(SKIP_LANDING_PAGE_STRING, true);

            body.put(MERCHANT_INFO_STRING, merchantInfo);
            body.put(TRANSACTION_STRING, transaction);

        } catch (JSONException e) {
            e.printStackTrace();
        }


        final String METHOD_URL = VIPPS_API_URL + ECOMM_V_2_PAYMENTS + orderId + CAPTURE;
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
        final String METHOD_URL = VIPPS_API_URL + ECOMM_V_2_PAYMENTS + orderId + DETAILS;
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
