package com.example.ie303911_medlemsapp.ui.userviews.home;

import android.app.Application;

import androidx.lifecycle.AndroidViewModel;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.toolbox.JsonArrayRequest;
import com.android.volley.toolbox.Volley;
import com.example.ie303911_medlemsapp.data.model.Club;

import org.json.JSONException;

import java.util.ArrayList;
import java.util.List;

public class ClubsViewModel extends AndroidViewModel {
    // TODO: Implement the ViewModel
    MutableLiveData<List<Club>> clubs;

    RequestQueue requestQueue;


    public ClubsViewModel(Application context) {
        super(context);
        requestQueue = Volley.newRequestQueue(context);
    }

    public LiveData<List<Club>> getSellables() {
        if (clubs == null) {
            clubs = new MutableLiveData<>();
            loadClubs();
        }
        return clubs;
    }

    protected void loadClubs() {
        String url = "0.0.0.0"; // TODO: get real url
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
                }, System.out::println);
        requestQueue.add(jar);
    }
}
