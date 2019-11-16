package no.ntnu.klubbhuset.ui.managerviews;

import android.app.Application;
import android.content.Context;
import android.content.SharedPreferences;

import androidx.lifecycle.AndroidViewModel;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;

import com.android.volley.NetworkResponse;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.Volley;

import org.json.JSONObject;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import no.ntnu.klubbhuset.data.model.Club;

import static no.ntnu.klubbhuset.data.CommunicationConfig.API_URL;
import static no.ntnu.klubbhuset.data.CommunicationConfig.ORGANIZATION;


public class ManagerViewModel extends AndroidViewModel {

    private final SharedPreferences pref;
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
            loadManagedClubs();
        }
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
                }, error -> {
            System.out.println("Something went wrong! " + error.getMessage());
        }) {
            @Override
            public Map<String, String> getHeaders() {
                Map<String, String> params = new HashMap<>();
                params.put("Content-Type", "application/json; charset=UTF-8");
                params.put("Authorization", "Bearer " + pref.getString("token", ""));
                return params;
            }
        };
        requestQueue.add(request);
    }

    private void loadManagedClubs() {
        //String url = //TODO api to get all orgs where user is admin
        /*JsonArrayRequest jar = new JsonArrayRequest(Request.Method.GET, url, null,
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
                }, System.out::println);
        requestQueue.add(jar);*/
    }
}
