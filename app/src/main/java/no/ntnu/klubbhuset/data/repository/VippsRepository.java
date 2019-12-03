package no.ntnu.klubbhuset.data.repository;

import android.content.Context;
import android.util.Log;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MediatorLiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.Observer;
import androidx.lifecycle.Transformations;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.UnsupportedEncodingException;
import java.util.Calendar;
import java.util.HashMap;
import java.util.Map;
import java.util.Observable;
import java.util.concurrent.TimeUnit;

import lombok.val;
import no.ntnu.klubbhuset.R;
import no.ntnu.klubbhuset.data.Status;
import no.ntnu.klubbhuset.data.cache.Cache;
import no.ntnu.klubbhuset.data.Resource;
import no.ntnu.klubbhuset.data.cache.VippsCache;
import no.ntnu.klubbhuset.data.model.Club;
import no.ntnu.klubbhuset.data.model.OrderId;
import no.ntnu.klubbhuset.data.model.User;
import no.ntnu.klubbhuset.data.model.VippsJsonProperties;
import no.ntnu.klubbhuset.data.model.VippsPaymentDetails;
import no.ntnu.klubbhuset.util.CommunicationConfig;
import no.ntnu.klubbhuset.util.Json;
import no.ntnu.klubbhuset.util.PreferenceUtils;

import static no.ntnu.klubbhuset.data.Resource.loading;
import static no.ntnu.klubbhuset.data.model.VippsJsonProperties.ACCESS_TOKEN;
import static no.ntnu.klubbhuset.data.model.VippsJsonProperties.APPLICATION_JSON;
import static no.ntnu.klubbhuset.data.model.VippsJsonProperties.AUTHORIZATION;
import static no.ntnu.klubbhuset.data.model.VippsJsonProperties.CLIENT_ID_STRING;
import static no.ntnu.klubbhuset.data.model.VippsJsonProperties.CLIENT_SECRET_STRING;
import static no.ntnu.klubbhuset.data.model.VippsJsonProperties.CONTENT_TYPE;
import static no.ntnu.klubbhuset.data.model.VippsJsonProperties.ECOMM_V_2_PAYMENTS;
import static no.ntnu.klubbhuset.data.model.VippsJsonProperties.EXT_EXPIRES_ON;
import static no.ntnu.klubbhuset.data.model.VippsJsonProperties.OCP_APIM_SUBSCRIPTION_KEY_STRING;
import static no.ntnu.klubbhuset.data.model.VippsJsonProperties.UTF_8;
import static no.ntnu.klubbhuset.data.model.VippsJsonProperties.VIPPS_URL;
import static no.ntnu.klubbhuset.util.PreferenceUtils.PREF_NO_FILE_FOUND;

public class VippsRepository {
    private static VippsRepository ourInstance;
    private RequestQueue requestQueue;
    private static final String TAG = "VippsRepository";
    private Context context;


    private Cache cache = Cache.getInstance();
    private VippsCache vippsCache = VippsCache.getInstance();
    private LiveData<Resource<String>> deeplink;

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
        if (prefToken.equals(PREF_NO_FILE_FOUND)) {
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
                map.put(CLIENT_ID_STRING, CommunicationConfig.getInstance().retrieveClientID());
                map.put(CLIENT_SECRET_STRING, CommunicationConfig.getInstance().retrieveClientSecret());
                map.put(OCP_APIM_SUBSCRIPTION_KEY_STRING, CommunicationConfig.getInstance().retrieveOcpApimSubscriptionKey());
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
        try {
            JSONObject json = new JSONObject(token);
            long expiryTime = json.getLong(EXT_EXPIRES_ON);
            long currentTime = TimeUnit.MILLISECONDS.toSeconds(System.currentTimeMillis());
            return currentTime > expiryTime;
        } catch (JSONException e) {
            throw new RuntimeException(TAG + " failed to parse received Vipps token");
        }
    }


    public LiveData<Resource<String>> getDeepLink(Resource<User> user, Club focusedClub) {
        deeplink = Transformations.switchMap(getToken(), stringResource -> {
            return loadDeepLink(user, focusedClub, stringResource);
        });
        return deeplink;
    }

    private LiveData<Resource<String>> loadDeepLink(Resource<User> user, Club focusedClub, Resource<String> token) {
        MutableLiveData returnValue = new MutableLiveData(Resource.loading());

        String bearer = null;
        try {
            bearer = new JSONObject(token.getData()).getString(ACCESS_TOKEN);
        } catch (JSONException e) {
            e.printStackTrace();
        }
        if (bearer == null) {
            returnValue.setValue(Resource.error("Error getting access token", null));
            return returnValue;
        }
        String finalBearer = bearer;
        VippsPaymentDetails details = getVippsPaymentDetails(user.getData(), focusedClub);
        JSONObject body = null;
        try {
            body = details.getBody();
        } catch (JSONException e) {
            e.printStackTrace();
        }
        JsonObjectRequest request = new JsonObjectRequest(Request.Method.POST, VIPPS_URL + ECOMM_V_2_PAYMENTS, body,
                response -> {
                    try {
                        String vippsDeepLink = (String) response.get("url");
                        returnValue.setValue(Resource.success(vippsDeepLink));
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                }, error -> {
            error.printStackTrace();
            Log.d(TAG, "retriveDeepLink: error: " + error);
            String responseBody;
            //get status code here
            if (error.networkResponse != null) {
                String statusCode = String.valueOf(error.networkResponse.statusCode);
                returnValue.setValue(Resource.error(statusCode, error));
                //get response body and parse with appropriate encoding
                if (error.networkResponse.data != null) {
                    try {
                        responseBody = new String(error.networkResponse.data, UTF_8);
                        Log.d(TAG, "retriveDeepLink: responsebody: " + responseBody);
                    } catch (UnsupportedEncodingException e) {
                        e.printStackTrace();
                    }
                }
            }

        }) {
            @Override
            public Map<String, String> getHeaders() {
                Map<String, String> headers = new HashMap<>();
                headers.put(AUTHORIZATION, VippsJsonProperties.BEARER + finalBearer);
                headers.put(CONTENT_TYPE, APPLICATION_JSON);
                headers.put(OCP_APIM_SUBSCRIPTION_KEY_STRING, CommunicationConfig.getInstance(context).retrieveOcpApimSubscriptionKey());
                return headers;
            }
        };
        requestQueue.add(request);
        return returnValue;
    }

    public VippsPaymentDetails getVippsPaymentDetails(User user, Club club) {


        String phoneNumber = user.getPhonenumber().substring(user.getPhonenumber().length() - 8); // getting last 8 digits of phonenumber number. This to avoid country code
        String organizationId = String.valueOf(club.getOid());
        String userId = user.getEmail();
        OrderId orderId;

        if (organizationId.length() > 20 || userId.length() > 20 || (organizationId + userId).trim().length() > 20) {
            orderId = new OrderId("", ""); // order id can only be 30 charachers long. timestamp is 10 chars long.
        } else {
            orderId = new OrderId(organizationId, userId);
        }

        Double amount = club.getPriceOfMembership().doubleValue();

        String transactionText = String.format("%s %s %s %s",
                context.getResources().getString(R.string.membership_of),
                club.getName(),
                context.getResources().getString(R.string.for_year),
                Calendar.getInstance().get(Calendar.YEAR));

        return new VippsPaymentDetails(phoneNumber, orderId, amount, transactionText, context);
    }
}
