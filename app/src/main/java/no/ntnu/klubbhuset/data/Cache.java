package no.ntnu.klubbhuset.data;

import androidx.collection.LongSparseArray;
import androidx.lifecycle.MutableLiveData;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import lombok.Data;
import no.ntnu.klubbhuset.data.model.Club;
import no.ntnu.klubbhuset.data.model.Member;
import no.ntnu.klubbhuset.data.model.User;

@Data
public class Cache {
    private User user;
    private final LongSparseArray<MutableLiveData<Resource<Member>>> myMemberships = new LongSparseArray<>();
    private final MutableLiveData<Resource<List<Club>>> homepageClubs = new MutableLiveData<>();
    //private final LongSparseArray<Resource<Club>> myMembershipsClubs = new LongSparseArray<>();
    private final MutableLiveData<Resource<List<Club>>> managedClubs = new MutableLiveData<>();
    //private final LongSparseArray<Resource<Member>> clubMembers = new LongSparseArray<>();

    private static volatile Cache ourInstance;

    public static Cache getInstance() {
        if (ourInstance == null) {
            ourInstance = new Cache();
        }
        return ourInstance;
    }
}
