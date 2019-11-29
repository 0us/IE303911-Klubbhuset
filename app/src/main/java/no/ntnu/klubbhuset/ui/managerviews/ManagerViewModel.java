package no.ntnu.klubbhuset.ui.managerviews;

import android.app.Application;
import android.content.Context;
import android.content.SharedPreferences;
import android.util.Patterns;
import android.widget.Toast;

import androidx.lifecycle.AndroidViewModel;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;

import com.android.volley.AuthFailureError;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.toolbox.JsonArrayRequest;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.Volley;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;


import no.ntnu.klubbhuset.adapter.AuthenticationPageAdapter;
import no.ntnu.klubbhuset.data.CommunicationConfig;

import no.ntnu.klubbhuset.R;

import no.ntnu.klubbhuset.data.model.Club;
import no.ntnu.klubbhuset.util.AuthHelper;

import static no.ntnu.klubbhuset.data.CommunicationConfig.API_URL;
import static no.ntnu.klubbhuset.data.CommunicationConfig.ORGANIZATION;


public class ManagerViewModel extends AndroidViewModel {
    private static final String TAG = "ManagerViewModel";

    private final SharedPreferences pref;
    private MutableLiveData<CreateOrganizationFormState> createOrganizationFormState = new MutableLiveData<>();
    private MutableLiveData<List<Club>> clubs;
    private MutableLiveData<Club> createdClub;
    private RequestQueue requestQueue;

    public ManagerViewModel(Application context) {
        super(context);
        requestQueue = Volley.newRequestQueue(context);
        this.pref = getApplication().getSharedPreferences("login", Context.MODE_PRIVATE);
    }

    public LiveData<List<Club>> getManagedClubs() {
        if (clubs == null) {
            clubs = new MutableLiveData<>();
        }
        loadManagedClubs();
        return clubs;
    }

    public LiveData<Club> createNewClub(Club club, byte[] imageInByte) {
        if (createdClub == null) {
            createdClub = new MutableLiveData<>();
        }
        createClubRequest(club, imageInByte);
        return createdClub;
    }

    private void createClubRequest(Club club, byte[] imageInByte) {
        String url = API_URL + ORGANIZATION;
        JSONObject jsonObject = club.toJson(imageInByte);
        JsonObjectRequest request = new JsonObjectRequest(Request.Method.POST, url, jsonObject,
                response -> {
                    createdClub.setValue(new Club(response));
                    Toast.makeText(
                            getApplication().getApplicationContext(),
                            "Organzation got created successfully!",
                            Toast.LENGTH_SHORT)
                            .show();
                }, error -> {
            System.out.println("Something went wrong! " + error.getMessage());
        }) {
            @Override
            public Map<String, String> getHeaders() throws AuthFailureError {
                return AuthHelper.getAuthHeaders(getApplication());
            }
        };
        requestQueue.add(request);
    }

    private void loadManagedClubs() {
        String url = API_URL + ORGANIZATION + "/managed";
        JsonArrayRequest jar = new JsonArrayRequest(Request.Method.GET, url, null,
                response -> {
                    List<Club> clubs = new ArrayList<>();
                    try {
                        for (int i = 0; i < response.length(); i++) {
                            clubs.add(new Club(response.getJSONObject(i)));
                        }
                    } catch (JSONException jex) {
                        System.out.println(jex);
                    }
                    this.clubs.setValue(clubs);
                }, System.out::println){
            @Override
            public Map<String, String> getHeaders() throws AuthFailureError {
                return AuthHelper.getAuthHeaders(getApplication());
            }
        };
        requestQueue.add(jar);
    }

    public void organizationDataChanged(String email) {
        boolean emailValid = isEmailValid(email);
        if (!emailValid) {
            createOrganizationFormState.setValue(new CreateOrganizationFormState(R.string.invalid_email));
        } else {
            createOrganizationFormState.setValue(new CreateOrganizationFormState(true));
        }
    }

    private boolean isEmailValid(String email) {
        if (email == null) {
            return false;
        }
        boolean contains = email.contains("@");
        if(!contains) {
            return false;
        }

        if(email.contains("@")) {
            return Patterns.EMAIL_ADDRESS.matcher(email).matches();
        } else {
            return !email.trim().isEmpty();
        }
    }

    public MutableLiveData<CreateOrganizationFormState> getCreateOrganizationFormState() {
        return createOrganizationFormState;
    }
}
