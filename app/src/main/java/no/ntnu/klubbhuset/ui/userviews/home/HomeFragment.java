package no.ntnu.klubbhuset.ui.userviews.home;

import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.lifecycle.ViewModelProviders;

import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ScrollView;

import java.util.Objects;

import no.ntnu.klubbhuset.R;
import no.ntnu.klubbhuset.data.Status;
import no.ntnu.klubbhuset.viewmodels.ClubsViewModel;
import no.ntnu.klubbhuset.adapter.ClubsRecyclerViewAdapter;


public class HomeFragment extends Fragment {

    private ClubsViewModel mViewModel;

    public static HomeFragment newInstance() {
        return new HomeFragment();
    }

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        ConstraintLayout root = (ConstraintLayout) inflater.inflate(
                R.layout.home_fragment, container, false);

        ScrollView scrollView = (ScrollView) root.getViewById(R.id.scrollView2);
        RecyclerView recyclerView = (RecyclerView) scrollView.getChildAt(0);
        recyclerView.setLayoutManager(new LinearLayoutManager(root.getContext()));

        ClubsViewModel model =
                ViewModelProviders.of(
                        Objects.requireNonNull(this.getActivity())).get(ClubsViewModel.class);
        model.getClubs(this).observe(this, clubs -> {
            if (clubs.getStatus() == Status.SUCCESS) {
                recyclerView.setAdapter(new ClubsRecyclerViewAdapter(clubs.getData(),
                        (ClubsListFragment.OnListFragmentInteractionListener) this.getActivity()));
            } else {
                //todo handle error
            }
        });
        return root;
    }


    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        mViewModel = ViewModelProviders.of(this).get(ClubsViewModel.class);
    }

}
