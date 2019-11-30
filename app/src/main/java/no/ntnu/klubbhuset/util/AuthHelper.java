package no.ntnu.klubbhuset.util;

import android.app.Application;
import android.content.Context;
import android.content.SharedPreferences;

import com.android.volley.AuthFailureError;

import java.util.HashMap;
import java.util.Map;


public class AuthHelper {

    /**
     * Fetches and creates the necessary headers to authorize using a bearer token
     * Intended for use in override method "getParams" in Volley requests:
     * -    @Override
     * -    public Map<String, String> getHeaders() throws AuthFailureError {
     * -    return AuthHelper.getAuthHeaders(getApplication());
     * }
     *
     * @param context the application context, required to fetch sharedPreferences
     * @return a Map containing necessary headers for authorization
     * @throws AuthFailureError
     */
    public static Map<String, String> getAuthHeaders(Context context) throws AuthFailureError {

        String token = getStoredToken(context);

        Map<String, String> headers;
        if (!token.isEmpty()) {
            headers = new HashMap<>();
            String auth = "Bearer " + token;
            headers.put("Content-Type", "application/json");
            headers.put("Authorization", auth);
        } else {
            throw new AuthFailureError("Error: not signed in!");
        }
        return headers;
    }

    private static String getStoredToken(Context context) {
        SharedPreferences pref = context.getSharedPreferences("login", Context.MODE_PRIVATE);
        return pref.getString("token", "");
    }
}
