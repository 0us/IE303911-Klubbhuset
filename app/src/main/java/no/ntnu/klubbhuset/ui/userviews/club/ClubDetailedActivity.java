package no.ntnu.klubbhuset.ui.userviews.club;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;

import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.android.material.snackbar.Snackbar;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentActivity;
import androidx.fragment.app.FragmentTransaction;
import androidx.lifecycle.ViewModel;
import androidx.lifecycle.ViewModelProviders;

import android.util.AttributeSet;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import java.util.Objects;

import no.ntnu.klubbhuset.R;
import no.ntnu.klubbhuset.data.model.Club;
import no.ntnu.klubbhuset.data.model.Member;

import static java.security.AccessController.getContext;

public class ClubDetailedActivity extends AppCompatActivity implements ClubDetailedFragment.onMembershipStatusChangedListener {


    private Club club;
    private ClubDetailedViewModel mViewModel;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        Intent intent = getIntent();
        Club club = (Club) intent.getExtras().get("club");
        ClubDetailedViewModel.setCurrentClub(club);

        setContentView(R.layout.activity_club_detailed);
        mViewModel = ViewModelProviders.of(this).get(ClubDetailedViewModel.class);
        /*Toolbar toolbar = findViewById(R.id.toolbar);
        toolbar.setTitle(club.getName());
        setSupportActionBar(toolbar);*/

        Fragment newFragment = ClubDetailedNotMember.newInstance();
        FragmentTransaction transaction = getSupportFragmentManager().beginTransaction()
                .replace(R.id.club_detailed_fragment_container, newFragment);
        transaction.commit();


    }


    @Override
    public void onMembershipStatusChanged(Member member) {

        if (member != null) {
            Fragment newFragment = ClubDetailedMember.newInstance(member);
            FragmentTransaction transaction = getSupportFragmentManager().beginTransaction()
                    .replace(R.id.club_detailed_fragment_container, newFragment);
            transaction.commit();
        } else {
            Fragment newFragment = ClubDetailedNotMember.newInstance();
            FragmentTransaction transaction = getSupportFragmentManager().beginTransaction()
                    .replace(R.id.club_detailed_fragment_container, newFragment);
            transaction.commit();
        }
    }
}

