package no.ntnu.klubbhuset.data.repository;

import android.app.Application;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;

import com.android.volley.AuthFailureError;
import com.android.volley.NetworkResponse;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.toolbox.JsonArrayRequest;
import com.android.volley.toolbox.Volley;

import org.json.JSONArray;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import no.ntnu.klubbhuset.data.Cache;
import no.ntnu.klubbhuset.data.Resource;
import no.ntnu.klubbhuset.data.Result;
import no.ntnu.klubbhuset.data.Status;
import no.ntnu.klubbhuset.data.model.Club;
import no.ntnu.klubbhuset.data.model.Group;
import no.ntnu.klubbhuset.data.model.Member;
import no.ntnu.klubbhuset.util.AuthHelper;
import no.ntnu.klubbhuset.util.Json;

import static no.ntnu.klubbhuset.util.CommunicationConfig.API_URL;
import static no.ntnu.klubbhuset.util.CommunicationConfig.JOIN;
import static no.ntnu.klubbhuset.util.CommunicationConfig.MEMBERSHIP;
import static no.ntnu.klubbhuset.util.CommunicationConfig.ORGANIZATION;

public class OrganizationRepository {
    private static volatile OrganizationRepository ourInstance;

    public static OrganizationRepository getInstance(Application context) {
        if (ourInstance == null) {
            ourInstance = new OrganizationRepository(context);
        }
        return ourInstance;
    }

    private Application context;
    private RequestQueue requestQueue;
    private final String ENDPOINT = API_URL + ORGANIZATION + "/";


    private Cache cache = Cache.getInstance();


    private OrganizationRepository(Application context) {
        this.context = context;
        this.requestQueue = Volley.newRequestQueue(context);
    }

    public LiveData<Resource<Club>> create(Club club) {
        throw new UnsupportedOperationException("TODO: Implement method");
    }

    public LiveData<Resource<List<Club>>> getAll() {
        throw new UnsupportedOperationException("TODO: Implement method");

    }

    public Resource<String> delete(Club club) {
        throw new UnsupportedOperationException("TODO: Implement method");
    }

    public Resource<Member> join(Club club) {
        String url = ENDPOINT + "/" + club.getOid() + "/" + JOIN;
        final int[] statusCode = new int[1]; // make variable effectively final to use it inside lambda
        JsonArrayRequest jsonRequest = new JsonArrayRequest(Request.Method.POST, url, null,
                response -> {
                    int result = statusCode[0];
                    if (result != 0) {
                        Member member = parseMembershipResponse(response);
                        cache.getMyMemberships().put(club.getOid(), Resource.success(member));
                    }
                },
                error -> {
                    cache.getMyMemberships().put(club.getOid(), Resource.error(null, error));

                }) {
            @Override
            protected Response<JSONArray> parseNetworkResponse(NetworkResponse response) {
                // override parseNetorkResponse to get status code
                statusCode[0] = response.statusCode;
                return super.parseNetworkResponse(response);
            }

            @Override
            public Map<String, String> getHeaders() throws AuthFailureError {
                return AuthHelper.getAuthHeaders(context);
            }
        };
        requestQueue.add(jsonRequest);
        return cache.getMyMemberships().get(club.getOid());
    }

    public LiveData<Resource<Club>> get(Club club) {
        throw new UnsupportedOperationException("TODO: Implement method");

    }

    public LiveData<Resource<List<Member>>> getMembers(long oid) {
        throw new UnsupportedOperationException("TODO: Implement method");

    }

    public LiveData<Result<List<Club>>> getManaged(long uid) {
        throw new UnsupportedOperationException("TODO: Implement method");

    }

    public Resource<Member> getMembership(Club club) {
        String url = ENDPOINT + "/" + club.getOid() + "/" + MEMBERSHIP;
        Resource<Member> resource = cache.getMyMemberships().get(club.getOid());
        if (resource != null) {
            return resource;
        }
        JsonArrayRequest jar = new JsonArrayRequest(Request.Method.GET, url, null,
                response -> {
                    Member received = parseMembershipResponse(response);
                    cache.getMyMemberships().put(club.getOid(), Resource.success(received));
                }, error -> {
            cache.getMyMemberships().put(club.getOid(), Resource.error(null, error));

        }) {
            @Override
            public Map<String, String> getHeaders() throws AuthFailureError {
                return AuthHelper.getAuthHeaders(context);
            }
        };

        requestQueue.add(jar);
        return cache.getMyMemberships().get(club.getOid());
    }


    /**
     * @param json
     * @return the membership with the highest access level Group
     */
    private Member parseMembershipResponse(JSONArray json) {
        Member[] memberships = Json.fromJson(json.toString(), Member[].class);
        Member result = null;
        if (memberships != null && memberships.length > 0) {
            result = memberships[0];
            for (Member member : memberships) {
                if (member.getGroup().equals(Group.ADMIN)) {
                    result = member;
                }
            }
        }
        return result;
    }
}
