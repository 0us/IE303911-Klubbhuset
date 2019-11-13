package no.ntnu.klubbhuset.ui.managerviews;

import android.app.Application;

import androidx.lifecycle.AndroidViewModel;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.toolbox.JsonArrayRequest;
import com.android.volley.toolbox.Volley;

import org.json.JSONException;

import java.util.ArrayList;
import java.util.List;

import no.ntnu.klubbhuset.data.model.Club;


public class ManagerViewModel extends AndroidViewModel {

    private MutableLiveData<List<Club>> clubs;
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
