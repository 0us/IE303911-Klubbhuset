package no.ntnu.klubbhuset.ui.main;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.util.Log;

import no.ntnu.klubbhuset.R;
import no.ntnu.klubbhuset.data.model.Club;
import no.ntnu.klubbhuset.ui.userviews.club.detailed.ClubDetailedActivity;
import no.ntnu.klubbhuset.ui.userviews.home.list.ClubFragment;
import no.ntnu.klubbhuset.ui.userviews.memberships.list.ClubMembershipFragment;
import no.ntnu.klubbhuset.util.mlkit.GraphicOverlay;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.google.android.material.tabs.TabLayout;

import androidx.viewpager.widget.ViewPager;
import androidx.appcompat.app.AppCompatActivity;

import java.util.Objects;

import static no.ntnu.klubbhuset.data.CommunicationConfig.API_URL;
import static no.ntnu.klubbhuset.data.CommunicationConfig.PUBLIC_KEY;
import static no.ntnu.klubbhuset.util.PreferenceUtils.setPublicKey;

public class MainActivity extends AppCompatActivity
        implements ClubFragment.OnListFragmentInteractionListener, ClubMembershipFragment.OnListFragmentInteractionListener {


    private RequestQueue requestQueue;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_main);
        SectionsPagerAdapter sectionsPagerAdapter = new SectionsPagerAdapter(this, getSupportFragmentManager());
        ViewPager viewPager = findViewById(R.id.view_pager);
        viewPager.setAdapter(sectionsPagerAdapter);
        TabLayout tabs = findViewById(R.id.tabs);
        tabs.setupWithViewPager(viewPager);

        this.requestQueue = Volley.newRequestQueue(getApplicationContext());

        savePublicKey();
    }

    private void savePublicKey() {
        String tag = "PublicKeyFetcher";

        String url = API_URL + PUBLIC_KEY;

        StringRequest request = new StringRequest(Request.Method.GET, url,
                response -> {
                    setPublicKey(response, getApplicationContext());
                    Log.i(tag, "Fetch successful!");
                },
                error -> Log.e(tag , Objects.requireNonNull(error.getMessage())));
        requestQueue.add(request);
    }

    @Override
    public void onListFragmentInteraction(Club item) {
        Intent intent = new Intent(this, ClubDetailedActivity.class);
        intent.putExtra("club", item);
        startActivity(intent);
    }


}

