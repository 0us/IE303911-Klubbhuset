package no.ntnu.klubbhuset.ui.userviews.club.detailed;

import android.app.Application;

import androidx.annotation.NonNull;
import androidx.lifecycle.AndroidViewModel;
import androidx.lifecycle.MutableLiveData;

import com.android.volley.AuthFailureError;
import com.android.volley.NetworkResponse;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonArrayRequest;
import com.android.volley.toolbox.Volley;
import com.google.gson.Gson;

import org.json.JSONArray;

import java.net.HttpURLConnection;
import java.util.Map;

import no.ntnu.klubbhuset.data.CommunicationConfig;
import no.ntnu.klubbhuset.data.model.Club;
import no.ntnu.klubbhuset.data.model.Group;
import no.ntnu.klubbhuset.data.model.Member;
import no.ntnu.klubbhuset.util.AuthHelper;

import static no.ntnu.klubbhuset.data.CommunicationConfig.API_URL;
import static no.ntnu.klubbhuset.data.CommunicationConfig.MEMBERSHIP;
import static no.ntnu.klubbhuset.data.CommunicationConfig.ORGANIZATION;


public class ClubDetailedViewModel extends AndroidViewModel {
    // TODO: Implement the ViewModel
    private RequestQueue requestQueue;
    private MutableLiveData<Member> membership;
    private Gson gson;
    private static Club focusedClub;

    public ClubDetailedViewModel(@NonNull Application application) {
        super(application);
        requestQueue = Volley.newRequestQueue(application);
        gson = new Gson();
        membership = new MutableLiveData<>();
    }

    public MutableLiveData<Member> joinClub(long oid) {
        tryJoinClub(oid);
        return membership;
    }


    private void tryJoinClub(long oid) {
        String url = CommunicationConfig.joinClub(oid);
        boolean returnValue = false;
        final int[] statusCode = new int[1]; // make variable effectively final to use it inside lambda
        JsonArrayRequest jsonRequest = new JsonArrayRequest(Request.Method.POST, url, null,
                response -> {
                    int result = statusCode[0];
                    if (result != 0) {
                        membership.setValue(parseMembershipResponse(response));
                    }
                },
                error -> {
                    membership.setValue(null);
                }) {
            @Override
            protected Response<JSONArray> parseNetworkResponse(NetworkResponse response) {
                // override parseNetorkResponse to get status code
                statusCode[0] = response.statusCode;
                return super.parseNetworkResponse(response);
            }

            @Override
            public Map<String, String> getHeaders() throws AuthFailureError {
                return AuthHelper.getAuthHeaders(getApplication());
            }
        };
        requestQueue.add(jsonRequest);
    }

    private void loadMembership(Club club) {
        String url = API_URL + ORGANIZATION + "/" + club.getOid() + "/" + MEMBERSHIP;
        JsonArrayRequest jar = new JsonArrayRequest(Request.Method.GET, url, null,
                response -> {
                    Member result = parseMembershipResponse(response);
                    membership.setValue(result);
                }, error -> {

        }) {
            @Override
            public Map<String, String> getHeaders() throws AuthFailureError {
                return AuthHelper.getAuthHeaders(getApplication());
            }
        };
        requestQueue.add(jar);
    }

    /**
     * @param json
     * @return the membership with the highest access level Group
     */
    private Member parseMembershipResponse(JSONArray json) {
        Member[] memberships = gson.fromJson(json.toString(), Member[].class);
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

    /**
     * get users membership status in given organization
     *
     * @param club
     * @return
     */
    public MutableLiveData<Member> getMembership(Club club) {
        if (membership == null) {
            loadMembership(club);
        }
        return membership;
    }

    public static Club getCurrentClub() {
        return focusedClub;
    }

    /**
     * set the currently focused organization, for use with
     * clubDetailedView
     *
     * @param currentClub
     */
    public static void setCurrentClub(Club currentClub) {
        focusedClub = currentClub;
    }

}
