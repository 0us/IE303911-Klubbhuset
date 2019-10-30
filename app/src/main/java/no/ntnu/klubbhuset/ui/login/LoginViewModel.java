package no.ntnu.klubbhuset.ui.login;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

import android.app.Application;
import android.content.Context;
import android.content.SharedPreferences;
import android.util.Patterns;

import no.ntnu.klubbhuset.data.LoginRepository;
import no.ntnu.klubbhuset.data.Result;
import no.ntnu.klubbhuset.data.model.LoggedInUser;
import no.ntnu.klubbhuset.R;

public class LoginViewModel extends ViewModel {

    private MutableLiveData<LoginFormState> loginFormState = new MutableLiveData<>();
    private MutableLiveData<LoginResult> loginResult = new MutableLiveData<>();
    private LoginRepository loginRepository;
    private SharedPreferences pref;
    private Application context;

    private final String LOGGED_IN = "loggedin";

    LoginViewModel(Application context, LoginRepository loginRepository) {
        this.loginRepository = loginRepository;
        this.context = context;
        this.pref = context.getSharedPreferences("login", Context.MODE_PRIVATE);
    }

    LiveData<LoginFormState> getLoginFormState() {
        return loginFormState;
    }

    LiveData<LoginResult> getLoginResult() {
        return loginResult;
    }

    public void login(String username, String password) {
        // can be launched in a separate asynchronous job
        Result<LoggedInUser> result = loginRepository.login(username, password);

        // TODO: save user credentials in prefs file
        SharedPreferences.Editor editor = pref.edit();
        if (result instanceof Result.Success) {
            LoggedInUser data = ((Result.Success<LoggedInUser>) result).getData();
            loginResult.setValue(new LoginResult(new LoggedInUserView(data.getDisplayName())));
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