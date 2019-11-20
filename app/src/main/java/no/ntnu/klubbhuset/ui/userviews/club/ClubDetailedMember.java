package no.ntnu.klubbhuset.ui.userviews.club;

import androidx.lifecycle.ViewModelProviders;

import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import no.ntnu.klubbhuset.R;
import no.ntnu.klubbhuset.data.model.Club;


public class ClubDetailedMember extends Fragment {

    private ClubDetailedViewModel mViewModel;

    public static ClubDetailedMember newInstance(long id) {
        Bundle args = new Bundle();
        args.putLong("id", id);
        ClubDetailedMember newInstance = new ClubDetailedMember();
        newInstance.setArguments(args);
        return newInstance;
    }

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.content_club_detailed_member, container, false);
        mViewModel = ViewModelProviders.of(this).get(ClubDetailedViewModel.class);
        //mViewModel.getMembership()
        return view;
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        // TODO: Use the ViewModel
    }

}
