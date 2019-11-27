package no.ntnu.klubbhuset.viewmodels;

import android.app.Application;

import androidx.annotation.NonNull;
import androidx.lifecycle.AndroidViewModel;
import androidx.lifecycle.MutableLiveData;

import com.android.volley.RequestQueue;
import com.android.volley.toolbox.Volley;
import com.google.gson.Gson;

import no.ntnu.klubbhuset.data.model.Club;
import no.ntnu.klubbhuset.data.model.Member;
import no.ntnu.klubbhuset.data.repository.OrganizationRepository;


public class ClubDetailedViewModel extends AndroidViewModel {
    // TODO: Implement the ViewModel
    private RequestQueue requestQueue;
    private MutableLiveData<Member> membership;
    private Gson gson;
    private static Club focusedClub;
    private OrganizationRepository organizationRepository;

    public ClubDetailedViewModel(@NonNull Application application) {
        super(application);
        requestQueue = Volley.newRequestQueue(application);
        gson = new Gson();
        membership = new MutableLiveData<>();
        organizationRepository = OrganizationRepository.getInstance(application);
    }

    public MutableLiveData<Member> joinClub(Club club) {
        organizationRepository.join(club);
        return membership;
    }


    /**
     * get users membership status in given organization
     * @param club
     * @return
     */
    public MutableLiveData<Member> getMembership(Club club) {
        organizationRepository.getMembership(club);
        return membership;
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
