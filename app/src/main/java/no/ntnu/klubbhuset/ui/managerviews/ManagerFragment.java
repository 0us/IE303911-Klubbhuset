package no.ntnu.klubbhuset.ui.managerviews;

import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.lifecycle.ViewModelProviders;

import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.navigation.NavAction;
import androidx.navigation.Navigation;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ScrollView;

import no.ntnu.klubbhuset.R;
import no.ntnu.klubbhuset.ui.userviews.home.list.ClubFragment;
import no.ntnu.klubbhuset.ui.userviews.home.list.MyClubRecyclerViewAdapter;


public class ManagerFragment extends Fragment {

    private ManagerViewModel mViewModel;

    public static ManagerFragment newInstance() {
        return new ManagerFragment();
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.manager_fragment, container, false);
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        Button createNewOrgBtn = getView().findViewById(R.id.manage_create_new_org);
        createNewOrgBtn.setOnClickListener(l -> {
            Navigation.findNavController(getView()).navigate(R.id.action_managerFragment_to_createOrganizationForm);
        });
    }

}
