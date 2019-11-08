package no.ntnu.klubbhuset.ui.login;

import android.app.Application;

import androidx.lifecycle.ViewModel;
import androidx.lifecycle.ViewModelProvider;
import androidx.annotation.NonNull;

import no.ntnu.klubbhuset.data.LoginDataSource;
import no.ntnu.klubbhuset.data.LoginRepository;

/**
 * ViewModel provider factory to instantiate LoginViewModel.
 * Required given LoginViewModel has a non-empty constructor
 */
public class LoginViewModelFactory implements ViewModelProvider.Factory {

    private Application context;

    public LoginViewModelFactory(Application context) {
        this.context = context;
    }

    @NonNull
    @Override
    @SuppressWarnings("unchecked")
    public <T extends ViewModel> T create(@NonNull Class<T> modelClass) {
        if (modelClass.isAssignableFrom(LoginViewModel.class)) {
            return (T) new LoginViewModel(context, LoginRepository.getInstance(new LoginDataSource(context)));

        } else {
            throw new IllegalArgumentException("Unknown ViewModel class");
        }
    }
}
