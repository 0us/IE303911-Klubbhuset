package no.ntnu.klubbhuset.service;

import android.content.Context;
import android.util.Log;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.Volley;

import org.json.JSONException;
import org.json.JSONObject;

import static no.ntnu.klubbhuset.data.CommunicationConfig.API_URL;
import static no.ntnu.klubbhuset.data.CommunicationConfig.USER;

public class UserService {
    private static final String TAG = "UserService";
    RequestQueue queue; // fixme needs context
    final String URL =  API_URL + USER;// todo needs other domain

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


        JsonObjectRequest jsonObjectRequest = new JsonObjectRequest(Request.Method.POST, URL, newUserJson,
                response -> {
                    Log.d(TAG, "createNewUser: got response" + response);
                    // todo. Start main activity. send response to the activity
                },
                error -> {
                    Log.d(TAG, "createNewUser: error");
                    if ( error != null) {
                        // todo handle error
                        Log.d(TAG, "createNewUser: Error" + error);
                    }
                });

        queue.add(jsonObjectRequest);
        return newUserJson;
    }

}
