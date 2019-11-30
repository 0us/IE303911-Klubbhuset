package no.ntnu.klubbhuset.viewmodels;

import android.app.Application;

import androidx.lifecycle.AndroidViewModel;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;

import com.android.volley.AuthFailureError;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.toolbox.JsonArrayRequest;
import com.android.volley.toolbox.Volley;

import no.ntnu.klubbhuset.data.Resource;
import no.ntnu.klubbhuset.data.model.Club;
import no.ntnu.klubbhuset.data.repository.OrganizationRepository;
import no.ntnu.klubbhuset.util.AuthHelper;

import org.json.JSONException;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import static no.ntnu.klubbhuset.util.CommunicationConfig.API_URL;
import static no.ntnu.klubbhuset.util.CommunicationConfig.ORGANIZATION;

public class ClubsViewModel extends AndroidViewModel {

    private OrganizationRepository repository;

    public ClubsViewModel(Application context) {
        super(context);
        repository = OrganizationRepository.getInstance(context);
    }

    public LiveData<Resource<List<Club>>> getClubs() {
        return repository.getAll();
    }

}
