package no.ntnu.klubbhuset.ui.login;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProviders;

import no.ntnu.klubbhuset.R;
import no.ntnu.klubbhuset.adapter.AuthenticationPageAdapter;
import no.ntnu.klubbhuset.data.Status;
import no.ntnu.klubbhuset.data.model.User;
import no.ntnu.klubbhuset.data.repository.UserRepository;
import no.ntnu.klubbhuset.viewmodels.LoginViewModel;

public class RegisterFragment extends Fragment {
    private EditText mFirstName;
    private EditText mLastName;
    private EditText mEmail;
    private EditText mPassword;
    private EditText mPhonenumber;

    private Button mCreateUser;
    private Button mCancel;

    private UserRepository userRepository;
    private LoginViewModel loginViewModel;

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        //userService = UserRe.
        loginViewModel = ViewModelProviders.of(getActivity()).get(LoginViewModel.class);

    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_new_user, container, false);
        bindFields(view);

        mCreateUser.setOnClickListener(v -> {
            loginViewModel.createUser(new User(
                    mFirstName.getText().toString(),
                    mLastName.getText().toString(),
                    mEmail.getText().toString(),
                    mPhonenumber.getText().toString(),
                    mPassword.getText().toString()))
                    .observe(this, response -> {
                        if (response.getStatus() == Status.SUCCESS) {
                            Toast.makeText(getContext(),
                                    "Success!",
                                    Toast.LENGTH_SHORT).show();

                        } else if (response.getStatus() == Status.ERROR){

                            if (response.getData(). statusCode == 403) {
                                Toast.makeText(
                                        getContext(),
                                        R.string.generic_error_response,
                                        Toast.LENGTH_SHORT).show();
                            } else if (response.getData().statusCode == 500) {
                                Toast.makeText(
                                        getContext(),
                                        R.string.userfeedback_cant_be_resolved,
                                        Toast.LENGTH_LONG).show();
                            }
                        }
            });
        });

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
