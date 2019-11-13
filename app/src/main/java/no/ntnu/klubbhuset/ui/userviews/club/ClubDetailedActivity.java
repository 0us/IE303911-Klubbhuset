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
import androidx.lifecycle.ViewModelProviders;

import android.util.AttributeSet;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import java.util.Objects;

import no.ntnu.klubbhuset.R;
import no.ntnu.klubbhuset.data.model.Club;

import static java.security.AccessController.getContext;

public class ClubDetailedActivity extends AppCompatActivity {

    private Button joinClubBtn;
    private int id;
    private TextView name;
    private TextView description;
    private TextView url;
    private TextView email;
    private ClubsViewModel model;
    private Club club;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        Intent intent = getIntent();
        Club club = (Club) intent.getExtras().get("club");
        setContentView(R.layout.activity_club_detailed);

        Toolbar toolbar = findViewById(R.id.toolbar);
        toolbar.setTitle(club.getName());
        setSupportActionBar(toolbar);

        name = findViewById(R.id.club_detailed_name);
        description = findViewById(R.id.club_detailed_description);
        url = findViewById(R.id.club_detailed_homepage);
        email = findViewById(R.id.club_detailed_email);

        name.setText(club.getName());
        description.setText(club.getDescription());
        url.setText(club.getUrl());
        email.setText(club.getEmailContact());

        //        joinClubBtn = getView().findViewById(R.id.club_detailed_joinbtn);
//        joinClubBtn.setOnClickListener(click -> {
//            mViewModel.joinClub(id).observe(this, success -> {
//                if (success) {
//                    joinClub();
//                }
//            });
//        });
    }
}
