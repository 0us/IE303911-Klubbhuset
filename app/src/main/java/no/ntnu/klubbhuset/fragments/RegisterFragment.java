package no.ntnu.klubbhuset.fragments;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProviders;

import no.ntnu.klubbhuset.R;
import no.ntnu.klubbhuset.service.UserService;
import no.ntnu.klubbhuset.ui.login.LoginViewModel;

public class RegisterFragment extends Fragment {
    EditText mFirstName;
    EditText mLastName;
    EditText mEmail;
    EditText mPassword;
    EditText mPhonenumber;

    Button mCreateUser;
    Button mCancel;

    UserService userService;
    LoginViewModel loginViewModel;

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        userService = new UserService(getActivity());
        loginViewModel = ViewModelProviders.of(getActivity()).get(LoginViewModel.class);

    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_new_user, container, false);
        bindFields(view);

        mCreateUser.setOnClickListener(v -> userService.createNewUser(
                mFirstName.getText().toString(),
                mLastName.getText().toString(),
                mEmail.getText().toString(),
                mPhonenumber.getText().toString(),
                mPassword.getText().toString()
        ));

        return view;
    }

    private void bindFields(View view) {
        mFirstName = view.findViewById(R.id.firstname_input);
        mLastName = view.findViewById(R.id.lastname_input);
        mEmail = view.findViewById(R.id.email_input);
        mPassword = view.findViewById(R.id.password_input);
        mPhonenumber = view.findViewById(R.id.phonenumber_input);
        mCreateUser = view.findViewById(R.id.create_user_submit);
        mCancel = view.findViewById(R.id.cancel_new_user_submit);
    }
}
