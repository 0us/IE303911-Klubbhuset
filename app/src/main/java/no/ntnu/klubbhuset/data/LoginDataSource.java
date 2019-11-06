package no.ntnu.klubbhuset.data;

import android.content.Context;
import android.util.Log;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;

import java.util.HashMap;
import java.util.Map;

import no.ntnu.klubbhuset.data.model.LoggedInUser;

import static no.ntnu.klubbhuset.data.CommunicationConfig.API_URL;

/**
 * Class that handles authentication w/ login credentials and retrieves user information.
 */
public class LoginDataSource {
    private static final String TAG = "LoginDataSource";
    private final String URL = API_URL;

    private RequestQueue requestQueue;
    private Result<LoggedInUser> result;
    private Context context;

    public LoginDataSource(Context context) {
        this.context = context;
        requestQueue = Volley.newRequestQueue(context);
    }

    public Result<LoggedInUser> login(String username, String password) {

        StringRequest request = new StringRequest(Request.Method.POST, URL,
                response -> {
                    Log.d(TAG, "login: response: " + response);
                    result = new Result.Success<>(response);
                },
                error -> {
                    Log.d(TAG, "login: error: " + error);
                    result = new Result.Error(error);
                }) {
            // adding data to the string request
            @Override
            protected Map<String, String> getParams() {
                Map<String, String> data = new HashMap<>();
                data.put("username", username);
                data.put("password", password);
                return data;
            }
        };
        requestQueue.add(request);
        return result;
    }

    public void logout() {
        // TODO: revoke authentication
    }
}
