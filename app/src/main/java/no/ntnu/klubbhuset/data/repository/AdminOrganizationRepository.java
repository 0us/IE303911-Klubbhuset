package no.ntnu.klubbhuset.data.repository;

import android.app.Application;

import androidx.annotation.NonNull;
import androidx.collection.LongSparseArray;

import com.android.volley.RequestQueue;
import com.android.volley.toolbox.Volley;

import no.ntnu.klubbhuset.data.Cache;
import no.ntnu.klubbhuset.data.Result;
import no.ntnu.klubbhuset.data.model.Club;
import no.ntnu.klubbhuset.data.model.Member;

public class AdminOrganizationRepository {

    private static volatile LongSparseArray<AdminOrganizationRepository> instances = new LongSparseArray<>();
    public static AdminOrganizationRepository getInstance(Application context, @NonNull Club club) {
        AdminOrganizationRepository instance;
        long oid = club.getOid();
        if ((instance = instances.get(oid)) == null) {
            instance = new AdminOrganizationRepository(context, club);
            instances.put(oid, instance);
        }
        return instance;
    }

    private Club club;
    private Application context;
    private RequestQueue requestQueue;

    private Cache cache = Cache.getInstance();


    private AdminOrganizationRepository(Application context, Club club) {
        this.club = club;
        this.context = context;
        this.requestQueue = Volley.newRequestQueue(context);
    }

    public Result<Member> getMembers() {
        throw new UnsupportedOperationException("TODO: Implement method");
    }

    public Result<Boolean> isAdmin() {
        throw new UnsupportedOperationException("TODO: Implement method");
    }

    public Result<String> hasMemberPaid(long uid) {
        throw new UnsupportedOperationException("TODO: Implement method");
    }
}
