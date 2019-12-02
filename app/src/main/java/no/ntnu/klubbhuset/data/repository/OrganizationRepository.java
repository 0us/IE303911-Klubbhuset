package no.ntnu.klubbhuset.data.repository;

import android.content.Context;
import android.os.AsyncTask;

import androidx.lifecycle.LifecycleOwner;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;

import com.android.volley.AuthFailureError;
import com.android.volley.NetworkResponse;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.toolbox.JsonArrayRequest;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.Volley;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.Arrays;
import java.util.List;
import java.util.Map;
import java.util.Optional;

import lombok.val;
import no.ntnu.klubbhuset.data.Status;
import no.ntnu.klubbhuset.data.cache.Cache;
import no.ntnu.klubbhuset.data.Resource;
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

    public static OrganizationRepository getInstance(Context context) {
        if (ourInstance == null) {
            ourInstance = new OrganizationRepository(context);
        }
        return ourInstance;
    }

    private Context context;
    private RequestQueue requestQueue;
    private final String ENDPOINT = API_URL + ORGANIZATION + "/";


    private Cache cache = Cache.getInstance();


    private OrganizationRepository(Context context) {
        this.context = context;
        this.requestQueue = Volley.newRequestQueue(context);
    }

    public LiveData<Resource<Club>> create(Club club, byte[] imageInByte) {
        JSONObject jsonObject = club.toJson(imageInByte);
        MutableLiveData res = new MutableLiveData();
        JsonObjectRequest request = new JsonObjectRequest(Request.Method.POST, ENDPOINT, jsonObject,
                response -> {
                    res.setValue(Resource.success(new Club(response)));
                },
                error -> {
                    res.setValue(Resource.error(null, error));
                    System.out.println("Something went wrong! " + error.getMessage());
                }) {
            @Override
            public Map<String, String> getHeaders() throws AuthFailureError {
                return AuthHelper.getAuthHeaders(context);
            }
        };
        requestQueue.add(request);
        return res;
    }

    public LiveData<Resource<List<Club>>> getOrgsWhereUserIsMember() {
        val cached = cache.getMyMembershipsClubs();
        if (cached.getValue() != null) {
            return cached;
        }
        String url = ENDPOINT + "member";
        JsonArrayRequest jar = new JsonArrayRequest(Request.Method.GET, url, null,
                response -> {
                    val resultArr = Json.fromJson(response.toString(), Club[].class);
                    val resultList = Arrays.asList(resultArr);
                    cached.setValue(Resource.success(resultList));
                },
                error -> {
                    cached.setValue(Resource.error(null, error));
                }) {
            @Override
            public Map<String, String> getHeaders() throws AuthFailureError {
                return AuthHelper.getAuthHeaders(context);
            }
        };
        requestQueue.add(jar);
        return cached;
    }

    public MutableLiveData<Resource<List<Club>>> getAll(LifecycleOwner owner) {
        val cached = cache.getHomepageClubs();
        if (cached.getValue() != null) {
                return cached;
            }
        cached.setValue(Resource.loading());
        JsonArrayRequest jar = new JsonArrayRequest(Request.Method.GET, ENDPOINT, null,
                response -> {
                    val clubsArr = Json.fromJson(response.toString(), Club[].class);
                    val clubs = Arrays.asList(clubsArr);
                    cached.setValue(Resource.success(clubs));
                    pairImagesAndClubs(owner, cached);
                },
                error -> {
                    cached.setValue(Resource.error(null, error));
                }) {
            @Override
            public Map<String, String> getHeaders() throws AuthFailureError {
                return AuthHelper.getAuthHeaders(context);
            }
        };

        requestQueue.add(jar);
        return cached;
    }

    private void pairImagesAndClubs(LifecycleOwner owner, MutableLiveData<Resource<List<Club>>> data) {
        ImageRepository.getInstance(context).pairImageAndClub(data, owner);
    }

    public LiveData<Resource<String>> delete(Club club) {
        throw new UnsupportedOperationException("TODO: Implement method");
    }

    public LiveData<Resource<Member>> join(Club club) {
        String url = ENDPOINT + club.getOid() + "/" + JOIN;
        final int[] statusCode = new int[1]; // make variable effectively final to use it inside lambda
        MutableLiveData res = new MutableLiveData();
        cache.getMyMemberships().put(club.getOid(), res);
        JsonArrayRequest jsonRequest = new JsonArrayRequest(Request.Method.POST, url, null,
                response -> {
                    int result = statusCode[0];
                    if (result != 0) {
                        Member member = parseMembershipResponse(response);
                        res.setValue(Resource.success(member));
                    }
                },
                error -> {
                    res.setValue(Resource.error(null, error));

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

    public Resource<Club> get(long oid) {
        val cached = cache.getHomepageClubs();
        if (cached.getValue() != null) {
            if (cached.getValue().getStatus() == Status.SUCCESS) {
                    val list = cached.getValue().getData();
                    Optional<Club> item = list.stream()
                            .filter(p -> p.getOid() == oid)
                            .findFirst();
                    if (item.isPresent()) {
                        return Resource.success(item.get());
                    }
                }
        }
        return Resource.error(null, null);
    }

    public LiveData<Resource<List<Member>>> getMembers(long oid) {
        throw new UnsupportedOperationException("TODO: Implement method");
    }

    public LiveData<Resource<List<Club>>> getManaged(LifecycleOwner owner) {
        String url = ENDPOINT + "managed";
        MutableLiveData cached = cache.getManagedClubs();
        if (cached.getValue() != null) {
            return cached;
        }
        JsonArrayRequest jar = new JsonArrayRequest(Request.Method.GET, url, null,
                response -> {
                    val clubsArr = Json.fromJson(response.toString(), Club[].class);
                    val clubs = Arrays.asList(clubsArr);
                    cached.setValue(Resource.success(clubs));
                    pairImagesAndClubs(owner, cached);
                },
                error -> {
                    cached.setValue(Resource.error("Failed to load managed clubs", error));
                }) {
            @Override
            public Map<String, String> getHeaders() throws AuthFailureError {
                return AuthHelper.getAuthHeaders(context);
            }
        };
        requestQueue.add(jar);
        return cached;
    }

    public LiveData<Resource<Member>> getMembership(Club club) {
        String url = ENDPOINT + club.getOid() + "/" + MEMBERSHIP;
        val cached = cache.getMyMemberships().get(club.getOid());
        if (cached != null) {
            return cached;
        }
        MutableLiveData res = new MutableLiveData<>();
        cache.getMyMemberships().put(club.getOid(), res);
        JsonArrayRequest jar = new JsonArrayRequest(Request.Method.GET, url, null,
                response -> {
                    Member received = parseMembershipResponse(response);
                    res.setValue(Resource.success(received));
                },
                error -> {
                    res.setValue(Resource.error(null, error));

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
