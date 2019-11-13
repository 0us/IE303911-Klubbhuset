package no.ntnu.klubbhuset.ui.managerviews;

import androidx.appcompat.app.AppCompatActivity;

import android.net.Uri;
import android.os.Bundle;

import no.ntnu.klubbhuset.R;
import no.ntnu.klubbhuset.data.model.Club;
import no.ntnu.klubbhuset.ui.managerviews.list.ManagedOrgsListFragment;
import no.ntnu.klubbhuset.ui.userviews.home.list.ClubFragment;

public class
ManagerActivity extends AppCompatActivity implements
        ManagedOrgsListFragment.OnListFragmentInteractionListener,
        CreateOrganizationForm.OnFragmentInteractionListener {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.manager_activity);
    }

    @Override
    public void onListFragmentInteraction(Club item) {

    }

    @Override
    public void onOrganizationCreated(Club club) {
    }
}
