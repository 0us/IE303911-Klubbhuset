package no.ntnu.klubbhuset.ui.userviews.club;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentActivity;
import androidx.fragment.app.FragmentTransaction;
import androidx.lifecycle.ViewModelProviders;

import java.util.Objects;

import no.ntnu.klubbhuset.R;
import no.ntnu.klubbhuset.data.model.Club;


public class ClubDetailedFragment extends Fragment {

    ClubDetailedViewModel mViewModel;
    private Club club;
    private Button joinClubBtn;
    private TextView name;
    private TextView description;
    private TextView url;
    private TextView email;

    public static ClubDetailedFragment newInstance(Club club) {
        Bundle args = new Bundle();
        args.putSerializable("club", club);
        ClubDetailedFragment newInstance = new ClubDetailedFragment();
        newInstance.setArguments(args);
        return newInstance;
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        super.onCreateView(inflater, container, savedInstanceState);
        //this.club = (Club) getArguments().getSerializable("club");
        return inflater.inflate(R.layout.content_club_detailed, container, false);
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        mViewModel = ViewModelProviders.of(this).get(ClubDetailedViewModel.class);
        club = ClubDetailedViewModel.getCurrentClub();

        // decide what content to show based on membership status
        mViewModel.getMembership(club).observe(this, l -> {
            if (l != null) {
                // user is a member
                Fragment newFragment = ClubDetailedMember.newInstance(club.getOid());

                FragmentTransaction transaction = getActivity().getSupportFragmentManager().beginTransaction()
                        .replace(R.id.club_detailed_fragment_container, newFragment)
                        .addToBackStack(null);
                transaction.commit();
            }
        });


        name = getView().findViewById(R.id.club_detailed_name);
        description = getView().findViewById(R.id.club_detailed_description);
        url = getView().findViewById(R.id.club_detailed_homepage);
        email = getView().findViewById(R.id.club_detailed_email);

        name.setText(club.getName());
        description.setText(club.getDescription());
        url.setText(club.getUrl());
        email.setText(club.getEmailContact());

        joinClubBtn = getView().findViewById(R.id.club_detailed_joinbtn);
        joinClubBtn.setOnClickListener(click -> {
            mViewModel.joinClub(club.getOid()).observe(this, response -> {
                if (response != null) {
                    System.out.println("what it do . . .");
                } else {

                }
            });
        });
    }
}
