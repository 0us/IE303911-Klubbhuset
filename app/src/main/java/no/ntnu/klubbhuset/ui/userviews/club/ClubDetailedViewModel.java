package no.ntnu.klubbhuset.ui.userviews.club;

import android.app.Application;

import androidx.annotation.NonNull;
import androidx.lifecycle.AndroidViewModel;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

import com.android.volley.NetworkResponse;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.JsonRequest;
import com.android.volley.toolbox.Volley;

import org.json.JSONObject;

import java.net.HttpURLConnection;


import no.ntnu.klubbhuset.data.CommunicationConfig;


public class ClubDetailedViewModel extends AndroidViewModel {
    // TODO: Implement the ViewModel
    private RequestQueue requestQueue;
    private MutableLiveData<Boolean> joinSuccess;

    public ClubDetailedViewModel(@NonNull Application application) {
        super(application);
        requestQueue = Volley.newRequestQueue(application);
    }

    public MutableLiveData<Boolean> joinClub(int oid) {
        if (joinSuccess == null) {
            joinSuccess = new MutableLiveData<>();
        }
        tryJoinClub(oid);
        return joinSuccess;
    }


    private void tryJoinClub(int oid) {

        String url = CommunicationConfig.joinClub(oid);
        boolean returnValue = false;
        final int[] statusCode = new int[1]; // make variable effectively final to use it inside lambda
        JsonRequest jsonRequest = new JsonObjectRequest(Request.Method.POST, url, null,
                response -> {
                    int result = statusCode[0];
                    if (result != 0) {
                        if (result == HttpURLConnection.HTTP_OK) {
                            joinSuccess.setValue(true);
                        } else {
                            joinSuccess.setValue(false);
                        }
                    }
                },
                error -> {
                    joinSuccess.setValue(false);
                }) {
            @Override
            protected Response<JSONObject> parseNetworkResponse(NetworkResponse response) {
                // override parseNetorkResponse to get status code
                statusCode[0] = response.statusCode;
                return super.parseNetworkResponse(response);
            }
        };
        requestQueue.add(jsonRequest);

    }
}
