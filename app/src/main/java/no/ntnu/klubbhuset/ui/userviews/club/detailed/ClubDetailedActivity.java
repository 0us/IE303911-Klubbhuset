package no.ntnu.klubbhuset.ui.userviews.club.detailed;

import android.content.Intent;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.net.Uri;
import android.os.Bundle;
import android.view.MenuItem;
import android.view.WindowManager;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentTransaction;
import androidx.lifecycle.ViewModelProviders;

import no.ntnu.klubbhuset.R;
import no.ntnu.klubbhuset.data.Status;
import no.ntnu.klubbhuset.data.model.Club;
import no.ntnu.klubbhuset.data.model.Member;
import no.ntnu.klubbhuset.viewmodels.ClubDetailedViewModel;

public class ClubDetailedActivity extends AppCompatActivity implements ClubDetailedFragment.onMembershipStatusChangedListener {


    private Club club;
    private ClubDetailedViewModel mViewModel;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mViewModel = ViewModelProviders.of(this).get(ClubDetailedViewModel.class);

        Intent intent = getIntent();
        long oid = (intent.getExtras().getLong("club"));
        mViewModel.setCurrentClub(oid);

        setContentView(R.layout.activity_club_detailed);
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_NAVIGATION, WindowManager.LayoutParams.FLAG_TRANSLUCENT_NAVIGATION);
        ActionBar supportActionBar = getSupportActionBar();
        if (supportActionBar != null) {
            supportActionBar.setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
            supportActionBar.setTitle("");
            supportActionBar.setDisplayHomeAsUpEnabled(true);
        }
        /*Toolbar toolbar = findViewById(R.id.toolbar);
        toolbar.setTitle(club.getName());
        setSupportActionBar(toolbar);*/

        Fragment newFragment = ClubDetailedNotMemberFragment.newInstance();
        FragmentTransaction transaction = getSupportFragmentManager().beginTransaction()
                .replace(R.id.club_detailed_fragment_container, newFragment);
        transaction.commit();
        // ATTENTION: This was auto-generated to handle app links.
        Intent appLinkIntent = getIntent();
        String appLinkAction = appLinkIntent.getAction();
        Uri appLinkData = appLinkIntent.getData();
    }


    /**
     * listens for changes in membership state, e.g when user joins an org,
     * or membership status gets fetched.
     *
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

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        switch (item.getItemId()) {
            // Respond to the action bar's Up/Home button
            case android.R.id.home:
                onBackPressed();
                return true;
        }
        return super.onOptionsItemSelected(item);
    }
}

