package no.ntnu.klubbhuset.data.repository;

import android.content.Context;

import androidx.lifecycle.MutableLiveData;

import com.android.volley.AuthFailureError;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.toolbox.JsonArrayRequest;
import com.android.volley.toolbox.Volley;

import java.util.Arrays;
import java.util.List;
import java.util.Map;

import lombok.val;
import no.ntnu.klubbhuset.data.Resource;
import no.ntnu.klubbhuset.data.cache.Cache;
import no.ntnu.klubbhuset.data.model.Club;
import no.ntnu.klubbhuset.data.model.Member;
import no.ntnu.klubbhuset.util.AuthHelper;
import no.ntnu.klubbhuset.util.Json;

import static no.ntnu.klubbhuset.util.CommunicationConfig.API_URL;
import static no.ntnu.klubbhuset.util.CommunicationConfig.ORGANIZATION;

public class MemberRepository {
    private static final String TAG = "MemberRepository";
    private static volatile MemberRepository ourInstance;

    private Cache cache = Cache.getInstance();

    private final String ENDPOINT = API_URL + ORGANIZATION;

    private Context context;
    private RequestQueue requestQueue;

    public static MemberRepository getInstance(Context context) {
        if( ourInstance == null) {
            ourInstance = new MemberRepository(context);
        }
        return ourInstance;
    }

    public MemberRepository(Context context) {
        this.context = context;
        this.requestQueue = Volley.newRequestQueue(context);
    }

    public MutableLiveData<Resource<List<Member>>> getMembers(Club club, boolean forceRefresh) {
        val cached = cache.getClubMembers();
        if(cached.getValue() != null && !forceRefresh) {
            return cached;
        }

        String url = ENDPOINT + "/" + club.getOid() + "/admin";
        JsonArrayRequest request = new JsonArrayRequest(Request.Method.GET, url, null,
                response -> {
                    val resultArray = Json.fromJson(response.toString(), Member[].class);
                    List<Member> members = Arrays.asList(resultArray);
                    cached.setValue(Resource.success(members));
                },
                error -> {cached.setValue(Resource.error(null, error));
        }) {
            @Override
            public Map<String, String> getHeaders() throws AuthFailureError {
                return AuthHelper.getAuthHeaders(context);
            }
        };
        requestQueue.add(request);
        return cached;
    }
}
