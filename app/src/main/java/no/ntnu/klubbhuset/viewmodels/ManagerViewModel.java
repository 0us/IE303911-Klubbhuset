package no.ntnu.klubbhuset.viewmodels;

import android.app.Application;
import android.content.Context;
import android.content.SharedPreferences;
import android.util.Patterns;

import androidx.lifecycle.AndroidViewModel;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;

import java.util.List;

import no.ntnu.klubbhuset.R;
import no.ntnu.klubbhuset.data.repository.MemberRepository;
import no.ntnu.klubbhuset.data.Resource;
import no.ntnu.klubbhuset.data.model.Club;
import no.ntnu.klubbhuset.data.model.Member;
import no.ntnu.klubbhuset.data.repository.OrganizationRepository;
import no.ntnu.klubbhuset.ui.managerviews.CreateOrganizationFormState;


public class ManagerViewModel extends AndroidViewModel {
    private static final String TAG = "ManagerViewModel";

    private final SharedPreferences pref;
    private MutableLiveData<CreateOrganizationFormState> createOrganizationFormState = new MutableLiveData<>();
    private OrganizationRepository repository;
    private MemberRepository memberRepository;

    public ManagerViewModel(Application context) {
        super(context);
        this.pref = getApplication().getSharedPreferences("login", Context.MODE_PRIVATE);
        repository = OrganizationRepository.getInstance(getApplication());
        memberRepository = MemberRepository.getInstance(getApplication());
    }

    public LiveData<Resource<List<Club>>> getManagedClubs() {
        return repository.getManaged();
    }

    public LiveData<Resource<Club>> createNewClub(Club club, byte[] imageInByte) {
        return repository.create(club, imageInByte);
    }

    public LiveData<Resource<List<Member>>> getAllMembersOfClub(Club club) {
        return memberRepository.getMembers(club, false);
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
     */
    public void refreshOrganizations() {
        repository.getAll(true);
        repository.getManaged(true);
        repository.getOrgsWhereUserIsMember(true);
    }

    /**
     * Refreshes only the list containing the managed orgs for this user
     */
    public LiveData<Resource<List<Club>>> refreshManaged() {
        return repository.getManaged(true);
    }
}
