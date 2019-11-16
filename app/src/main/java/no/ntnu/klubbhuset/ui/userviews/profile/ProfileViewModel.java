package no.ntnu.klubbhuset.ui.userviews.profile;

import android.app.Application;
import android.content.Context;
import android.content.SharedPreferences;
import android.util.Base64;

import androidx.lifecycle.AndroidViewModel;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

import com.android.volley.AuthFailureError;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.Volley;

import java.io.InvalidObjectException;
import java.util.HashMap;
import java.util.Map;

import no.ntnu.klubbhuset.data.CommunicationConfig;
import no.ntnu.klubbhuset.data.model.User;
import no.ntnu.klubbhuset.util.AuthHelper;

public class ProfileViewModel extends AndroidViewModel {
    private RequestQueue requestQueue;

    private MutableLiveData<User> user;

    public ProfileViewModel(Application context) {
        super(context);
        this.requestQueue = Volley.newRequestQueue(context);
    }

    public MutableLiveData<User> getUser() {
        if (this.user == null) {
            this.user = new MutableLiveData<>();
            fetchUser();
        }
        return user;
    }

    private void fetchUser() {
        String url = CommunicationConfig.API_URL + "currentuser";

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
            public Map<String, String> getHeaders() throws AuthFailureError {
                return AuthHelper.getAuthHeaders(getApplication());
            }
        };
        requestQueue.add(request);
    }
}
