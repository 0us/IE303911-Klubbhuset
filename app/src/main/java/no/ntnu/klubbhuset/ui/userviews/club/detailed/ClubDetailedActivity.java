package no.ntnu.klubbhuset.ui.userviews.club.detailed;

import android.content.Intent;
import android.os.Bundle;

import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentTransaction;
import androidx.lifecycle.ViewModelProviders;

import no.ntnu.klubbhuset.R;
import no.ntnu.klubbhuset.data.model.Club;
import no.ntnu.klubbhuset.data.model.Member;

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

        Fragment newFragment = ClubDetailedNotMemberFragment.newInstance();
        FragmentTransaction transaction = getSupportFragmentManager().beginTransaction()
                .replace(R.id.club_detailed_container, newFragment);
        transaction.commit();
    }


    /**
     * listens for changes in membership state, e.g when user joins an org,
     * or membership status gets fetched.
     * @param member
     */
    @Override
    public void onMembershipStatusChanged(Member member) {
        if (member != null) {
            // replace non-member view with member-view
            Fragment newFragment = ClubDetailedMemberFragment.newInstance(member);
            FragmentTransaction transaction = getSupportFragmentManager().beginTransaction()
                    .replace(R.id.club_detailed_fragment_container, newFragment);
            transaction.commit();
        } else {
            Fragment newFragment = ClubDetailedNotMemberFragment.newInstance();
            FragmentTransaction transaction = getSupportFragmentManager().beginTransaction()
                    .replace(R.id.club_detailed_fragment_container, newFragment);
            transaction.commit();
        }
    }
}

