package no.ntnu.klubbhuset.ui.userviews.club.detailed;

import android.content.Context;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;
import android.widget.Toast;

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
        mViewModel = ViewModelProviders.of(getActivity()).get(ClubDetailedViewModel.class);

        name = getView().findViewById(R.id.club_detailed_name);
        description = getView().findViewById(R.id.club_detailed_description);
        url = getView().findViewById(R.id.club_detailed_homepage);
        email = getView().findViewById(R.id.club_detailed_email);
        image = getView().findViewById(R.id.club_detailed_banner);

        mViewModel.getCurrentClub().observe(this, clubResource -> {
            if (clubResource.getStatus() == Status.SUCCESS) {
                this.club = clubResource.getData();
                setData();
            } else if (clubResource.getStatus() == Status.LOADING){
                // loading
            } else if (clubResource.getStatus() == Status.ERROR){
                Toast.makeText(getContext(), R.string.generic_error_response, Toast.LENGTH_SHORT).show();;
            }
        });
    }

    private void setData() {
        if (club.getOrgImages() == null || club.getOrgImages().length == 0) {
            // set placeholder
            image.setImageResource(R.drawable.ic_broken_image_black_24dp);
        } else {
            image.setImageBitmap(club.getOrgImages()[0].getImage());
        }

        name.setText(club.getName());
        description.setText(club.getDescription());
        url.setText(club.getUrl());
        email.setText(club.getEmailContact());

        // decide what content to show based on membership status
        mViewModel.getMembership(club).observe(this, response -> {
            if (response.getStatus() == Status.SUCCESS) {
                mListener.onMembershipStatusChanged(response.getData());
            } else {
                //todo handle error
            }
        });
    }

    public interface onMembershipStatusChangedListener {
        void onMembershipStatusChanged(Member member);
    }

}
