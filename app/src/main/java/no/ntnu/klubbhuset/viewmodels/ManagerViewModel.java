package no.ntnu.klubbhuset.viewmodels;

import android.app.Application;
import android.content.Context;
import android.content.SharedPreferences;
import android.util.Patterns;
import android.widget.Toast;

import androidx.lifecycle.AndroidViewModel;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;

import com.android.volley.AuthFailureError;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.toolbox.JsonArrayRequest;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.Volley;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;


import no.ntnu.klubbhuset.R;

import no.ntnu.klubbhuset.data.Resource;
import no.ntnu.klubbhuset.data.model.Club;
import no.ntnu.klubbhuset.data.repository.OrganizationRepository;
import no.ntnu.klubbhuset.ui.managerviews.CreateOrganizationFormState;
import no.ntnu.klubbhuset.util.AuthHelper;

import static no.ntnu.klubbhuset.util.CommunicationConfig.API_URL;
import static no.ntnu.klubbhuset.util.CommunicationConfig.ORGANIZATION;


public class ManagerViewModel extends AndroidViewModel {
    private static final String TAG = "ManagerViewModel";

    private final SharedPreferences pref;
    private MutableLiveData<CreateOrganizationFormState> createOrganizationFormState = new MutableLiveData<>();
    private OrganizationRepository repository;

    public ManagerViewModel(Application context) {
        super(context);
        this.pref = getApplication().getSharedPreferences("login", Context.MODE_PRIVATE);
        repository = OrganizationRepository.getInstance(getApplication());
    }

    public LiveData<Resource<List<Club>>> getManagedClubs() {
        return repository.getManaged();
    }

    public LiveData<Resource<Club>> createNewClub(Club club, byte[] imageInByte) {
        return repository.create(club, imageInByte);
    }

    public void organizationDataChanged(String email) {
        boolean emailValid = isEmailValid(email);
        if (!emailValid) {
            createOrganizationFormState.setValue(new CreateOrganizationFormState(R.string.invalid_email));
        } else {
            createOrganizationFormState.setValue(new CreateOrganizationFormState(true));
        }
    }

    private boolean isEmailValid(String email) {
        if (email == null) {
            return false;
        }
        boolean contains = email.contains("@");
        if(!contains) {
            return false;
        }

        if(email.contains("@")) {
            return Patterns.EMAIL_ADDRESS.matcher(email).matches();
        } else {
            return !email.trim().isEmpty();
        }
    }

    public MutableLiveData<CreateOrganizationFormState> getCreateOrganizationFormState() {
        return createOrganizationFormState;
    }
}
