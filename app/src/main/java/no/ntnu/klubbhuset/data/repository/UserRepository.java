package no.ntnu.klubbhuset.data.repository;

import android.app.Application;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;

import com.android.volley.AuthFailureError;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.Volley;

import java.util.Map;

import no.ntnu.klubbhuset.data.Cache;
import no.ntnu.klubbhuset.data.Resource;
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

    private Cache cache = Cache.getInstance();
    private Resource<User> created;


    private UserRepository(Application context) {
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
        throw new UnsupportedOperationException("TODO: Implement method");
    }

    public LiveData<Resource<String>> delete() {
        throw new UnsupportedOperationException("TODO: Implement method");
    }
}
