package no.ntnu.klubbhuset.ui.main;

import android.content.Intent;
import android.os.Bundle;

import no.ntnu.klubbhuset.R;
import no.ntnu.klubbhuset.data.model.Club;
import no.ntnu.klubbhuset.ui.userviews.club.ClubDetailedActivity;
import no.ntnu.klubbhuset.ui.userviews.home.list.ClubFragment;
import no.ntnu.klubbhuset.ui.userviews.memberships.list.ClubMembershipFragment;

import com.google.android.material.tabs.TabLayout;

import androidx.viewpager.widget.ViewPager;
import androidx.appcompat.app.AppCompatActivity;

public class MainActivity extends AppCompatActivity
        implements ClubFragment.OnListFragmentInteractionListener, ClubMembershipFragment.OnListFragmentInteractionListener {


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_main);
        SectionsPagerAdapter sectionsPagerAdapter = new SectionsPagerAdapter(this, getSupportFragmentManager());
        ViewPager viewPager = findViewById(R.id.view_pager);
        viewPager.setAdapter(sectionsPagerAdapter);
        TabLayout tabs = findViewById(R.id.tabs);
        tabs.setupWithViewPager(viewPager);
    }

    @Override
    public void onListFragmentInteraction(Club item) {
        Intent intent = new Intent(this, ClubDetailedActivity.class);
        intent.putExtra("club", item);
        startActivity(intent);
    }


}

