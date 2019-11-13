package no.ntnu.klubbhuset.ui.managerviews;

import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.drawable.BitmapDrawable;
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
import android.widget.ImageView;
import android.widget.TextView;

import java.io.ByteArrayOutputStream;
import java.io.FileNotFoundException;
import java.io.InputStream;
import java.math.BigDecimal;
import java.util.Objects;

import no.ntnu.klubbhuset.R;
import no.ntnu.klubbhuset.data.model.Club;


public class CreateOrganizationForm extends Fragment {

    private static final int UPLOAD_IMAGE_CODE = 1;
    private OnFragmentInteractionListener mListener;

    ImageView imageView;

    public CreateOrganizationForm() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_create_new_organization, container, false);
        Button registerBtn = view.findViewById(R.id.register_organization);
        Button cancelBtn = view.findViewById(R.id.cancel_registration);
        imageView = view.findViewById(R.id.organization_profile_picture);

        imageView.setOnClickListener(l -> onUploadImageButtonPressed());
        registerBtn.setOnClickListener(l -> onCreateButtonPressed());
        cancelBtn.setOnClickListener(l -> onCancelButtonPressed());
        return view;
    }

    private void onUploadImageButtonPressed() {
        selectImageFromGallery();
    }

    private void onCreateButtonPressed() {
        View view = getView();
        TextView title = Objects.requireNonNull(view).findViewById(R.id.organization_name);
        TextView description = view.findViewById(R.id.organization_description);
        TextView price = view.findViewById(R.id.membership_price);
        TextView email = view.findViewById(R.id.contact_email);

        Bitmap bitmap = ((BitmapDrawable) imageView.getDrawable()).getBitmap();
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        bitmap.compress(Bitmap.CompressFormat.JPEG, 100, baos);
        byte[] imageInByte = baos.toByteArray();

        Club club = new Club(
                description.getText().toString(),
                new BigDecimal(price.getText().toString()),
                email.getText().toString(),
                null,
                title.getText().toString());
        ManagerViewModel viewModel = ViewModelProviders.of(this).get(ManagerViewModel.class);
        viewModel.createNewClub(club, imageInByte).observe(this, response -> {
            if (mListener != null) {
                mListener.onOrganizationCreated(club);
            }
        });

    }

    private void onCancelButtonPressed() {
        Navigation.findNavController(Objects.requireNonNull(getView())).popBackStack();
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
     * Retrieves image from users ImageGallery
     */
    private void selectImageFromGallery(){
        Intent galleryIntent = new Intent(Intent.ACTION_PICK,
                android.provider.MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
        startActivityForResult(galleryIntent, UPLOAD_IMAGE_CODE);
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        InputStream stream = null;
        try {
            stream = Objects.requireNonNull(getActivity()).getContentResolver().openInputStream(
                    Objects.requireNonNull(Objects.requireNonNull(data).getData()));
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        }
        Bitmap bitmap = BitmapFactory.decodeStream(stream);
        imageView.setImageBitmap(bitmap);
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
        void onOrganizationCreated(Club club);
    }
}
