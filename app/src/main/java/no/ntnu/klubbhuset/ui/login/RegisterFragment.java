package no.ntnu.klubbhuset.ui.login;

import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
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
import no.ntnu.klubbhuset.data.Status;
import no.ntnu.klubbhuset.data.model.User;
import no.ntnu.klubbhuset.data.repository.UserRepository;
import no.ntnu.klubbhuset.viewmodels.LoginViewModel;

public class RegisterFragment extends Fragment {
    private static final String TAG = "RegisterFragment";
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

        mCreateUser.setEnabled(false);

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

        // Retrieve errors for each input field
        loginViewModel.getRegFormState().observe(this, regFormState -> {
            if (regFormState.getFirstNameError() != null) {
                mFirstName.setError(getString(regFormState.getFirstNameError()));
            }
            if (regFormState.getLastNameError() != null) {
                mLastName.setError(getString(regFormState.getLastNameError()));
            }
            if (regFormState.getEmailError() != null) {
                mEmail.setError(getString(regFormState.getEmailError()));
            }
            if (regFormState.getPasswordError() != null) {
                mPassword.setError(getString(regFormState.getPasswordError()));
            }
            if (regFormState.getPhoneNumberError() != null) {
                mPhonenumber.setError(getString(regFormState.getPhoneNumberError()));
            }
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

        mFirstName.addTextChangedListener(textWatcher);
        mLastName.addTextChangedListener(textWatcher);
        mEmail.addTextChangedListener(textWatcher);
        mPassword.addTextChangedListener(textWatcher);
        mPhonenumber.addTextChangedListener(textWatcher);
    }

    /**
     * Watches for changes to the text in the forms field.
     */
    private TextWatcher textWatcher = new TextWatcher() {
        @Override
        public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

        }

        @Override
        public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {
        }

        /**
         * Will be called every time a character has been inserted to the form.
         *
         * @param editable
         */
        @Override
        public void afterTextChanged(Editable editable) {
            loginViewModel.organizationDataChanged(
                    mFirstName.getText().toString(),
                    mLastName.getText().toString(),
                    mEmail.getText().toString(),
                    mPassword.getText().toString(),
                    mPhonenumber.getText().toString());

            if (loginViewModel.getRegFormState().getValue().isDataValid()) {
                mCreateUser.setEnabled(true);
            } else {
                mCreateUser.setEnabled(false);
            }
        }
    };

//    private boolean fieldsAreValid() {
//        boolean firstName = false;
//        boolean lastName = false;
//        boolean email = false;
//        boolean password = false;
//        boolean phonenumber = false;
//
//        try {
//            firstName = !(this.mFirstName.getText().toString().isEmpty());
//            lastName = !(this.mLastName.getText().toString().isEmpty());
//            password = !(this.mPassword.getText().toString().isEmpty());
//            phonenumber = !(this.mPhonenumber.getText().toString().isEmpty());
//        } catch (NullPointerException e) {
//            Log.e(TAG, e.getMessage());
//        }
//
//        if (!firstName) {
//            this.mFirstName.setError(getString(R.string.field_cant_be_empty));
//        }
//        if (!lastName) {
//            this.mLastName.setError(getString(R.string.field_cant_be_empty));
//        }
//        if (!loginViewModel.getRegFormState().getValue().isEmailValid()) {
//            this.mEmail.setError(getText(R.string.invalid_email));
//        } else {
//            email = true;
//        }
//        if (!password) {
//            this.mPassword.setError(getString(R.string.field_cant_be_empty));
//        }
//        if (!phonenumber) {
//            this.mPhonenumber.setError(getString(R.string.field_cant_be_empty));
//        }
//
//        return firstName && lastName && email && password && phonenumber;
//    }
}
