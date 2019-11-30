package no.ntnu.klubbhuset.ui.userviews.profile;

import androidx.lifecycle.ViewModel;
import androidx.lifecycle.ViewModelProviders;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import no.ntnu.klubbhuset.R;
import no.ntnu.klubbhuset.data.model.User;
import no.ntnu.klubbhuset.ui.login.LoginActivity;
import no.ntnu.klubbhuset.ui.login.LoginViewModel;
import no.ntnu.klubbhuset.ui.managerviews.ManagerActivity;

import static no.ntnu.klubbhuset.ui.main.MainActivity.LOGOUT;


public class ProfileFragment extends Fragment {

    private ProfileViewModel mViewModel;

    private TextView firstname;
    private TextView lastname;
    private TextView email;
    private TextView phone;
    private ImageView picture;
    private Button signout;
    private Button manage;


    public static ProfileFragment newInstance() {
        return new ProfileFragment();
    }

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_my_profile, container, false);
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        initButtons();

        mViewModel = ViewModelProviders.of(this).get(ProfileViewModel.class);
        mViewModel.getUser().observe(this, this::fillUserInfo);

    }

    private void fillUserInfo(User user) {
        if (user != null) {
            firstname = getView().findViewById(R.id.profile_firstname);
            lastname = getView().findViewById(R.id.profile_lastname);
            email = getView().findViewById(R.id.profile_email);
            phone = getView().findViewById(R.id.profile_phonenumber);
            picture = getView().findViewById(R.id.profile_picture);

            firstname.setText(user.getFirstName());
            lastname.setText(user.getLastName());
            email.setText(user.getEmail());
            phone.setText(user.getPhone());
        } else {
            Log.e("Tag test 123", "User is null");
            // show error
        }
    }

    private void initButtons() {
        // sign out
        signout = getView().findViewById(R.id.btn_sign_out);
        signout.setOnClickListener(l -> signOut());

        // manage orgs
        manage = getView().findViewById(R.id.profile_manage_orgs);
        manage.setOnClickListener(l -> manageOrgs());
    }

    private void signOut() {
        getActivity().setResult(LOGOUT);
        getActivity().finish();
    }

    private void manageOrgs() {
        Intent intent = new Intent(this.getActivity(), ManagerActivity.class);
        startActivity(intent);
    }

}
