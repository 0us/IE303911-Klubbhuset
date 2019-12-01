package no.ntnu.klubbhuset.viewmodels;

import android.app.Application;

import androidx.annotation.NonNull;
import androidx.lifecycle.AndroidViewModel;
import androidx.lifecycle.LiveData;

import no.ntnu.klubbhuset.data.Resource;
import no.ntnu.klubbhuset.data.model.Club;
import no.ntnu.klubbhuset.data.model.Member;
import no.ntnu.klubbhuset.data.repository.OrganizationRepository;


public class ClubDetailedViewModel extends AndroidViewModel {
    // TODO: Implement the ViewModel
    private static Club focusedClub;
    private OrganizationRepository organizationRepository;

    public ClubDetailedViewModel(@NonNull Application application) {
        super(application);
        organizationRepository = OrganizationRepository.getInstance(application);
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

}
