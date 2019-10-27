package no.ntnu.klubbhuset;

import android.content.Intent;
import android.os.Bundle;

import no.ntnu.klubbhuset.data.model.Club;
import no.ntnu.klubbhuset.ui.login.LoginActivity;
import no.ntnu.klubbhuset.ui.login.LoginViewModel;
import no.ntnu.klubbhuset.ui.login.LoginViewModelFactory;
import no.ntnu.klubbhuset.ui.userviews.home.list.ClubFragment;
import no.ntnu.klubbhuset.ui.userviews.memberships.list.ClubMembershipFragment;
import com.google.android.material.tabs.TabLayout;

import androidx.annotation.Nullable;
import androidx.lifecycle.ViewModel;
import androidx.lifecycle.ViewModelProviders;
import androidx.viewpager.widget.ViewPager;
import androidx.appcompat.app.AppCompatActivity;

import no.ntnu.klubbhuset.ui.main.SectionsPagerAdapter;

public class MainActivity extends AppCompatActivity
        implements ClubFragment.OnListFragmentInteractionListener, ClubMembershipFragment.OnListFragmentInteractionListener {

    private final int LOGIN_REQUEST = 1;

    @Override
    protected void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);
        LoginViewModel loginViewModel = ViewModelProviders.of(this, new LoginViewModelFactory(getApplication()))
                .get(LoginViewModel.class);

        if (loginViewModel.isLoggedIn()) {
            showHome();
        } else {
            Intent intent = new Intent(this, LoginActivity.class);
            startActivityForResult(intent, LOGIN_REQUEST);
        }

    }

    private void showHome() {
        setContentView(R.layout.activity_main);
        SectionsPagerAdapter sectionsPagerAdapter = new SectionsPagerAdapter(this, getSupportFragmentManager());
        ViewPager viewPager = findViewById(R.id.view_pager);
        viewPager.setAdapter(sectionsPagerAdapter);
        TabLayout tabs = findViewById(R.id.tabs);
        tabs.setupWithViewPager(viewPager);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        // Check which request we're responding to
        if (requestCode == LOGIN_REQUEST) {
            // Make sure the request was successful
            if (resultCode == LoginActivity.RESULT_OK) {
                showHome();
            }
        }
    }

    @Override
    public void onListFragmentInteraction(Club item) {

    }
}