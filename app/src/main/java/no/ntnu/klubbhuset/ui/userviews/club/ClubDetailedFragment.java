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
import androidx.lifecycle.ViewModelProviders;

import java.util.Objects;

import no.ntnu.klubbhuset.R;


public class ClubDetailedFragment extends Fragment {

    private Button joinClubBtn;
    private int id;
    private TextView name;
    private TextView description;
    private TextView url;
    private TextView email;
    private ClubsViewModel model;

    public static Fragment newInstance() {
        return new ClubDetailedFragment();
    }


    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        model = ViewModelProviders.of((FragmentActivity) Objects.requireNonNull(getContext())).get(ClubsViewModel.class);
        model.getSelectedClub().observe(this, club -> {
            name.setText(club.getName());
            description.setText(club.getDescription());
            url.setText(club.getUrl());
            email.setText(club.getEmailContact());
        });
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        super.onCreateView(inflater, container, savedInstanceState);
        View result = inflater.inflate(R.layout.club_detailed_fragment, container, false);
        name = result.findViewById(R.id.club_detailed_name);
        description = result.findViewById(R.id.club_detailed_description);
        url = result.findViewById(R.id.club_detailed_homepage);
        email = result.findViewById(R.id.club_detailed_email);
        return result;
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);


//        id = getArguments().getInt("id");

//        joinClubBtn = getView().findViewById(R.id.club_detailed_joinbtn);
//        joinClubBtn.setOnClickListener(click -> {
//            mViewModel.joinClub(id).observe(this, success -> {
//                if (success) {
//                    joinClub();
//                }
//            });
//        });
        //
    }



    public void joinClub() {

    }

}
