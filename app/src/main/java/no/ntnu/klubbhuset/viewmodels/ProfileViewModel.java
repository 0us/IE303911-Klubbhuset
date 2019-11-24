package no.ntnu.klubbhuset.viewmodels;

import android.app.Application;
import android.content.Context;
import android.content.SharedPreferences;
import android.content.Context;
import android.content.SharedPreferences;
import android.util.Base64;

import androidx.lifecycle.AndroidViewModel;
import androidx.lifecycle.MutableLiveData;

import com.android.volley.AuthFailureError;
import com.android.volley.NetworkResponse;
import com.android.volley.AuthFailureError;
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
import no.ntnu.klubbhuset.data.repository.UserRepository;
import no.ntnu.klubbhuset.util.AuthHelper;

import static no.ntnu.klubbhuset.data.CommunicationConfig.USER;

public class ProfileViewModel extends AndroidViewModel {
    private final SharedPreferences pref;
    private RequestQueue requestQueue;

    private MutableLiveData<User> user;

    private UserRepository userRepository;

    public ProfileViewModel(Application context) {
        super(context);
        this.requestQueue = Volley.newRequestQueue(context);
        this.pref = getApplication().getSharedPreferences("login", Context.MODE_PRIVATE);
        this.userRepository = UserRepository.getInstance(getApplication());
    }

    public MutableLiveData<User> getUser() {
        if (this.user == null) {
            this.user = new MutableLiveData<>();
            fetchUser();
        }
        return user;
    }

    private void fetchUser() {
        userRepository.get();
    }
}
