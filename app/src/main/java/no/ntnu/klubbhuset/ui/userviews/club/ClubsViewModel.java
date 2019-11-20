package no.ntnu.klubbhuset.ui.userviews.club;

import android.app.Application;
import android.content.Context;
import android.content.SharedPreferences;

import androidx.lifecycle.AndroidViewModel;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;

import com.android.volley.AuthFailureError;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.toolbox.JsonArrayRequest;
import com.android.volley.toolbox.Volley;

import no.ntnu.klubbhuset.data.model.Club;
import no.ntnu.klubbhuset.data.model.Member;
import no.ntnu.klubbhuset.util.AuthHelper;

import org.json.JSONException;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static no.ntnu.klubbhuset.data.CommunicationConfig.API_URL;
import static no.ntnu.klubbhuset.data.CommunicationConfig.MEMBERSHIP;
import static no.ntnu.klubbhuset.data.CommunicationConfig.ORGANIZATION;

public class ClubsViewModel extends AndroidViewModel {
    MutableLiveData<List<Club>> clubs;

    RequestQueue requestQueue;
    private MutableLiveData<Club> selectedClub = new MutableLiveData<>();
    private MutableLiveData<Member> membership = new MutableLiveData<>();


    public ClubsViewModel(Application context) {
        super(context);
        requestQueue = Volley.newRequestQueue(context);
    }

    public LiveData<List<Club>> getClubs() {
        if (clubs == null) {
            clubs = new MutableLiveData<>();
            loadClubs();
        }
        return clubs;
    }

    public LiveData<Club> getSelectedClub() { return selectedClub; }

    public void setSelectedClub(Club selectedClub) {
        this.selectedClub.setValue(selectedClub);
    }

    private void loadClubs() {
        String url = API_URL + ORGANIZATION;

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
                }, System.out::println) {
            @Override
            public Map<String, String> getHeaders() throws AuthFailureError {
                return AuthHelper.getAuthHeaders(getApplication());
            }
        };
        requestQueue.add(jar);
    }

    private void loadMembership(Club club) {
        String url = API_URL + ORGANIZATION +  "/" + club.getOid() + "/" + MEMBERSHIP;
    }

    public void getMembership(Club club) {

    }

    public void joinClub(Club club) {

    }
}
