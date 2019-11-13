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
import android.util.Patterns;

import no.ntnu.klubbhuset.data.model.LoggedInUser;
import no.ntnu.klubbhuset.R;

public class LoginViewModel extends AndroidViewModel {

    private MutableLiveData<LoginFormState> loginFormState = new MutableLiveData<>();
    private MutableLiveData<LoginResult> loginResult = new MutableLiveData<>();
    private LoginRepository loginRepository;
    private SharedPreferences pref;
    private Application context;

    private final String LOGGED_IN = "loggedin";
    private final String TOKEN = "token";

//    LoginViewModel(Application context, LoginRepository loginRepository) {
//        this.loginRepository = loginRepository;
//        this.context = context;
//        this.pref = context.getSharedPreferences("login", Context.MODE_PRIVATE);
//    }


    public LoginViewModel(@NonNull Application application, LoginRepository loginRepository) {
        super(application);
        this.loginRepository = loginRepository;
        this.pref = getApplication().getSharedPreferences("login", Context.MODE_PRIVATE);
    }

    LiveData<LoginFormState> getLoginFormState() {
        return loginFormState;
    }

    LiveData<LoginResult> getLoginResult() {
        return loginResult;
    }

    public void login(String email, String password) {

        Result result = loginRepository.login(email, password);

        SharedPreferences.Editor editor = pref.edit();
        if (result instanceof Result.Success) {
            LoggedInUser data = ((Result.Success<LoggedInUser>) result).getData();
            loginResult.setValue(new LoginResult(new LoggedInUserView(data.getDisplayName())));
            editor.putString(TOKEN, data.getToken());
            editor.putBoolean(LOGGED_IN, true);
        } else {
            loginResult.setValue(new LoginResult(R.string.login_failed));
            editor.putBoolean(LOGGED_IN, false);
        }
        editor.apply();
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
