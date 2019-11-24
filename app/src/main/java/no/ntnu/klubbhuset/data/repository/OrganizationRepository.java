package no.ntnu.klubbhuset.data.repository;

import android.app.Application;

import com.android.volley.AuthFailureError;
import com.android.volley.NetworkResponse;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.toolbox.JsonArrayRequest;
import com.android.volley.toolbox.Volley;

import org.json.JSONArray;

import java.util.List;
import java.util.Map;

import no.ntnu.klubbhuset.data.CommunicationConfig;
import no.ntnu.klubbhuset.data.Result;
import no.ntnu.klubbhuset.data.model.Club;
import no.ntnu.klubbhuset.data.model.Group;
import no.ntnu.klubbhuset.data.model.Member;
import no.ntnu.klubbhuset.util.AuthHelper;
import no.ntnu.klubbhuset.util.Json;

import static no.ntnu.klubbhuset.data.CommunicationConfig.API_URL;
import static no.ntnu.klubbhuset.data.CommunicationConfig.JOIN;
import static no.ntnu.klubbhuset.data.CommunicationConfig.MEMBERSHIP;
import static no.ntnu.klubbhuset.data.CommunicationConfig.ORGANIZATION;

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

    private Result<Member> joinResult;
    private Result<Member> getMembershipResult;


    private OrganizationRepository(Application context) {
        this.context = context;
        this.requestQueue = Volley.newRequestQueue(context);
    }

    public Result<Club> create(Club club) {
        throw new UnsupportedOperationException("TODO: Implement method");
    }

    public Result<List<Club>> getAll() {
        throw new UnsupportedOperationException("TODO: Implement method");

    }

    public Result<String> delete(Club club) {
        throw new UnsupportedOperationException("TODO: Implement method");
    }

    public Result<Member> join(Club club) {
        String url = ENDPOINT + "/" + club.getOid() + "/" + JOIN;
        final int[] statusCode = new int[1]; // make variable effectively final to use it inside lambda
        JsonArrayRequest jsonRequest = new JsonArrayRequest(Request.Method.POST, url, null,
                response -> {
                    int result = statusCode[0];
                    if (result != 0) {
                        Member member = parseMembershipResponse(response);
                        joinResult = new Result.Success<>(member);
                    }
                },
                error -> {
                    joinResult = new Result.Error(error);

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
        return joinResult;
    }

    public Result<Club> get(Club club) {
        throw new UnsupportedOperationException("TODO: Implement method");

    }

    public Result<List<Member>> getMembers(long oid) {
        throw new UnsupportedOperationException("TODO: Implement method");

    }

    public Result<List<Club>> getManaged(long uid) {
        throw new UnsupportedOperationException("TODO: Implement method");

    }

    public Result<Member> getMembership(Club club) {
        String url = ENDPOINT + "/" + club.getOid() + "/" + MEMBERSHIP;
        JsonArrayRequest jar = new JsonArrayRequest(Request.Method.GET, url, null,
                response -> {
                    Member received = parseMembershipResponse(response);
                    getMembershipResult = new Result.Success<>(received);
                }, error -> {
            getMembershipResult = new Result.Error(error);

        }) {
            @Override
            public Map<String, String> getHeaders() throws AuthFailureError {
                return AuthHelper.getAuthHeaders(context);
            }
        };
        requestQueue.add(jar);
        return getMembershipResult;
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
