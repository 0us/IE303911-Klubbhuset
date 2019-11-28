package no.ntnu.klubbhuset.util;

import android.app.Application;
import android.content.Context;
import android.content.SharedPreferences;

import com.android.volley.AuthFailureError;
import com.android.volley.Request;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.Volley;

import org.json.JSONException;

import java.io.InvalidObjectException;
import java.util.Map;

import no.ntnu.klubbhuset.data.model.User;

import static no.ntnu.klubbhuset.data.CommunicationConfig.API_URL;

public class UserHelper {
    private static User user;
    public static User getCurrentUser(Context context) throws JSONException {
        if(user == null) {
            retriveCurrentUser(context);
        }
        return user;
    }

    private static String getToken(Context context) {
        SharedPreferences preferences = context.getSharedPreferences("login", Context.MODE_PRIVATE);
        return preferences.getString("token", null);
    }

    private static void retriveCurrentUser(Context context) {
        JsonObjectRequest request = new JsonObjectRequest(Request.Method.GET,API_URL, null,
                response -> {
                    try {
                        user = new User(response);
                    } catch (InvalidObjectException e) {
                        e.printStackTrace();
                    }
                },
                error -> {}) {
            @Override
            public Map<String, String> getHeaders() throws AuthFailureError {
                return AuthHelper.getAuthHeaders((Application) context);
            }
        };
        Volley.newRequestQueue(context).add(request);
    }

}
