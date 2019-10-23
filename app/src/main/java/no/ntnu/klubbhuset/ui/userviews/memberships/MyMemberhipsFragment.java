package no.ntnu.klubbhuset.ui.userviews.memberships;

import androidx.lifecycle.ViewModelProviders;

import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.ntnu.klubbhuset.R;


public class MyMemberhipsFragment extends Fragment {

    private MyMemberhipsViewModel mViewModel;

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
        // TODO: Use the ViewModel
    }

}
