package no.ntnu.klubbhuset.viewmodels;

import android.app.Application;

import androidx.annotation.NonNull;
import androidx.lifecycle.AndroidViewModel;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;

import no.ntnu.klubbhuset.data.Resource;
import no.ntnu.klubbhuset.data.model.Club;
import no.ntnu.klubbhuset.data.model.Member;
import no.ntnu.klubbhuset.data.model.User;
import no.ntnu.klubbhuset.data.repository.OrganizationRepository;
import no.ntnu.klubbhuset.data.repository.UserRepository;
import no.ntnu.klubbhuset.data.repository.VippsRepository;

import static no.ntnu.klubbhuset.util.CommunicationConfig.API_URL;
import static no.ntnu.klubbhuset.util.CommunicationConfig.USER;


public class ClubDetailedViewModel extends AndroidViewModel {
    // TODO: Implement the ViewModel
    private MutableLiveData<Resource<Club>> focusedClub;
    private OrganizationRepository organizationRepository;
    private VippsRepository vippsRepository;
    private UserRepository userRepository;

    public ClubDetailedViewModel(@NonNull Application application) {
        super(application);
        focusedClub = new MutableLiveData<>(Resource.loading());
        organizationRepository = OrganizationRepository.getInstance(application);
        vippsRepository = VippsRepository.getInstance(application);
        userRepository = UserRepository.getInstance(application);
    }

    public LiveData<Resource<Member>> joinClub(Club club) {
        return organizationRepository.join(club);
    }


    /**
     * get users membership status in given organization
     *
     * @param club
     * @return
     */
    public LiveData<Resource<Member>> getMembership(Club club) {
        return organizationRepository.getMembership(club);
    }

    public LiveData<Resource<Club>> getCurrentClub() {
        return focusedClub;
    }

    public void setCurrentClub(long currentClub) {
        focusedClub.setValue(organizationRepository.get(currentClub));
    }

    public LiveData<Resource<User>> getUser() {
        return userRepository.get();
    }
    public LiveData<Resource<String>> getDeeplink(Resource<User> user) {
        return vippsRepository.getDeepLink(user, focusedClub.getValue().getData());
    }
}
