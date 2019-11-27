package no.ntnu.klubbhuset.viewmodels;

import android.app.Application;
import android.content.Context;
import android.content.SharedPreferences;

import androidx.lifecycle.AndroidViewModel;
import androidx.lifecycle.MutableLiveData;

import com.android.volley.RequestQueue;
import com.android.volley.toolbox.Volley;

import no.ntnu.klubbhuset.data.Result;
import no.ntnu.klubbhuset.data.model.User;
import no.ntnu.klubbhuset.data.repository.UserRepository;

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
            Result data = userRepository.get();
            if (data instanceof Result.Success);
        }
        return user;
    }


}
