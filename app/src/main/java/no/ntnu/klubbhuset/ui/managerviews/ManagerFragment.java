package no.ntnu.klubbhuset.ui.managerviews;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.navigation.Navigation;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;

import java.util.Objects;

import no.ntnu.klubbhuset.R;
import no.ntnu.klubbhuset.data.Status;
import no.ntnu.klubbhuset.viewmodels.ManagerViewModel;


public class ManagerFragment extends Fragment {

    private ManagerViewModel mViewModel;

    public static ManagerFragment newInstance() {
        return new ManagerFragment();
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        View root = inflater.inflate(R.layout.manager_fragment, container, false);

        mViewModel = new ManagerViewModel(getActivity().getApplication());

        SwipeRefreshLayout swipeRefreshLayout = root.findViewById(R.id.swipe_refresh_layout);
        swipeRefreshLayout.setOnRefreshListener(() -> {
            mViewModel.refreshManaged().observe(this, resource -> {
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
        return root;
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        Button createNewOrgBtn = Objects.requireNonNull(getView()).findViewById(R.id.manage_create_new_org);
        createNewOrgBtn.setOnClickListener(l -> {
            Navigation.findNavController(getView()).navigate(R.id.action_managerFragment_to_createOrganizationForm);
        });

    }
}
