package no.ntnu.klubbhuset.ui.managerviews.barcode;

import android.app.Application;
import android.util.Log;

import androidx.annotation.NonNull;
import androidx.lifecycle.AndroidViewModel;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;

import com.android.volley.AuthFailureError;
import com.android.volley.NetworkResponse;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.HttpHeaderParser;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.Volley;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.Map;
import java.util.Objects;

import no.ntnu.klubbhuset.R;
import no.ntnu.klubbhuset.data.model.Club;
import no.ntnu.klubbhuset.util.AuthHelper;

import static no.ntnu.klubbhuset.data.CommunicationConfig.checkHasPaid;

public class BarcodeViewModel extends AndroidViewModel {

    private static final String TAG = "BarcodeViewModel";
    private static final String JSON_MSG = "msg";
    public static final String PAYMENT_STATUS_OK = "OK!";
    public static final String PAYMENT_STATUS_NOT_OK = "NOT PAYED!";
    public static final String MEMBER_NOT_FOUND = "NOT FOUND!";
    private final RequestQueue requestQueue;
    private MutableLiveData<String> userPaymentStatus;

    public BarcodeViewModel(@NonNull Application context) {
        super(context);
        requestQueue = Volley.newRequestQueue(context);
    }

    public LiveData<String> getUserPaymentStatus(String email, Club club) {
        if (userPaymentStatus == null) {
            userPaymentStatus = new MutableLiveData<>();
            loadUserPaymentStatus(email, club);
        }
        loadUserPaymentStatus(email, club);
        return userPaymentStatus;

    }

    /**
     * Checks wether the user has paid for the membership of a given organization
     *
     * @param email the email of the user to check
     */
    private void loadUserPaymentStatus(String email, Club club) {
        String url = checkHasPaid(club.getOid());
        try {
            JSONObject jsonObject = new JSONObject().put("email", email);
            JsonObjectRequest request = new JsonObjectRequest(
                    Request.Method.POST,
                    url,
                    jsonObject,
                    response -> {
                        try {
                            userPaymentStatus.setValue(response.getString(JSON_MSG));
                        } catch (JSONException e) {
                            Log.e(TAG, "Someting went wrong with parsing response. Response from server was: " + response.toString());
                        }
                    },
                    error -> {
                        if (error.networkResponse != null) {
                            if (error.networkResponse.statusCode == 402) {
                                String message = new String(error.networkResponse.data);
                                userPaymentStatus.setValue(message);
                            }
                        } else {
                            userPaymentStatus.setValue("MEMBER NOT FOUND");
                        }
                        error.printStackTrace();
                    }
            ) {

                @Override
                protected Response<JSONObject> parseNetworkResponse(NetworkResponse response) {
                    try {
                        JSONObject jsonObject = new JSONObject();
                        int statusCode = response.statusCode;

                        switch (statusCode) {

                            case 204:
                                jsonObject.put(JSON_MSG, MEMBER_NOT_FOUND);
                                break;

                            case 402:
                                jsonObject.put(JSON_MSG, PAYMENT_STATUS_NOT_OK);
                                break;

                            case 200:
                                jsonObject.put(JSON_MSG, PAYMENT_STATUS_OK);
                                break;
                        }

                        return Response.success(jsonObject, HttpHeaderParser.parseCacheHeaders(response));
                    } catch (JSONException e) {
                        return Response.error(new VolleyError(response));
                    }
                }

                @Override
                public Map<String, String> getHeaders() throws AuthFailureError {
                    return AuthHelper.getAuthHeaders(getApplication());
                }
            };
            requestQueue.add(request);
        } catch (JSONException e) {
            Log.e(TAG, Objects.requireNonNull(e.getMessage()));
        }

    }
}