package no.ntnu.klubbhuset.ui.login;

import android.app.Activity;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.EditorInfo;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.annotation.StringRes;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProviders;

import lombok.NoArgsConstructor;
import no.ntnu.klubbhuset.R;

@NoArgsConstructor
public class LoginFragment extends Fragment {
    private static final String TAG = "LoginFragment";

    LoginViewModel loginViewModel;

    EditText username, password;
    Button loginButton;
    private ProgressBar loadingProgressBar;

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        loginViewModel = ViewModelProviders.of(getActivity()).get(LoginViewModel.class);
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_login, container, false);

        bindFields(view);

        loginButton.setOnClickListener(v -> {
            loginViewModel.login(username.getText().toString(), password.getText().toString());
        });

//        loginViewModel.getLoginFormState().observe(this, loginFormState -> {
//            if (loginFormState == null) {
//                return;
//            }
//            loginButton.setEnabled(loginFormState.isDataValid());
//            if (loginFormState.getUsernameError() != null) {
//                username.setError(getString(loginFormState.getUsernameError()));
//            }
//            if (loginFormState.getPasswordError() != null) {
//                password.setError(getString(loginFormState.getPasswordError()));
//            }
//        });

        loginViewModel.getLoginResult().observe(this, loginResult -> {
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
                });

        TextWatcher afterTextChangedListener = new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
                // ignore
            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                // ignore
            }

            @Override
            public void afterTextChanged(Editable s) {
                loginViewModel.loginDataChanged(username.getText().toString(),
                        password.getText().toString());
            }
        };
        username.addTextChangedListener(afterTextChangedListener);
        password.addTextChangedListener(afterTextChangedListener);
        password.setOnEditorActionListener((v, actionId, event) -> {
            if (actionId == EditorInfo.IME_ACTION_DONE) {
                loginViewModel.login(username.getText().toString(),
                        password.getText().toString());
            }
            return false;
        });


        return view;
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        loadingProgressBar = getActivity().findViewById(R.id.loading);
        loginButton.setOnClickListener(v -> {
            loadingProgressBar.setVisibility(View.VISIBLE);
            loginViewModel.login(username.getText().toString(),
                    password.getText().toString());
        });
    }

    private void bindFields(View view) {
        username = view.findViewById(R.id.username_input);
        password = view.findViewById(R.id.password_input);
        loginButton = view.findViewById(R.id.login_button);
    }

    private void showLoginFailed(@StringRes Integer errorString) {
        Toast.makeText(getActivity(), errorString, Toast.LENGTH_SHORT).show();
    }

    private void showHome() {
        getActivity().setResult(Activity.RESULT_OK);
        getActivity().finish();
    }
}
