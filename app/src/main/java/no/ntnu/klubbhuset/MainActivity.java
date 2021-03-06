package no.ntnu.klubbhuset;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;

import androidx.appcompat.app.AppCompatActivity;
import androidx.viewpager.widget.ViewPager;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.google.android.material.tabs.TabLayout;

import java.util.Objects;

import no.ntnu.klubbhuset.adapter.TabsPagerAdapter;
import no.ntnu.klubbhuset.data.model.Club;
import no.ntnu.klubbhuset.ui.userviews.club.detailed.ClubDetailedActivity;
import no.ntnu.klubbhuset.ui.userviews.home.ClubsListFragment;
import no.ntnu.klubbhuset.ui.userviews.mymemberships.MyMembershipsListFragment;

import static no.ntnu.klubbhuset.util.CommunicationConfig.API_URL;
import static no.ntnu.klubbhuset.util.CommunicationConfig.PUBLIC_KEY;
import static no.ntnu.klubbhuset.util.PreferenceUtils.setPublicKey;

public class MainActivity extends AppCompatActivity
        implements ClubsListFragment.OnListFragmentInteractionListener, MyMembershipsListFragment.OnListFragmentInteractionListener {


    private RequestQueue requestQueue;

    /**
     * Activity result codes
     */
    public static final int FINISHED = 1;
    public static final int LOGOUT = 2;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_main);
        TabsPagerAdapter tabsPagerAdapter = new TabsPagerAdapter(this, getSupportFragmentManager());
        ViewPager viewPager = findViewById(R.id.view_pager);
        viewPager.setAdapter(tabsPagerAdapter);
        TabLayout tabs = findViewById(R.id.tabs);
        int[] tabIcons = {
                R.drawable.ic_home_black_24dp,
                R.drawable.ic_card_membership_black_24dp,
                R.drawable.ic_person_black_24dp
        };
        tabs.setupWithViewPager(viewPager);
        for (int i = 0; i < tabs.getTabCount(); i++) {
            tabs.getTabAt(i).setIcon(tabIcons[i]);
        }

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
                error -> Log.e(tag , Objects.requireNonNull(error.toString())));
        requestQueue.add(request);
    }

    @Override
    public void onListFragmentInteraction(Club item) {
        Intent intent = new Intent(this, ClubDetailedActivity.class);
        intent.putExtra("club", item.getOid());
        startActivity(intent);
    }

    @Override
    public void onBackPressed() {
        if (getSupportFragmentManager().getBackStackEntryCount() > 0) {
            super.onBackPressed();
        } else {
            setResult(FINISHED);
            finish();
        }
    }
}

