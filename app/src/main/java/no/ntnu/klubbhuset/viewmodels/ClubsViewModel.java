package no.ntnu.klubbhuset.viewmodels;

import android.app.Application;

import androidx.lifecycle.AndroidViewModel;
import androidx.lifecycle.LifecycleOwner;
import androidx.lifecycle.LiveData;

import no.ntnu.klubbhuset.data.Resource;
import no.ntnu.klubbhuset.data.model.Club;
import no.ntnu.klubbhuset.data.repository.OrganizationRepository;

import java.util.List;

public class ClubsViewModel extends AndroidViewModel {

    private OrganizationRepository repository;

    public ClubsViewModel(Application context) {
        super(context);
        repository = OrganizationRepository.getInstance(context);
    }

    public LiveData<Resource<List<Club>>> getClubs(LifecycleOwner owner) {
        return repository.getAll(owner);
    }

    public LiveData<Resource<List<Club>>> refreshClubs(LifecycleOwner owner) {
        return repository.getAll(owner, true);
    }

}
