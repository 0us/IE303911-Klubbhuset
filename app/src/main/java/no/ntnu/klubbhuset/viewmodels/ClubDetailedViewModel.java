package no.ntnu.klubbhuset.viewmodels;

import android.app.Application;

import androidx.annotation.NonNull;
import androidx.lifecycle.AndroidViewModel;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;

import com.android.volley.RequestQueue;
import com.android.volley.toolbox.Volley;
import com.google.gson.Gson;

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
    private static Club focusedClub;
    private OrganizationRepository organizationRepository;
    private VippsRepository vippsRepository;
    private UserRepository userRepository;

    public ClubDetailedViewModel(@NonNull Application application) {
        super(application);
        organizationRepository = OrganizationRepository.getInstance(application);
        vippsRepository = VippsRepository.getInstance(application);
        userRepository = UserRepository.getInstance(application);
    }

    public LiveData<Resource<Member>> joinClub(Club club) {
        return organizationRepository.join(club);
    }


    /**
     * get users membership status in given organization
     * @param club
     * @return
     */
    public LiveData<Resource<Member>> getMembership(Club club) {
        return organizationRepository.getMembership(club);
    }

    public static Club getCurrentClub() {
        return focusedClub;
    }

    /**
     * set the currently focused organization, for use with
     * clubDetailedView
     * @param currentClub
     */
    public static void setCurrentClub(Club currentClub) {
        focusedClub = currentClub;
    }

    public LiveData<Resource<String>> getDeeplink(Resource<User> user) {
        return vippsRepository.getDeepLink(user, focusedClub);
    }

    public LiveData<Resource<User>> getUser() {
        return userRepository.get();
    }
}
