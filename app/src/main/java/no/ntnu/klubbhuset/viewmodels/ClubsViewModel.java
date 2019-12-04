package no.ntnu.klubbhuset.viewmodels;

import android.app.Application;

import androidx.lifecycle.AndroidViewModel;
import androidx.lifecycle.LiveData;

import java.util.List;

import no.ntnu.klubbhuset.data.Resource;
import no.ntnu.klubbhuset.data.model.Club;
import no.ntnu.klubbhuset.data.repository.OrganizationRepository;

public class ClubsViewModel extends AndroidViewModel {

    private OrganizationRepository repository;

    public ClubsViewModel(Application context) {
        super(context);
        repository = OrganizationRepository.getInstance(context);
    }

    public LiveData<Resource<List<Club>>> getClubs() {
        return repository.getAll();
    }

    public LiveData<Resource<List<Club>>> refreshClubs() {
        return repository.getAll(true);
    }
}
