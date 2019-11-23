package no.ntnu.klubbhuset.ui.managerviews;

import android.content.Intent;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.navigation.Navigation;

import android.provider.MediaStore;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;

import java.util.Objects;

import no.ntnu.klubbhuset.R;
import no.ntnu.klubbhuset.ui.managerviews.barcode.BarcodeScannerActivity;


public class ManagerFragment extends Fragment {

    private static final int TAKE_PICTURE = 1;
    private ManagerViewModel mViewModel;

    public static ManagerFragment newInstance() {
        return new ManagerFragment();
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.manager_fragment, container, false);
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        Button createNewOrgBtn = Objects.requireNonNull(getView()).findViewById(R.id.manage_create_new_org);
        createNewOrgBtn.setOnClickListener(l -> {
            Navigation.findNavController(getView()).navigate(R.id.action_managerFragment_to_createOrganizationForm);
        });

        Button scanQrCodeBtn = Objects.requireNonNull(getView()).findViewById(R.id.manage_scan_qr_code);
        scanQrCodeBtn.setOnClickListener(l -> {
            Intent intent = new Intent(getActivity(), BarcodeScannerActivity.class);
            startActivity(intent);
        });
    }
}
