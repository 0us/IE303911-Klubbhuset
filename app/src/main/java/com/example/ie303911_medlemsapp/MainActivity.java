package com.example.ie303911_medlemsapp;

import android.os.Bundle;

import com.example.ie303911_medlemsapp.data.model.Club;
import com.example.ie303911_medlemsapp.ui.userviews.home.list.ClubFragment;
import com.example.ie303911_medlemsapp.ui.userviews.memberships.list.ClubMembershipFragment;
import com.google.android.material.tabs.TabLayout;

import androidx.viewpager.widget.ViewPager;
import androidx.appcompat.app.AppCompatActivity;

import com.example.ie303911_medlemsapp.ui.main.SectionsPagerAdapter;

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

        /*FloatingActionButton fab = findViewById(R.id.fab);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Snackbar.make(view, "Replace with your own action", Snackbar.LENGTH_LONG)
                        .setAction("Action", null).show();
            }
        });*/
    }


    @Override
    public void onListFragmentInteraction(Club item) {

    }
}