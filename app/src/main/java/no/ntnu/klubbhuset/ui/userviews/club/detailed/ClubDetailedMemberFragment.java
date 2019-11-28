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
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProviders;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.Volley;
import com.g00fy2.versioncompare.Version;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.UnsupportedEncodingException;
import java.math.BigDecimal;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

import no.ntnu.klubbhuset.R;
import no.ntnu.klubbhuset.data.CommunicationConfig;
import no.ntnu.klubbhuset.data.model.Club;
import no.ntnu.klubbhuset.data.model.Member;
import no.ntnu.klubbhuset.data.model.OrderId;
import no.ntnu.klubbhuset.data.model.User;
import no.ntnu.klubbhuset.data.model.VippsPaymentDetails;
import no.ntnu.klubbhuset.util.PreferenceUtils;
import no.ntnu.klubbhuset.util.UserHelper;


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
    private RequestQueue queue;
    private View vippsBtn;
    private ImageView paymentStatusImg;
    private TextView paymentStatusText;
    private TextView paymentDueDate;
    private SimpleDateFormat dateFormat = new SimpleDateFormat("dd MMMM yyyy");


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
        paymentStatusText = view.findViewById(R.id.club_detailed_payment_status_description);
        paymentDueDate = view.findViewById(R.id.club_detailed_due_date);
        queue = Volley.newRequestQueue(getActivity());
        return view;
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        mViewModel = ViewModelProviders.of(this).get(ClubDetailedViewModel.class);

        TextView memberSince = getView().findViewById(R.id.club_detailed_member_since);
        memberSince.setText(dateFormat.format(member.getCreated()));

        mViewModel.getMembership(club).observe(this, response -> {
            this.member = response;
            if (response.isHasPaid() || club.getPriceOfMembership() == null || club.getPriceOfMembership().equals(BigDecimal.ZERO)) {
                vippsBtn.setVisibility(View.GONE);
                paymentStatusText.setText(getString(R.string.payment_true));
                paymentDueDate.setText("");
                paymentStatusImg.setImageResource(R.drawable.ic_check_black_24dp);
            } else {
                vippsBtn.setVisibility(View.VISIBLE);
                paymentStatusText.setText(getString(R.string.payment_false));
                paymentDueDate.setText(dateFormat.format(new Date(1995, 1, 1))); // todo real get a date
                paymentStatusImg.setImageResource(R.drawable.ic_sentiment_very_dissatisfied_black_24dp);
            }
        });

        vippsBtn.setOnClickListener(v -> {
            mViewModel.getUser().observe(this, user -> {
                payWithVipps(user);
            });
        });

    }


    private void payWithVipps(User user) {
        VippsPaymentDetails details = getVippsPaymentDetails(user);

        JSONObject body = null;
        try {
            body = details.getBody();
        } catch (JSONException e) {
            e.printStackTrace();
        }
        JsonObjectRequest request = new JsonObjectRequest(Request.Method.POST, VIPPS_URL + "/ecomm/v2/payments", body,
                response -> {
                    try {
                        String deepLink = (String) response.get("url");
                        openVipps(deepLink);
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                }, error -> {
            Log.d(TAG, "payWithVipps: error: " + error);
            String responseBody;
            //get status code here
            String statusCode = String.valueOf(error.networkResponse.statusCode);
            //get response body and parse with appropriate encoding
            if (error.networkResponse.data != null) {
                try {
                    responseBody = new String(error.networkResponse.data, "UTF-8");
                    Log.d(TAG, "payWithVipps: responsebody: " + responseBody);
                } catch (UnsupportedEncodingException e) {
                    e.printStackTrace();
                }
            }
        }) {
            @Override
            public Map<String, String> getHeaders() {
                Map<String, String> headers = new HashMap<>();
                String vippsToken = PreferenceUtils.getVippsAccessToken(getActivity());
                headers.put("Authorization", "Bearer " + vippsToken);
                headers.put("Content-Type", "application/json");
                headers.put("Ocp-Apim-Subscription-Key", CommunicationConfig.getInstance(getActivity()).retrieveOcpApimSubscriptionKey());
                return headers;
            }
        };
        queue.add(request);
    }

    private VippsPaymentDetails getVippsPaymentDetails(User user) {


        String phoneNumber = user.getPhone().substring(user.getPhone().length() - 8); // getting last 8 digits of phone number. This to avoid country code
        String organizationId = String.valueOf(club.getOid());
        String userId = user.getEmail();
        OrderId orderId;

        if (organizationId.length() > 20 || userId.length() > 20 || (organizationId + userId).trim().length() > 20) {
            orderId = new OrderId("", ""); // order id can only be 30 charachers long. timestamp is 10 chars long.
        } else {
            orderId = new OrderId(organizationId, userId);
        }

        Double amount = club.getPriceOfMembership().doubleValue();

        String transactionText = String.format("%s %s %s %s",
                getResources().getString(R.string.membership_of),
                club.getName(),
                getResources().getString(R.string.for_year),
                Calendar.getInstance().get(Calendar.YEAR));

        return new VippsPaymentDetails(phoneNumber, orderId, amount, transactionText, getActivity());
    }

    private User getCurrentUser() throws JSONException {
        return UserHelper.getCurrentUser(getActivity());
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
