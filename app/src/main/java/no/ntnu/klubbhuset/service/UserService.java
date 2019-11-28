package no.ntnu.klubbhuset.service;

import android.content.Context;
import android.content.Intent;
import android.util.Log;
import android.widget.Toast;

import com.android.volley.AuthFailureError;
import com.android.volley.NetworkResponse;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyLog;
import com.android.volley.toolbox.HttpHeaderParser;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.UnsupportedEncodingException;

import no.ntnu.klubbhuset.ui.login.LoginActivity;

import static no.ntnu.klubbhuset.data.CommunicationConfig.API_URL;
import static no.ntnu.klubbhuset.data.CommunicationConfig.USER;

public class UserService {
    private static final String TAG = "UserService";
    RequestQueue queue; // fixme needs context

    final String URL =  API_URL + USER;// todo needs other domain
    Context context;

    public UserService(Context context) {
        this.context = context;
        queue = Volley.newRequestQueue(context);
    }

    public JSONObject createNewUser(String firstname, String lastname, String email, String phonenumber, String password) {
        Log.d(TAG, "createNewUser: called");
        JSONObject newUserJson = new JSONObject();
        try {
            newUserJson.put("firstName", firstname);
            newUserJson.put("lastName", lastname);
            newUserJson.put("email", email);
            newUserJson.put("phonenumber", phonenumber);
            newUserJson.put("password", password);
        } catch (JSONException e) {
            e.printStackTrace();
        }
        String requestBody = newUserJson.toString();

        StringRequest jsonObjectRequest = new StringRequest(Request.Method.POST, URL,
                response -> {
                    Log.d(TAG, "createNewUser: got response" + response);
                    Toast.makeText(context,"Profile created. You can now login", Toast.LENGTH_LONG).show();
                    Intent intent = new Intent(context, LoginActivity.class);
                    context.startActivity(intent);
                },
                error -> {
                    Log.d(TAG, "createNewUser: error");
                    if ( error != null) {
                        // todo handle error
                        Log.d(TAG, "createNewUser: Error: " + error);
                    }
                }) {
            @Override
            public String getBodyContentType() {
                return "application/json; charset=utf-8";
            }

            @Override
            public byte[] getBody() throws AuthFailureError {
                try {
                    return requestBody == null ? null : requestBody.getBytes("utf-8");
                } catch (UnsupportedEncodingException e) {
                    VolleyLog.wtf("Unsupported Encoding while trying to get the bytes of %s using %s", requestBody, "utf-8");
                    e.printStackTrace();
                    return null;
                }
            }

            @Override
            protected Response<String> parseNetworkResponse(NetworkResponse response) {
                String responseString = "";
                if (response != null) {
                    responseString = String.valueOf(response.statusCode);
                    // can get more details such as response.headers
                }
                return Response.success(responseString, HttpHeaderParser.parseCacheHeaders(response));
            }
        };

        queue.add(jsonObjectRequest);
        return newUserJson;
    }

}