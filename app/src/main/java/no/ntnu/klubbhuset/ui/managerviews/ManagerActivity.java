package no.ntnu.klubbhuset.ui.managerviews;

import android.content.Intent;
import android.os.Bundle;

import androidx.appcompat.app.AppCompatActivity;

import no.ntnu.klubbhuset.R;
import no.ntnu.klubbhuset.data.model.Club;


public class
ManagerActivity extends AppCompatActivity implements
        ManagedOrgsListFragment.OnListFragmentInteractionListener, CreateOrganizationFormFragment.OnFragmentInteractionListener{


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.manager_activity);
    }

    @Override
    public void onListFragmentInteraction(Club item) {
        Intent intent = new Intent(this, ClubAdminActivity.class);
        intent.putExtra("club", item);
        startActivity(intent);
    }

    @Override
    public void onOrganizationCreated(Club club) {
    }
}
