package no.ntnu.klubbhuset.ui.login;

import androidx.annotation.NonNull;
import androidx.lifecycle.AndroidViewModel;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;

import android.annotation.SuppressLint;
import android.app.Application;
import android.content.Context;
import android.content.SharedPreferences;
import android.os.AsyncTask;
import android.util.Log;
import android.util.Patterns;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;

import java.util.HashMap;
import java.util.Map;

import no.ntnu.klubbhuset.data.model.LoggedInUser;
import no.ntnu.klubbhuset.R;

import static no.ntnu.klubbhuset.data.CommunicationConfig.API_URL;
import static no.ntnu.klubbhuset.data.CommunicationConfig.LOGIN;

public class LoginViewModel extends AndroidViewModel {

    private final RequestQueue requestQueue;
    private MutableLiveData<LoginFormState> loginFormState = new MutableLiveData<>();
    private MutableLiveData<LoginResult> loginResult = new MutableLiveData<>();
    private LoginRepository loginRepository;
    private SharedPreferences pref;

    private final String LOGGED_IN = "loggedin";
    private final String TOKEN = "token";

    private static final String TAG = "LoginDataSource";
    private final String URL = API_URL + LOGIN;
    private LoggedInUser user;

//    LoginViewModel(Application context, LoginRepository loginRepository) {
//        this.loginRepository = loginRepository;
//        this.context = context;
//        this.pref = context.getSharedPreferences("login", Context.MODE_PRIVATE);
//    }


    public LoginViewModel(@NonNull Application application, LoginRepository loginRepository) {
        super(application);
        this.loginRepository = loginRepository;
        this.pref = getApplication().getSharedPreferences("login", Context.MODE_PRIVATE);
        requestQueue = Volley.newRequestQueue(application.getApplicationContext());
    }

    LiveData<LoginFormState> getLoginFormState() {
        return loginFormState;
    }

    LiveData<LoginResult> getLoginResult() {
        return loginResult;
    }

    private void setLoggedInUser(LoggedInUser user) {
        this.user = user;
    }

    public void login(String email, String password) {
        SharedPreferences.Editor editor = pref.edit();
        StringRequest request = new StringRequest(Request.Method.POST, URL,
                response -> {
                    Log.d(TAG, "login: response: " + response);
                    LoggedInUser user = new LoggedInUser(email, response);
                    Result result = new Result.Success<>(user);
                    setLoggedInUser(((Result.Success<LoggedInUser>) result).getData());
                    loginResult.setValue(new LoginResult(new LoggedInUserView(user.getDisplayName())));
                    editor.putString(TOKEN, user.getToken());
                    editor.putBoolean(LOGGED_IN, true);
                    editor.apply();
                },
                error -> {
                    Log.d(TAG, "login: error: " + error);
                    Result result = new Result.Error(error);
                    loginResult.setValue(new LoginResult(R.string.login_failed));
                    editor.putBoolean(LOGGED_IN, false);
                    editor.apply();
                }) {
            // adding data to the string request
            @Override
            protected Map<String, String> getParams() {
                Map<String, String> data = new HashMap<>();
                data.put("email", email);
                data.put("password", password);
                return data;
            }
        };
        requestQueue.add(request);


//        Result result = loginRepository.login(email, password);
//
//        SharedPreferences.Editor editor = pref.edit();
//        if (result instanceof Result.Success) {
//            LoggedInUser data = ((Result.Success<LoggedInUser>) result).getData();
//            loginResult.setValue(new LoginResult(new LoggedInUserView(data.getDisplayName())));
//            editor.putString(TOKEN, data.getToken());
//            editor.putBoolean(LOGGED_IN, true);
//        } else {
//            loginResult.setValue(new LoginResult(R.string.login_failed));
//            editor.putBoolean(LOGGED_IN, false);
//        }
//        editor.apply();
    }

    public boolean isLoggedIn() {
        return pref.getBoolean(LOGGED_IN, false);
    }

    public void loginDataChanged(String username, String password) {
        if (!isUserNameValid(username)) {
            loginFormState.setValue(new LoginFormState(R.string.invalid_username, null));
        } else if (!isPasswordValid(password)) {
            loginFormState.setValue(new LoginFormState(null, R.string.invalid_password));
        } else {
            loginFormState.setValue(new LoginFormState(true));
        }
    }

    // A placeholder username validation check
    private boolean isUserNameValid(String username) {
        if (username == null) {
            return false;
        }
        if (username.contains("@")) {
            return Patterns.EMAIL_ADDRESS.matcher(username).matches();
        } else {
            return !username.trim().isEmpty();
        }
    }

    // A placeholder password validation check
    private boolean isPasswordValid(String password) {
        return password != null && password.trim().length() > 5;
    }
}
