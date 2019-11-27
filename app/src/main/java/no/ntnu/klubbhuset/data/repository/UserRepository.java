package no.ntnu.klubbhuset.data.repository;

import android.app.Application;

import com.android.volley.AuthFailureError;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.Volley;

import java.util.Map;

import no.ntnu.klubbhuset.data.Cache;
import no.ntnu.klubbhuset.data.Result;
import no.ntnu.klubbhuset.data.model.User;
import no.ntnu.klubbhuset.util.AuthHelper;
import no.ntnu.klubbhuset.util.Json;

import static no.ntnu.klubbhuset.util.CommunicationConfig.API_URL;
import static no.ntnu.klubbhuset.util.CommunicationConfig.USER;

public class UserRepository {
    private static UserRepository ourInstance;

    public static UserRepository getInstance(Application context) {
        if (ourInstance == null) {
            ourInstance = new UserRepository(context);
        }
        return ourInstance;
    }

    private Application context;
    private RequestQueue requestQueue;
    private final String ENDPOINT = API_URL + USER;

    private Result<User> result;
    private Cache cache = Cache.getInstance();


    private UserRepository(Application context) {
        this.context = context;
        this.requestQueue = Volley.newRequestQueue(context);
    }

    public Result<User> create(User user) {
        JsonObjectRequest request = new JsonObjectRequest(Request.Method.GET, ENDPOINT, null,
                response -> {
                    User newUser = Json.fromJson(response.toString(), User.class);
                    result = new Result.Success<>(newUser);
                },
                error -> {
                    result = new Result.Error(error);
                    System.out.println(error.networkResponse);
                }) {
            @Override
            public Map<String, String> getHeaders() throws AuthFailureError {
                return AuthHelper.getAuthHeaders(context);
            }
        };
        requestQueue.add(request);
        return result;
    }

    public Result<User> get() {
        throw new UnsupportedOperationException("TODO: Implement method");
    }

    public Result<String> delete() {
        throw new UnsupportedOperationException("TODO: Implement method");
    }
}
