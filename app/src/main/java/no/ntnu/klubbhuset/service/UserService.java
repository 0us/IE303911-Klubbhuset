package no.ntnu.klubbhuset.service;

import android.content.Context;
import android.util.Log;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.Volley;

import org.json.JSONException;
import org.json.JSONObject;

public class UserService {
    private static final String TAG = "UserService";
    RequestQueue queue; // fixme needs context
    final String URL = "http://10.22.195.81/Klubbhuset/api/user"; // todo needs other domain

    public UserService(Context context) {
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


        JsonObjectRequest request = new JsonObjectRequest(Request.Method.POST, URL, newUserJson,
                response -> {
                },
                error -> {
                    Log.d(TAG, "createNewUser: error");
                    Log.d(TAG, error.getMessage());
                });

        queue.add(request);
        return newUserJson;
    }

}
