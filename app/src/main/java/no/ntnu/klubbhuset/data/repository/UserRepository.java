package no.ntnu.klubbhuset.data.repository;

import android.app.Application;
import android.content.Context;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;

import com.android.volley.AuthFailureError;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.Volley;

import java.util.Map;

import no.ntnu.klubbhuset.data.cache.Cache;
import no.ntnu.klubbhuset.data.Resource;
import no.ntnu.klubbhuset.data.model.User;
import no.ntnu.klubbhuset.util.AuthHelper;
import no.ntnu.klubbhuset.util.CommunicationConfig;
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

    private Context context;
    private final String ENDPOINT = API_URL + USER;
    private RequestQueue requestQueue;

    private Cache cache = Cache.getInstance();


    private UserRepository(Context context) {
        this.context = context;
        this.requestQueue = Volley.newRequestQueue(context);
    }

    public LiveData<Resource<User>> create(User user) {
        MutableLiveData created = new MutableLiveData();
        JsonObjectRequest request = new JsonObjectRequest(Request.Method.GET, ENDPOINT, null,
                response -> {
                    User newUser = Json.fromJson(response.toString(), User.class);
                    created.setValue(Resource.success(newUser));
                },
                error -> {
                    created.setValue(Resource.error(null, error));
                    System.out.println(error.networkResponse);
                }) {
            @Override
            public Map<String, String> getHeaders() throws AuthFailureError {
                return AuthHelper.getAuthHeaders(context);
            }
        };
        requestQueue.add(request);
        return created;
    }

    public LiveData<Resource<User>> get() {
        String url = CommunicationConfig.API_URL + USER;
        MutableLiveData<Resource<User>> cached = cache.getUser();
        if (cached.getValue() != null) {
            return cached;
        }

        JsonObjectRequest request = new JsonObjectRequest(Request.Method.GET, url, null,
                response -> {
                    User newUser = Json.fromJson(response.toString(), User.class);
                    cached.setValue(Resource.success(newUser));
                },
                error -> {
                    // error
                    System.out.println(error.networkResponse);
                    cached.setValue(Resource.error("Error fetching user", error));
                }) {
            @Override
            public Map<String, String> getHeaders() throws AuthFailureError {
                return AuthHelper.getAuthHeaders(context);
            }
        };
        requestQueue.add(request);
        return cached;
    }

    public LiveData<Resource<String>> delete() {
        throw new UnsupportedOperationException("TODO: Implement method");
    }
}
