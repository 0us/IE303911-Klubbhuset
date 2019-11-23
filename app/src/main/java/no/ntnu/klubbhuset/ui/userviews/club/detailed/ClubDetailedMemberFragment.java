package no.ntnu.klubbhuset.ui.userviews.club.detailed;

import android.content.Intent;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProviders;

import com.android.volley.AuthFailureError;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.Volley;
import com.g00fy2.versioncompare.Version;

import org.json.JSONException;
import org.json.JSONObject;

import java.text.SimpleDateFormat;
import java.util.HashMap;
import java.util.Map;
import java.util.Properties;
import java.time.LocalDateTime;
import java.util.Date;

import no.ntnu.klubbhuset.R;
import no.ntnu.klubbhuset.data.model.Club;
import no.ntnu.klubbhuset.data.model.Member;
import no.ntnu.klubbhuset.data.model.OrderId;
import no.ntnu.klubbhuset.data.model.VippsPaymentDetails;


/**
 * Displays information relating to a users membership in an organization, like
 * payment-information etc
 */
public class ClubDetailedMemberFragment extends Fragment {
    private static final String TAG = "ClubDetailedMemberFragm";

    public static final String MERCHANT_SERIAL_NUMBER = "merchantSerialNumber";
    private static final String VIPPS_URL = "https://apitest.vipps.no";
    private static final int REQUEST_CODE = 100; // Success code https://github.com/vippsas/vipps-ecom-api/blob/master/vipps-ecom-api.md#error-codes-for-deeplinking
    public static final String MINIMUM_REQUIRED_VIPPS_VERSION = "1.8.0";
    private ClubDetailedViewModel mViewModel;
    private Club club;
    private Member member;
    private RequestQueue queue = Volley.newRequestQueue(getActivity());
    private Properties properties = new Properties();

    public static ClubDetailedMemberFragment newInstance(Member member) {
        Bundle args = new Bundle();
        args.putSerializable("member", member);
        ClubDetailedMemberFragment newInstance = new ClubDetailedMemberFragment();
        newInstance.setArguments(args);
        return newInstance;
    }

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.content_club_detailed_member, container, false);
        this.club = ClubDetailedViewModel.getCurrentClub();
        this.member = (Member) getArguments().getSerializable("member");

        vippsBtn = view.findViewById(R.id.club_detailed_pay_with_vipps);
        paymentStatusImg = view.findViewById(R.id.club_detailed_paid_status_img);
        paymentStatusHeader = view.findViewById(R.id.club_detailed_payment_status_text);
        paymentStatusText = view.findViewById(R.id.club_detailed_payment_status_description);
        paymentDueDate = view.findViewById(R.id.club_detailed_due_date);
        return view;
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        mViewModel = ViewModelProviders.of(this).get(ClubDetailedViewModel.class);

        TextView memberSince = getView().findViewById(R.id.club_detailed_member_since);
        SimpleDateFormat format = new SimpleDateFormat("dd MMMM yyyy");
        memberSince.setText(format.format(member.getCreated()));

        mViewModel.getMembership(club).observe(this, response -> {
            this.member = response;
            if (response.isHasPaid()) {
                vippsBtn.setVisibility(View.GONE);
                paymentStatusText.setText(getString(R.string.payment_true));
                paymentDueDate.setText("");
                paymentStatusImg.setImageResource(R.drawable.ic_check_black_24dp);
            } else {
                vippsBtn.setVisibility(View.VISIBLE);
                paymentStatusText.setText(getString(R.string.payment_false));
                paymentDueDate.setText(new Date(0).toString()); // todo real get a date
                paymentStatusImg.setImageResource(R.drawable.ic_sentiment_very_dissatisfied_black_24dp);
            }
        });

        vippsBtn.setOnClickListener(l-> payWithVipps());

    }


    private void payWithVipps() {
        OrderId orderId = new OrderId("testOrg", "testUser");
        VippsPaymentDetails details = new VippsPaymentDetails("48059626", orderId, 200, "test transaction"); // fixme

        JSONObject body = null;
        try {
            body = details.getBody();
        } catch (JSONException e) {
            e.printStackTrace();
        }
        JsonObjectRequest request = new JsonObjectRequest(Request.Method.POST, VIPPS_URL + "/ecomm/v2/payment", body,
                response -> {
                    try {
                        String deepLink = (String) response.get("url");
                        openVipps(deepLink);
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                }, error -> {
            Log.d(TAG, "payWithVipps: error: " + error.getMessage());
        }) {
            @Override
            public Map<String, String> getHeaders() throws AuthFailureError {
                Map<String, String> headers = new HashMap<>();
                headers.put("Authorization", "Bearer " + vippsToken);
                headers.put("Content-Type", "application/json");
                headers.put("Ocp-Apim-Subscription-Key", properties.getProperty("Ocp-Apim-Subscription-Key"));
                return headers;
            }
        };
    }

    private void openVipps(String deepLink) {
        try {
            PackageManager pm = getActivity().getPackageManager();
            PackageInfo info = pm.getPackageInfo("no.dnb.vipps", PackageManager.GET_ACTIVITIES);

            boolean higherThan = new Version(info.versionName).isHigherThan(MINIMUM_REQUIRED_VIPPS_VERSION);
            if (higherThan) {
                String uri = deepLink; // Use deeplink url provided in API response
                Intent intent = new Intent(Intent.ACTION_VIEW);
                intent.setData(Uri.parse(uri));
                startActivityForResult(intent, REQUEST_CODE);
            } else {
                // Notify user to download the latest version of Vipps application.
                Toast.makeText(getActivity(), getResources().getText(R.string.latest_vipps_ver), Toast.LENGTH_SHORT).show();
            }
        } catch (PackageManager.NameNotFoundException e) {
            // No Vipps app! Open play store page.
            String url = " https://play.google.com/store/apps/details?id=no.dnb.vipps";
            Intent storeIntent = new Intent(Intent.ACTION_VIEW);
            storeIntent.setData(Uri.parse(url));
            startActivity(storeIntent);
        }
        //todo do :)
    }

}
