package no.ntnu.klubbhuset.data.repository;

import android.app.Application;
import android.content.Context;
import android.util.Log;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;

import com.android.volley.AuthFailureError;
import com.android.volley.NetworkResponse;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.toolbox.HttpHeaderParser;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.Volley;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.Map;

import no.ntnu.klubbhuset.data.Status;
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
    private final String TAG = "UserRepository";
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

    public LiveData<Resource<NetworkResponse>> create(User user) {
        MutableLiveData created = new MutableLiveData();
        JSONObject jsonUser = null;
        try {
            jsonUser = new JSONObject(Json.toJson(user));
        } catch (JSONException e) {
            created.setValue(Resource.error(e.getMessage(), null));
        }

        JsonObjectRequest request = new JsonObjectRequest(Request.Method.POST, ENDPOINT, jsonUser,
                response -> {
                    try {
                        NetworkResponse networkResponse = (NetworkResponse) response.get("response");
                        created.setValue(Resource.success(networkResponse));
                    } catch (JSONException e ) {
                        created.setValue(Resource.error("Could not parse json", null));
                    }
                },
                error -> {
                    created.setValue(Resource.error(null, error.networkResponse));
                }) {
            @Override
            protected Response<JSONObject> parseNetworkResponse(NetworkResponse response) {
                if (response.statusCode == 201) {
                    try {
                        return Response.success(
                                new JSONObject().put("response", response),
                                HttpHeaderParser.parseCacheHeaders(response));
                    } catch (JSONException e) {
                        return super.parseNetworkResponse(response);
                    }
                } else return super.parseNetworkResponse(response);
            }
        };
        requestQueue.add(request);
        return created;
    }

    public LiveData<Resource<User>> get() {
        MutableLiveData<Resource<User>> cached = cache.getUser();
        if (cached.getValue() != null) {
            return cached;
        }

        JsonObjectRequest request = new JsonObjectRequest(Request.Method.GET, ENDPOINT, null,
                response -> {
                    User newUser = Json.fromJson(response.toString(), User.class);
                    cached.setValue(Resource.success(newUser));
                },
                error -> {
                    // error
                    Log.e(TAG, error.networkResponse.toString());
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
