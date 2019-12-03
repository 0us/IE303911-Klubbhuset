package no.ntnu.klubbhuset.ui.userviews.mymemberships;

import androidx.lifecycle.ViewModelProviders;

import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.Toast;

import no.ntnu.klubbhuset.R;
import no.ntnu.klubbhuset.data.Status;
import no.ntnu.klubbhuset.viewmodels.MyMemberhipsViewModel;


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

        SwipeRefreshLayout swipeRefreshLayout = getView().findViewById(R.id.swipe_refresh_layout);
        swipeRefreshLayout.setOnRefreshListener(() -> {
            mViewModel.refreshClubs(this).observe(this, resource -> {
                if (resource.getStatus() == Status.SUCCESS) {
                    swipeRefreshLayout.setRefreshing(false);
                } else if (resource.getStatus() == Status.ERROR) {
                    Toast.makeText(
                            getContext(),
                            R.string.generic_error_response,
                            Toast.LENGTH_LONG).
                            show();
                    swipeRefreshLayout.setRefreshing(false);
                }
            });
        });
    }
}
