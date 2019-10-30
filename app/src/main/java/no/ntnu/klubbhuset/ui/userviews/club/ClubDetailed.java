package no.ntnu.klubbhuset.ui.userviews.club;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProviders;

import no.ntnu.klubbhuset.R;


public class ClubDetailed extends Fragment {

    private ClubDetailedViewModel mViewModel;
    private Button joinClubBtn;
    private int id;

    public static ClubDetailed newInstance() {
        return new ClubDetailed();
    }

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.club_detailed_fragment, container, false);
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        mViewModel = ViewModelProviders.of(this).get(ClubDetailedViewModel.class);
        id = getArguments().getInt("id");

        joinClubBtn = getView().findViewById(R.id.club_detailed_joinbtn);
        joinClubBtn.setOnClickListener(click -> {
            mViewModel.joinClub(id).observe(this, success -> {
                if (success) {
                    joinClub();
                }
            });
        });
        // TODO: Use the ViewModel
    }

    public void joinClub() {

    }

}
