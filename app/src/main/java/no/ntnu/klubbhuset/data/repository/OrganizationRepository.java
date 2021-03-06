package no.ntnu.klubbhuset.data.repository;

import android.content.Context;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;

import com.android.volley.AuthFailureError;
import com.android.volley.NetworkResponse;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.HttpHeaderParser;
import com.android.volley.toolbox.JsonArrayRequest;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.Volley;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.Arrays;
import java.util.List;
import java.util.Map;
import java.util.Optional;

import lombok.val;
import lombok.var;
import no.ntnu.klubbhuset.data.Resource;
import no.ntnu.klubbhuset.data.Status;
import no.ntnu.klubbhuset.data.cache.Cache;
import no.ntnu.klubbhuset.data.model.Club;
import no.ntnu.klubbhuset.data.model.Member;
import no.ntnu.klubbhuset.util.AuthHelper;
import no.ntnu.klubbhuset.util.Json;

import static no.ntnu.klubbhuset.util.CommunicationConfig.API_URL;
import static no.ntnu.klubbhuset.util.CommunicationConfig.JOIN;
import static no.ntnu.klubbhuset.util.CommunicationConfig.MEMBERSHIP;
import static no.ntnu.klubbhuset.util.CommunicationConfig.ORGANIZATION;

public class OrganizationRepository {
    private static final String JSON_MSG = "msg";
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
        return getOrgsWhereUserIsMember(false);
    }

    public LiveData<Resource<List<Club>>> getOrgsWhereUserIsMember(boolean forceRefresh) {
        val cached = cache.getMyMembershipsClubs();
        if (cached.getValue() != null && !forceRefresh) {
            return cached;
        }
        String url = ENDPOINT + "member";
        JsonArrayRequest jar = new JsonArrayRequest(Request.Method.GET, url, null,
                response -> {
                    val resultArr = Json.fromJson(response.toString(), Club[].class);
                    val resultList = Arrays.asList(resultArr);
                    cached.setValue(Resource.success(resultList));
                    pairImagesAndClubs(cached);
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

    public LiveData<Resource<List<Club>>> getAll() {
        return getAll(false);
    }


    public LiveData<Resource<List<Club>>> getAll(boolean forceRefresh) {
        var cached = cache.getHomepageClubs();
        if (cached.getValue() != null && !forceRefresh) {
            return cached;
        }
        cached.setValue(Resource.loading());
        JsonArrayRequest jar = new JsonArrayRequest(Request.Method.GET, ENDPOINT, null,
                response -> {
                    val clubsArr = Json.fromJson(response.toString(), Club[].class);
                    val clubs = Arrays.asList(clubsArr);
                    cached.setValue(Resource.success(clubs));
                    pairImagesAndClubs(cached);
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

    private void pairImagesAndClubs(MutableLiveData<Resource<List<Club>>> data) {
        ImageRepository.getInstance(context).pairImageAndClub(data);
    }

    public LiveData<Resource<String>> delete(Club club) {
        throw new UnsupportedOperationException("TODO: Implement method");
    }

    public LiveData<Resource<Member>> join(Club club) {
        String url = ENDPOINT + club.getOid() + "/" + JOIN;
        MutableLiveData res = new MutableLiveData();
        cache.getMyMemberships().put(club.getOid(), res);
        JsonObjectRequest jsonRequest = new JsonObjectRequest(Request.Method.POST, url, null,
                response -> {
                    Member member = Json.fromJson(response.toString(), Member.class);
                    res.setValue(Resource.success(member));
                },
                error -> {
                    res.setValue(Resource.error(null, error));
                }) {
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

    public LiveData<Resource<List<Club>>> getManaged() {
        return getManaged(false);
    }

    public LiveData<Resource<List<Club>>> getManaged(boolean forceRefresh) {
        String url = ENDPOINT + "managed";
        MutableLiveData cached = cache.getManagedClubs();
        if (cached.getValue() != null && !forceRefresh) {
            return cached;
        }
        JsonArrayRequest jar = new JsonArrayRequest(Request.Method.GET, url, null,
                response -> {
                    val clubsArr = Json.fromJson(response.toString(), Club[].class);
                    val clubs = Arrays.asList(clubsArr);
                    cached.setValue(Resource.success(clubs));
                    pairImagesAndClubs(cached);
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
        JsonObjectRequest jar = new JsonObjectRequest(Request.Method.GET, url, null,
                response -> {
                    Member member = Json.fromJson(response.toString(), Member.class);
                    res.setValue(Resource.success(member));
                },
                error -> {
                    res.setValue(Resource.error(null, error));
                }) {
            @Override
            public Map<String, String> getHeaders() throws AuthFailureError {
                return AuthHelper.getAuthHeaders(context);
            }

            @Override
            protected Response<JSONObject> parseNetworkResponse(NetworkResponse response) {
                try {
                    String jsonString = new String(response.data);
                    JSONObject jsonObject = new JSONObject(jsonString);
                    return Response.success(jsonObject, HttpHeaderParser.parseCacheHeaders(response));
                } catch (JSONException e) {
                    return Response.error(new VolleyError(response));
                }
            }
        };

        requestQueue.add(jar);
        return cache.getMyMemberships().get(club.getOid());
    }
}
