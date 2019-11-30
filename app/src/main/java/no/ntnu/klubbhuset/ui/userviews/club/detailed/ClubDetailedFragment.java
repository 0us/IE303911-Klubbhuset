package no.ntnu.klubbhuset.ui.userviews.club.detailed;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.util.Base64;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProviders;

import no.ntnu.klubbhuset.R;
import no.ntnu.klubbhuset.data.Status;
import no.ntnu.klubbhuset.data.model.Club;
import no.ntnu.klubbhuset.data.model.Member;
import no.ntnu.klubbhuset.viewmodels.ClubDetailedViewModel;


public class ClubDetailedFragment extends Fragment {

    private ClubDetailedViewModel mViewModel;
    private Club club;
    private Button joinClubBtn;
    private TextView name;
    private TextView description;
    private TextView url;
    private TextView email;
    private ImageView image;
    private onMembershipStatusChangedListener mListener;

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
    public void onAttach(Context context) {
        super.onAttach(context);
        if (context instanceof ClubDetailedFragment.onMembershipStatusChangedListener) {
            mListener = (ClubDetailedFragment.onMembershipStatusChangedListener) context;
        } else {
            throw new RuntimeException(context.toString()
                    + " must implement onMembershipStatusChangedListener");
        }
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        mViewModel = ViewModelProviders.of(this).get(ClubDetailedViewModel.class);
        club = ClubDetailedViewModel.getCurrentClub();

        // decide what content to show based on membership status
        mViewModel.getMembership(club).observe(this, response -> {
            if (response.getStatus() == Status.SUCCESS) {
                mListener.onMembershipStatusChanged(response.getData());
            } else {
                //todo handle error
            }
        });


        name = getView().findViewById(R.id.club_detailed_name);
        description = getView().findViewById(R.id.club_detailed_description);
        url = getView().findViewById(R.id.club_detailed_homepage);
        email = getView().findViewById(R.id.club_detailed_email);
        image = getView().findViewById(R.id.club_detailed_banner);
        if (club.getImage() == null || club.getImage().isEmpty()) {
            // set placeholder
            image.setImageResource(R.drawable.ic_landscape_black_24dp);
        } else {
            String encodedString = club.getImage();

            byte [] encodeByte = Base64.decode(encodedString, Base64.DEFAULT);
            Bitmap bitmap = BitmapFactory.decodeByteArray(encodeByte, 0, encodeByte.length);
            image.setImageBitmap(bitmap);
        }

        name.setText(club.getName());
        description.setText(club.getDescription());
        url.setText(club.getUrl());
        email.setText(club.getEmailContact());

        /*joinClubBtn = getView().findViewById(R.id.club_detailed_joinbtn);
        joinClubBtn.setOnClickListener(click -> {
            mViewModel.joinClub(club.getOid()).observe(this, response -> {
                if (response != null) {
                    System.out.println("what it do . . .");
                    mListener.onMembershipStatusChanged(response);
                } else {

                }
            });
        });*/
    }

    public interface onMembershipStatusChangedListener {
        void onMembershipStatusChanged(Member member);
    }

}
