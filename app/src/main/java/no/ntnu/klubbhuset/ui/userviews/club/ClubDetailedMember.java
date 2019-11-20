package no.ntnu.klubbhuset.ui.userviews.club;

import androidx.lifecycle.ViewModelProviders;

import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;

import org.w3c.dom.Text;

import java.text.SimpleDateFormat;

import no.ntnu.klubbhuset.R;
import no.ntnu.klubbhuset.data.model.Club;
import no.ntnu.klubbhuset.data.model.Member;


public class ClubDetailedMember extends Fragment {

    private ClubDetailedViewModel mViewModel;
    private Club club;
    private Member member;
    private Button joinClubBtn;
    private TextView name;
    private TextView description;
    private TextView url;
    private TextView email;

    public static ClubDetailedMember newInstance(Member member) {
        Bundle args = new Bundle();
        args.putSerializable("member", member);
        ClubDetailedMember newInstance = new ClubDetailedMember();
        newInstance.setArguments(args);
        return newInstance;
    }

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.content_club_detailed_member, container, false);
        this.club = ClubDetailedViewModel.getCurrentClub();
        this.member = (Member) getArguments().getSerializable("member");
        return view;
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        mViewModel = ViewModelProviders.of(this).get(ClubDetailedViewModel.class);

        /*name = getView().findViewById(R.id.club_detailed_name);
        description = getView().findViewById(R.id.club_detailed_description);
        url = getView().findViewById(R.id.club_detailed_homepage);
        email = getView().findViewById(R.id.club_detailed_email);

        name.setText(club.getName());
        description.setText(club.getDescription());
        url.setText(club.getUrl());
        email.setText(club.getEmailContact());*/

        TextView memberSince = getView().findViewById(R.id.club_detailed_member_since);
        SimpleDateFormat format  =new SimpleDateFormat("dd MMMM yyyy");
        memberSince.setText(format.format(member.getCreated()));
        mViewModel.getMembership(club).observe(this, l-> {

        });
    }

}
