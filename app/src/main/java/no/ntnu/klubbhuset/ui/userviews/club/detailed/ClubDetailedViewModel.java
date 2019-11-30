package no.ntnu.klubbhuset.ui.userviews.club.detailed;

import android.app.Application;
import android.util.Log;

import androidx.annotation.NonNull;
import androidx.lifecycle.AndroidViewModel;
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
import com.google.gson.Gson;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.InvalidObjectException;
import java.io.UnsupportedEncodingException;
import java.util.Calendar;
import java.util.HashMap;
import java.util.Map;

import no.ntnu.klubbhuset.R;
import no.ntnu.klubbhuset.data.CommunicationConfig;
import no.ntnu.klubbhuset.data.model.Club;
import no.ntnu.klubbhuset.data.model.Group;
import no.ntnu.klubbhuset.data.model.Member;
import no.ntnu.klubbhuset.data.model.OrderId;
import no.ntnu.klubbhuset.data.model.User;
import no.ntnu.klubbhuset.data.model.VippsJsonProperties;
import no.ntnu.klubbhuset.data.model.VippsPaymentDetails;
import no.ntnu.klubbhuset.util.AuthHelper;
import no.ntnu.klubbhuset.util.PreferenceUtils;

import static no.ntnu.klubbhuset.data.CommunicationConfig.API_URL;
import static no.ntnu.klubbhuset.data.CommunicationConfig.MEMBERSHIP;
import static no.ntnu.klubbhuset.data.CommunicationConfig.ORGANIZATION;
import static no.ntnu.klubbhuset.data.CommunicationConfig.USER;
import static no.ntnu.klubbhuset.data.model.VippsJsonProperties.APPLICATION_JSON;
import static no.ntnu.klubbhuset.data.model.VippsJsonProperties.AUTHORIZATION;
import static no.ntnu.klubbhuset.data.model.VippsJsonProperties.CONTENT_TYPE;
import static no.ntnu.klubbhuset.data.model.VippsJsonProperties.ECOMM_V_2_PAYMENTS;
import static no.ntnu.klubbhuset.data.model.VippsJsonProperties.OCP_APIM_SUBSCRIPTION_KEY_STRING;
import static no.ntnu.klubbhuset.data.model.VippsJsonProperties.UTF_8;
import static no.ntnu.klubbhuset.data.model.VippsJsonProperties.VIPPS_URL;


public class ClubDetailedViewModel extends AndroidViewModel {
    private static final String TAG = "ClubDetailedViewModel";

    // TODO: Implement the ViewModel
    private RequestQueue requestQueue;
    private MutableLiveData<Member> membership;
    private Gson gson;
    private static Club focusedClub;
    private MutableLiveData<User> user;
    private MutableLiveData<String> deeplink;


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

    public LiveData<User> getUser() {
        if(user == null) {
            user = new MutableLiveData<>();
            loadUser();
        }
        return user;
    }

    public LiveData<String> getDeeplink(User user) {
        if(deeplink == null) {
            deeplink = new MutableLiveData<>();
            retriveDeepLink(user, focusedClub);
        }
        return deeplink;
    }


    private void loadUser() {
        JsonObjectRequest request = new JsonObjectRequest(Request.Method.GET,API_URL + USER, null,
                response -> {
                    try {
                        Log.i(TAG, "loadUser: Got response " + response);
                        user.setValue(new User(response));
                    } catch (InvalidObjectException e) {
                        e.printStackTrace();
                    }
                },
                error -> {}) {
            @Override
            public Map<String, String> getHeaders() throws AuthFailureError {
                return AuthHelper.getAuthHeaders(getApplication());
            }
        };
        requestQueue.add(request);
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
        loadMembership(club);
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


    public VippsPaymentDetails getVippsPaymentDetails(User user, Club club) {


        String phoneNumber = user.getPhone().substring(user.getPhone().length() - 8); // getting last 8 digits of phone number. This to avoid country code
        String organizationId = String.valueOf(club.getOid());
        String userId = user.getEmail();
        OrderId orderId;

        if (organizationId.length() > 20 || userId.length() > 20 || (organizationId + userId).trim().length() > 20) {
            orderId = new OrderId("", ""); // order id can only be 30 charachers long. timestamp is 10 chars long.
        } else {
            orderId = new OrderId(organizationId, userId);
        }

        Double amount = club.getPriceOfMembership().doubleValue();

        String transactionText = String.format("%s %s %s %s",
                getApplication().getResources().getString(R.string.membership_of),
                club.getName(),
                getApplication().getResources().getString(R.string.for_year),
                Calendar.getInstance().get(Calendar.YEAR));

        return new VippsPaymentDetails(phoneNumber, orderId, amount, transactionText, getApplication());
    }

    public void retriveDeepLink(User user, Club club) {
        VippsPaymentDetails details = getVippsPaymentDetails(user, club);

        JSONObject body = null;
        try {
            body = details.getBody();
        } catch (JSONException e) {
            e.printStackTrace();
        }
        JsonObjectRequest request = new JsonObjectRequest(Request.Method.POST, VIPPS_URL + ECOMM_V_2_PAYMENTS, body,
                response -> {
                    try {
                        String vippsDeepLink = (String) response.get("url");
                        deeplink.setValue(vippsDeepLink);
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                }, error -> {
            Log.d(TAG, "retriveDeepLink: error: " + error);
            String responseBody;
            //get status code here
            String statusCode = String.valueOf(error.networkResponse.statusCode);
            //get response body and parse with appropriate encoding
            if (error.networkResponse.data != null) {
                try {
                    responseBody = new String(error.networkResponse.data, UTF_8);
                    Log.d(TAG, "retriveDeepLink: responsebody: " + responseBody);
                } catch (UnsupportedEncodingException e) {
                    e.printStackTrace();
                }
            }
        }) {
            @Override
            public Map<String, String> getHeaders() {
                Map<String, String> headers = new HashMap<>();
                String vippsToken = PreferenceUtils.getVippsAccessToken(getApplication());
                headers.put(AUTHORIZATION, VippsJsonProperties.BEARER + vippsToken);
                headers.put(CONTENT_TYPE, APPLICATION_JSON);
                headers.put(OCP_APIM_SUBSCRIPTION_KEY_STRING, CommunicationConfig.getInstance(getApplication()).retrieveOcpApimSubscriptionKey());
                return headers;
            }
        };
        requestQueue.add(request);
    }
}
