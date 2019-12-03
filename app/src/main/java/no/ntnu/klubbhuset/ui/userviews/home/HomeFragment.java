package no.ntnu.klubbhuset.ui.userviews.home;

import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.lifecycle.ViewModelProviders;

import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ScrollView;
import android.widget.Toast;

import no.ntnu.klubbhuset.R;
import no.ntnu.klubbhuset.data.Status;
import no.ntnu.klubbhuset.viewmodels.ClubsViewModel;


public class HomeFragment extends Fragment {

    private ClubsViewModel mViewModel;
    private SwipeRefreshLayout swipeRefreshLayout;

    public static HomeFragment newInstance() {
        return new HomeFragment();
    }

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        swipeRefreshLayout = (SwipeRefreshLayout) inflater.inflate(
                R.layout.home_fragment, container, false);

//        ScrollView scrollView = (ScrollView) root.getViewById(R.id.home_club_scrollview);

        // ID is "1" because SwipeRefreshLayout has built in child at ID "0"
        ScrollView scrollView = (ScrollView) swipeRefreshLayout.getChildAt(1);
        RecyclerView recyclerView = (RecyclerView) scrollView.getChildAt(0);
        recyclerView.setLayoutManager(new LinearLayoutManager(swipeRefreshLayout.getContext()));

        swipeRefreshLayout.setOnRefreshListener(() -> {
            mViewModel.refreshClubs(this).observe(this, resource -> {
                if (resource.getStatus() == Status.SUCCESS) {
                    swipeRefreshLayout.setRefreshing(false);
                } else if (resource.getStatus() == Status.ERROR) {
                    Toast.makeText(
                            getContext(),
                            R.string.generic_error_response,
                            Toast.LENGTH_LONG).
                            show();
                    swipeRefreshLayout.setRefreshing(false);
                }
            });
        });

        return swipeRefreshLayout;
    }


    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        mViewModel = ViewModelProviders.of(this).get(ClubsViewModel.class);
    }

}
