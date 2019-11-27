package no.ntnu.klubbhuset.data.repository;

import android.app.Application;
import android.util.Log;

import androidx.annotation.NonNull;
import androidx.collection.LongSparseArray;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;

import com.android.volley.AuthFailureError;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.Volley;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.Map;
import java.util.Objects;

import no.ntnu.klubbhuset.data.cache.Cache;
import no.ntnu.klubbhuset.data.Resource;
import no.ntnu.klubbhuset.data.model.Club;
import no.ntnu.klubbhuset.data.model.Member;
import no.ntnu.klubbhuset.util.AuthHelper;

import static no.ntnu.klubbhuset.util.CommunicationConfig.checkHasPaid;

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

    private final String TAG = "AdminOrganizationRepository";

    private Cache cache = Cache.getInstance();


    private AdminOrganizationRepository(Application context, Club club) {
        this.club = club;
        this.context = context;
        this.requestQueue = Volley.newRequestQueue(context);
    }

    public LiveData<Resource<Member>> getMembers() {
        throw new UnsupportedOperationException("TODO: Implement method");
    }

    public LiveData<Resource<Boolean>> isAdmin() {
        throw new UnsupportedOperationException("TODO: Implement method");
    }

    public LiveData<Resource<String>> hasMemberPaid(String email) {
        String url = checkHasPaid(2);
        MutableLiveData<Resource<String>> userPaymentStatus = new MutableLiveData<>();
        try {
            JSONObject jsonObject = new JSONObject().put("email", email);
            JsonObjectRequest request = new JsonObjectRequest(
                    Request.Method.POST,
                    url,
                    jsonObject,
                    response -> {
                        userPaymentStatus.setValue(Resource.success(response.toString()));
                    },
                    error -> {
                        if (error.networkResponse.statusCode == 402) {
                            String message = new String(error.networkResponse.data);
                            userPaymentStatus.setValue(Resource.error(message, error));
                        }
                    }
            ) {
                @Override
                public Map<String, String> getHeaders() throws AuthFailureError {
                    return AuthHelper.getAuthHeaders(context);
                }
            };
            requestQueue.add(request);
        } catch (
                JSONException e) {
            Log.e(TAG, Objects.requireNonNull(e.getMessage()));
        }
        return userPaymentStatus;
    }
}
