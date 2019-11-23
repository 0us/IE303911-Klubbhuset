package no.ntnu.klubbhuset.ui.userviews.club;

import android.app.Application;
import android.content.Context;

import androidx.test.platform.app.InstrumentationRegistry;

import org.junit.Test;

import no.ntnu.klubbhuset.ui.login.LoginDataSource;
import no.ntnu.klubbhuset.ui.login.LoginRepository;
import no.ntnu.klubbhuset.ui.login.LoginViewModel;

import static org.junit.Assert.*;

public class ClubsViewModelTest {

    @Test
    public void getMembership() {
        Application appContext = (Application) InstrumentationRegistry.getInstrumentation().getTargetContext();
        LoginViewModel loginViewModel = new LoginViewModel(appContext, LoginRepository.getInstance(new LoginDataSource(appContext)));
        System.out.println(loginViewModel.isLoggedIn());
    }
}