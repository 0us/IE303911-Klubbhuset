package no.ntnu.klubbhuset.ui.managerviews;

import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.graphics.drawable.VectorDrawable;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProviders;
import androidx.navigation.Navigation;

import java.io.ByteArrayOutputStream;
import java.io.FileNotFoundException;
import java.io.InputStream;
import java.math.BigDecimal;
import java.util.Objects;

import no.ntnu.klubbhuset.R;
import no.ntnu.klubbhuset.data.model.Club;
import no.ntnu.klubbhuset.viewmodels.ManagerViewModel;


public class CreateOrganizationFormFragment extends Fragment {
    private static final String TAG = "CreateOrganizationFormFragment";

    private static final int UPLOAD_IMAGE_CODE = 1;
    private OnFragmentInteractionListener mListener;

    ImageView imageView;
    TextView email;
    TextView organizationName;
    ManagerViewModel viewModel;

    public CreateOrganizationFormFragment() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        viewModel = ViewModelProviders.of(getActivity()).get(ManagerViewModel.class);

        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_create_new_organization, container, false);
        Button registerBtn = view.findViewById(R.id.register_organization);
        Button cancelBtn = view.findViewById(R.id.cancel_registration);
        imageView = view.findViewById(R.id.organization_profile_picture);
        email = view.findViewById(R.id.contact_email);
        organizationName = view.findViewById(R.id.organization_name);

        email.addTextChangedListener(textWatcher);
        organizationName.addTextChangedListener(textWatcher);

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
        TextView description = view.findViewById(R.id.organization_description);
        TextView price = view.findViewById(R.id.membership_price);

        byte[] imageInByte = null;
        Drawable drawable = imageView.getDrawable();

        // Check that a new image is loaded
        if (!(drawable instanceof VectorDrawable)) {
            Bitmap bitmap = ((BitmapDrawable) drawable).getBitmap();
            ByteArrayOutputStream baos = new ByteArrayOutputStream();
            bitmap.compress(Bitmap.CompressFormat.JPEG, 100, baos);
            imageInByte = baos.toByteArray();
        }

        String descriptionString = description.getText().toString();

        BigDecimal priceOfMembership = BigDecimal.ZERO; // default zero (0)
        try {
            priceOfMembership = new BigDecimal(price.getText().toString()); // If field is text this will throw error
        } catch (NumberFormatException e) {
            Log.d(TAG, "onCreateButtonPressed: creating user. BigDecimal fail" + e.getStackTrace());
        }

        String emailString = email.getText().toString();
        String titleString = organizationName.getText().toString();

        if (titleString.isEmpty()) {
            Toast.makeText(getActivity(), "Title can not be empty", Toast.LENGTH_SHORT).show();
        }
        if (emailString.isEmpty()) {
            Toast.makeText(getActivity(), "Email can not be empty", Toast.LENGTH_SHORT).show();
        }

        if (descriptionString.isEmpty()) {
            descriptionString = "";
        }

        Club club = new Club(
                descriptionString,
                priceOfMembership,
                emailString,
                null,
                titleString);
       
        viewModel.createNewClub(club, imageInByte).observe(this, response -> {
            if (mListener != null) {
                mListener.onOrganizationCreated(club);
                navigateBack();
            }
        });

    }

    /**
     * This method will return the user to the previous view
     */
    private void navigateBack() {
        Navigation.findNavController(Objects.requireNonNull(getView())).popBackStack();
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
    private void selectImageFromGallery() {
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

    TextWatcher textWatcher = new TextWatcher() {
        @Override
        public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

        }

        @Override
        public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {
        }

        @Override
        public void afterTextChanged(Editable editable) {
            viewModel.organizationDataChanged(email.getText().toString());
            if (!viewModel.getCreateOrganizationFormState().getValue().isDataValid()) {
                email.setError(getText(R.string.invalid_email));
            }
            if (organizationName.getText().toString().trim().isEmpty()) {
                organizationName.setError(getText(R.string.organization_name_not_empty));
            }
        }
    };
}
