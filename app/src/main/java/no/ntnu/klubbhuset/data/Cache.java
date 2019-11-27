package no.ntnu.klubbhuset.data;

import androidx.collection.LongSparseArray;

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
    private final LongSparseArray<Resource<Member>> myMemberships = new LongSparseArray<>();
    private final List<Resource<List<Club>>> homepageClubs = new ArrayList<>();
    //private final LongSparseArray<Resource<Club>> myMembershipsClubs = new LongSparseArray<>();
    //private final LongSparseArray<Resource<Club>> managedClubs = new LongSparseArray<>();
    //private final LongSparseArray<Resource<Member>> clubMembers = new LongSparseArray<>();

    private static volatile Cache ourInstance;

    public static Cache getInstance() {
        if (ourInstance == null) {
            ourInstance = new Cache();
        }
        return ourInstance;
    }
}
