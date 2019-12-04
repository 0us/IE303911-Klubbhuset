package no.ntnu.klubbhuset.data.cache;

import androidx.collection.LongSparseArray;
import androidx.lifecycle.MutableLiveData;

import java.util.List;

import lombok.Data;
import lombok.Getter;
import no.ntnu.klubbhuset.data.Resource;
import no.ntnu.klubbhuset.data.model.Club;
import no.ntnu.klubbhuset.data.model.Member;
import no.ntnu.klubbhuset.data.model.User;

@Data
public class Cache {
    @Getter(lazy = true) private final MutableLiveData<Resource<User>> user = new MutableLiveData<>();
    @Getter(lazy = true) private final LongSparseArray<MutableLiveData<Resource<Member>>> myMemberships = new LongSparseArray<>();
    @Getter(lazy = true) private final MutableLiveData<Resource<List<Club>>> homepageClubs = new MutableLiveData<>();
    @Getter(lazy = true) private final MutableLiveData<Resource<List<Club>>> myMembershipsClubs = new MutableLiveData<>();
    @Getter(lazy = true) private final MutableLiveData<Resource<List<Club>>> managedClubs = new MutableLiveData<>();
    //private final LongSparseArray<Resource<Member>> clubMembers = new LongSparseArray<>();

    private static volatile Cache ourInstance;


    public static Cache getInstance() {
        if (ourInstance == null) {
            ourInstance = new Cache();
        }
        return ourInstance;
    }
}
