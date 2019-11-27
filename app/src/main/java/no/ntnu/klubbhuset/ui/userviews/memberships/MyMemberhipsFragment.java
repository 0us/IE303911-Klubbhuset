package no.ntnu.klubbhuset.ui.userviews.memberships;

import androidx.lifecycle.ViewModelProviders;

import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.Toast;

import no.ntnu.klubbhuset.R;


public class MyMemberhipsFragment extends Fragment {

    private MyMemberhipsViewModel mViewModel;
    private ImageView qrView;

    public static MyMemberhipsFragment newInstance() {
        return new MyMemberhipsFragment();
    }

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.my_memberhips_fragment, container, false);
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        mViewModel = ViewModelProviders.of(this).get(MyMemberhipsViewModel.class);
        mViewModel.getQRCode().observe(this, response -> {
            qrView = getView().findViewById(R.id.qrView);
            qrView.setImageBitmap(response);
        });

        mViewModel.getVippsToken().observe(this, response -> {
            // TODO: 23.11.2019 do stuff when token has been recieved
            Toast.makeText(getContext(), response.toString(),Toast.LENGTH_LONG).show();
        });
    }

}
