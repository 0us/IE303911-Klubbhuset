package no.ntnu.klubbhuset.viewmodels;

import android.app.Application;
import android.util.Log;

import androidx.annotation.NonNull;
import androidx.lifecycle.AndroidViewModel;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;

import com.android.volley.AuthFailureError;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.Volley;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.Map;
import java.util.Objects;

import no.ntnu.klubbhuset.data.Resource;
import no.ntnu.klubbhuset.data.model.Club;
import no.ntnu.klubbhuset.data.repository.AdminOrganizationRepository;
import no.ntnu.klubbhuset.util.AuthHelper;

import static no.ntnu.klubbhuset.util.CommunicationConfig.checkHasPaid;

public class BarcodeViewModel extends AndroidViewModel {

    private static final String TAG = "BarcodeViewModel";
    private static final String JSON_MSG = "msg";
    public static final String PAYMENT_STATUS_OK = "OK!";
    public static final String PAYMENT_STATUS_NOT_OK = "NOT PAYED!";
    public static final String MEMBER_NOT_FOUND = "NOT FOUND!";
    private Application context;

    public BarcodeViewModel(@NonNull Application context) {
        super(context);
        this.context = context;
    }

    public LiveData<Resource<String>> getUserPaymentStatus(String email, Club club) {
        return AdminOrganizationRepository.getInstance(context, club).hasMemberPaid(email, club);
    }
}