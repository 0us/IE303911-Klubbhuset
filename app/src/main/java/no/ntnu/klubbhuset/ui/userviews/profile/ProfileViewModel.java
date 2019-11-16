package no.ntnu.klubbhuset.ui.userviews.profile;

import android.app.Application;
import android.content.Context;
import android.content.SharedPreferences;

import androidx.lifecycle.AndroidViewModel;
import androidx.lifecycle.MutableLiveData;

import com.android.volley.AuthFailureError;
import com.android.volley.NetworkResponse;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.Volley;

import org.json.JSONObject;

import java.io.InvalidObjectException;
import java.util.HashMap;
import java.util.Map;

import no.ntnu.klubbhuset.data.CommunicationConfig;
import no.ntnu.klubbhuset.data.model.User;

import static no.ntnu.klubbhuset.data.CommunicationConfig.USER;

public class ProfileViewModel extends AndroidViewModel {
    private final SharedPreferences pref;
    private RequestQueue requestQueue;

    private MutableLiveData<User> user;

    public ProfileViewModel(Application context) {
        super(context);
        this.requestQueue = Volley.newRequestQueue(context);
        this.pref = getApplication().getSharedPreferences("login", Context.MODE_PRIVATE);
    }

    public MutableLiveData<User> getUser() {
        if (this.user == null) {
            this.user = new MutableLiveData<>();
            fetchUser();
        }
        return user;
    }

    private void fetchUser() {
        String url = CommunicationConfig.API_URL + USER;
        JsonObjectRequest request = new JsonObjectRequest(Request.Method.GET, url, null,
                response -> {
                    User newUser = null;
                    try {
                        newUser = new User(response);
                    } catch (InvalidObjectException e) {
                        e.printStackTrace();
                    }
                    user.setValue(newUser);
                },
                error -> {
                    // error
                    System.out.println(error.networkResponse);
                }) {
            @Override
            public Map<String, String> getHeaders() {
                Map<String, String> params = new HashMap<>();
                params.put("Content-Type", "application/json; charset=UTF-8");
                params.put("Authorization", "Bearer " + pref.getString("token", ""));
                return params;
            }
        };

        requestQueue.add(request);
    }
}
