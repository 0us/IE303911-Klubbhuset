package no.ntnu.klubbhuset.ui.login;

import android.app.Activity;
import android.os.Bundle;
import android.util.Log;
import android.widget.Toast;

import androidx.annotation.StringRes;
import androidx.appcompat.app.AppCompatActivity;
import androidx.lifecycle.ViewModelProviders;
import androidx.viewpager.widget.ViewPager;

import no.ntnu.klubbhuset.R;
import no.ntnu.klubbhuset.adapter.AuthenticationPageAdapter;
import no.ntnu.klubbhuset.viewmodels.LoginViewModel;
import no.ntnu.klubbhuset.viewmodels.LoginViewModelFactory;


public class LoginActivity extends AppCompatActivity {
    private static final String TAG = "LoginActivity";

    private LoginViewModel loginViewModel;
    AuthenticationPageAdapter authenticationPageAdapter;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        loginViewModel = ViewModelProviders.of(this, new LoginViewModelFactory(getApplication()))
                .get(LoginViewModel.class);
//        Log.d(TAG, "onCreate: setting content view");
//        setContentView(R.layout.activity_login);

        if (loginViewModel.isLoggedIn()) {
            showHome();
        } else {
            showLogin();
        }
    }

    void showHome() {
        setResult(Activity.RESULT_OK);
        finish();
    }

    private void showLogin() {
        setContentView(R.layout.activity_login);

        Log.d(TAG, "onCreate: init fragment");
        ViewPager viewPager = findViewById(R.id.view_pager);
        authenticationPageAdapter = new AuthenticationPageAdapter(getSupportFragmentManager());
        authenticationPageAdapter.addFragment(new LoginFragment());
        authenticationPageAdapter.addFragment(new RegisterFragment());
        viewPager.setAdapter(authenticationPageAdapter);

//        final EditText usernameEditText = findViewById(R.id.username);
//        final EditText passwordEditText = findViewById(R.id.password);
//        final Button loginButton = findViewById(R.id.login);
//        final Button createNewUserButton = findViewById(R.id.create_new_user_button);
        //final ProgressBar loadingProgressBar = (ProgressBar) findViewById(R.id.loading);

//        loginViewModel.getLoginFormState().observe(this, loginFormState -> {
//            if (loginFormState == null) {
//                return;
//            }
//            loginButton.setEnabled(loginFormState.isEmailValid());
//            if (loginFormState.getUsernameError() != null) {
//                usernameEditText.setError(getString(loginFormState.getUsernameError()));
//            }
//            if (loginFormState.getPasswordError() != null) {
//                passwordEditText.setError(getString(loginFormState.getPasswordError()));
//            }
//        });

        /*loginViewModel.getLoginResult().observe(this, loginResult -> {
            if (loginResult == null) {
                return;
            }
            loadingProgressBar.setVisibility(View.GONE);
            if (loginResult.getError() != null) {
                showLoginFailed(loginResult.getError());
            }
            if (loginResult.getSuccess() != null) {
                showHome();
            }

            showHome(); // TODO
        });*/

//        TextWatcher afterTextChangedListener = new TextWatcher() {
//            @Override
//            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
//                // ignore
//            }
//
//            @Override
//            public void onTextChanged(CharSequence s, int start, int before, int count) {
//                // ignore
//            }
//
//            @Override
//            public void afterTextChanged(Editable s) {
//                loginViewModel.loginDataChanged(usernameEditText.getText().toString(),
//                        passwordEditText.getText().toString());
//            }
//        };

//        usernameEditText.addTextChangedListener(afterTextChangedListener);
//        passwordEditText.addTextChangedListener(afterTextChangedListener);
//        passwordEditText.setOnEditorActionListener((v, actionId, event) -> {
//            if (actionId == EditorInfo.IME_ACTION_DONE) {
//                loginViewModel.login(usernameEditText.getText().toString(),
//                        passwordEditText.getText().toString());
//            }
//            return false;
//        });

//        loginButton.setOnClickListener(v -> {
//            loadingProgressBar.setVisibility(View.VISIBLE);
//            loginViewModel.login(usernameEditText.getText().toString(),
//                    passwordEditText.getText().toString());
//        });
    }

    private void showLoginFailed(@StringRes Integer errorString) {
        Toast.makeText(getApplicationContext(), errorString, Toast.LENGTH_SHORT).show();
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
//         forbid the user from going to home without being logged in
    }
}
