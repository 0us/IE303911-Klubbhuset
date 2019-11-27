package no.ntnu.klubbhuset.viewmodels;

import android.app.Application;
import android.content.Context;
import android.content.SharedPreferences;

import androidx.lifecycle.AndroidViewModel;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;

import com.android.volley.RequestQueue;
import com.android.volley.toolbox.Volley;

import no.ntnu.klubbhuset.data.Resource;
import no.ntnu.klubbhuset.data.Result;
import no.ntnu.klubbhuset.data.model.User;
import no.ntnu.klubbhuset.data.repository.UserRepository;

public class ProfileViewModel extends AndroidViewModel {

    private UserRepository userRepository;

    public ProfileViewModel(Application context) {
        super(context);
        this.userRepository = UserRepository.getInstance(getApplication());
    }

    public LiveData<Resource<User>> getUser() {
        return userRepository.get();
    }


}
