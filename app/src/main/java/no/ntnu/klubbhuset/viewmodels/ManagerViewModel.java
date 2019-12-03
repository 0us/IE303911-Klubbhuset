package no.ntnu.klubbhuset.viewmodels;

import android.app.Application;
import android.content.Context;
import android.content.SharedPreferences;
import android.util.Patterns;

import androidx.lifecycle.AndroidViewModel;
import androidx.lifecycle.LifecycleOwner;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;

import java.util.List;


import no.ntnu.klubbhuset.R;

import no.ntnu.klubbhuset.data.Resource;
import no.ntnu.klubbhuset.data.model.Club;
import no.ntnu.klubbhuset.data.repository.OrganizationRepository;
import no.ntnu.klubbhuset.ui.managerviews.CreateOrganizationFormState;


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

    public LiveData<Resource<List<Club>>> getManagedClubs(LifecycleOwner owner) {
        return repository.getManaged(owner);
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

    /**
     * Refreshes organizations in the entire app, this should not be used
     * frequently since it's a heavy operation to update everything at once.
     * @param owner LifecycleOwner
     */
    public void refreshOrganizations(LifecycleOwner owner) {
        repository.getAll(owner, true);
        repository.getManaged(owner, true);
        repository.getOrgsWhereUserIsMember(owner, true);
    }

    /**
     * Refreshes only the list containing the managed orgs for this user
     */
    public LiveData<Resource<List<Club>>> refreshManaged(LifecycleOwner owner) {
        return repository.getManaged(owner, true);
    }
}
