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

import no.ntnu.klubbhuset.R;
import no.ntnu.klubbhuset.viewmodels.ClubsViewModel;


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

        ScrollView scrollView = (ScrollView) root.getViewById(R.id.home_club_scrollview);
        RecyclerView recyclerView = (RecyclerView) scrollView.getChildAt(0);
        recyclerView.setLayoutManager(new LinearLayoutManager(root.getContext()));
        return root;
    }


    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        mViewModel = ViewModelProviders.of(this).get(ClubsViewModel.class);
    }

}
