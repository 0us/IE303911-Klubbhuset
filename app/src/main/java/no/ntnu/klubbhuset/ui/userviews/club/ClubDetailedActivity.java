package no.ntnu.klubbhuset.ui.userviews.club;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.widget.Button;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import java.io.UnsupportedEncodingException;
import java.net.URLDecoder;

import no.ntnu.klubbhuset.R;
import no.ntnu.klubbhuset.data.model.Club;

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


    // TODO Finish this

    /**
     * This method has to be implemented to work with the vipps app.
     * @param intent
     */
    @Override
    protected void onNewIntent(Intent intent) {
        super.onNewIntent(intent);
        String url = null;
        if (intent != null && intent.getData() != null) {
            try {
                url = URLDecoder.decode(intent.getData().toString(), "UTF-8");
                Uri parseUri = Uri.parse(url);
                String status = parseUri.getQueryParameter("status");
                // TODO handle status
            } catch (UnsupportedEncodingException e) {
                e.printStackTrace();
            }
        }
    }
}
