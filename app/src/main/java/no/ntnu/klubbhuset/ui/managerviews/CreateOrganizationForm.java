package no.ntnu.klubbhuset.ui.managerviews;

import android.content.Context;
import android.media.Image;
import android.net.Uri;
import android.os.Bundle;

import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProviders;
import androidx.navigation.Navigation;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;

import java.math.BigDecimal;

import no.ntnu.klubbhuset.R;
import no.ntnu.klubbhuset.data.model.Club;


/**
 * A simple {@link Fragment} subclass.
 * Activities that contain this fragment must implement the
 * {@link CreateOrganizationForm.OnFragmentInteractionListener} interface
 * to handle interaction events.
 * Use the {@link CreateOrganizationForm#newInstance} factory method to
 * create an instance of this fragment.
 */
public class CreateOrganizationForm extends Fragment {
    // TODO: Rename parameter arguments, choose names that match
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";

    // TODO: Rename and change types of parameters
    private String mParam1;
    private String mParam2;

    private OnFragmentInteractionListener mListener;

    public CreateOrganizationForm() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_create_new_organization, container, false);
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        Button registerBtn = getView().findViewById(R.id.register_organization);
        Button cancelBtn = getView().findViewById(R.id.cancel_registration);

        registerBtn.setOnClickListener(l -> onCreateButtonPressed());
        cancelBtn.setOnClickListener(l -> onCancelButtonPressed());
    }

    // TODO: Rename method, update argument and hook method into UI event
    public void onCreateButtonPressed() {
        View view = getView();
        TextView title = view.findViewById(R.id.organization_name);
        TextView description = view.findViewById(R.id.organization_description);
        TextView price = view.findViewById(R.id.membership_price);
        TextView email = view.findViewById(R.id.contact_info);
        //Image image
        Club club = new Club(
                description.getText().toString(),
                new BigDecimal(price.getText().toString()),
                email.getText().toString(),
                null,
                title.getText().toString());
        ManagerViewModel viewModel = ViewModelProviders.of(this).get(ManagerViewModel.class);
        viewModel.createNewClub(club).observe(this, response -> {
            if (mListener != null) {
                mListener.onOrganizationCreated(club);
            }
        });

    }

    public void onCancelButtonPressed() {
        Navigation.findNavController(getView()).popBackStack();
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        if (context instanceof OnFragmentInteractionListener) {
            mListener = (OnFragmentInteractionListener) context;
        } else {
            throw new RuntimeException(context.toString()
                    + " must implement OnFragmentInteractionListener");
        }
    }

    @Override
    public void onDetach() {
        super.onDetach();
        mListener = null;
    }

    /**
     * This interface must be implemented by activities that contain this
     * fragment to allow an interaction in this fragment to be communicated
     * to the activity and potentially other fragments contained in that
     * activity.
     * <p>
     * See the Android Training lesson <a href=
     * "http://developer.android.com/training/basics/fragments/communicating.html"
     * >Communicating with Other Fragments</a> for more information.
     */
    public interface OnFragmentInteractionListener {
        // TODO: Update argument type and name
        void onOrganizationCreated(Club club);
    }
}
