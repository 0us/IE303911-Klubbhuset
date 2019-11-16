package no.ntnu.klubbhuset.ui.managerviews;

import android.app.Application;

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
import java.util.List;
import java.util.Map;

import no.ntnu.klubbhuset.data.CommunicationConfig;
import no.ntnu.klubbhuset.data.model.Club;
import no.ntnu.klubbhuset.util.AuthHelper;

import static no.ntnu.klubbhuset.data.CommunicationConfig.API_URL;
import static no.ntnu.klubbhuset.data.CommunicationConfig.ORGANIZATION;


public class ManagerViewModel extends AndroidViewModel {

    private MutableLiveData<List<Club>> clubs;
    private MutableLiveData<Club> createdClub;
    private RequestQueue requestQueue;

    public ManagerViewModel(Application context) {
        super(context);
        requestQueue = Volley.newRequestQueue(context);
    }

    public LiveData<List<Club>> getManagedClubs() {
        if (clubs == null) {
            clubs = new MutableLiveData<>();
            loadManagedClubs();
        }
        return clubs;
    }

    public LiveData<Club> createNewClub(Club club){
        if (createdClub == null) {
            createdClub = new MutableLiveData<>();
        }
        createClubRequest(club);
        return createdClub;
    }

    private void createClubRequest(Club club) {
        String url = API_URL + ORGANIZATION;
        JSONObject jsonObject = club.toJson();
        JsonObjectRequest request = new JsonObjectRequest(Request.Method.POST, url, jsonObject,
                response -> {
                    createdClub.setValue(new Club(response));
                }, error -> {

        });
        requestQueue.add(request);
    }

    private void loadManagedClubs() {
        String url = API_URL + ORGANIZATION + "/owned";
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
}
