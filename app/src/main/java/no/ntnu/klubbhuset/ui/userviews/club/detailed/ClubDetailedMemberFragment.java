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
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProviders;

import com.android.volley.RequestQueue;
import com.android.volley.toolbox.Volley;
import com.g00fy2.versioncompare.Version;

import java.math.BigDecimal;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Objects;

import no.ntnu.klubbhuset.R;
import no.ntnu.klubbhuset.data.Status;
import no.ntnu.klubbhuset.data.model.Member;
import no.ntnu.klubbhuset.viewmodels.ClubDetailedViewModel;

import static no.ntnu.klubbhuset.data.model.VippsJsonProperties.MINIMUM_REQUIRED_VIPPS_VERSION;
import static no.ntnu.klubbhuset.data.model.VippsJsonProperties.NO_DNB_VIPPS_PACKAGE;


/**
 * Displays information relating to a users membership in an organization, like
 * payment-information etc
 */
public class ClubDetailedMemberFragment extends Fragment {
    private static final String TAG = "ClubDetailedMemberFragment";

    public static final String MEMBER_STRING = "member";
    private static final int REQUEST_CODE = 100; // Success code https://github.com/vippsas/vipps-ecom-api/blob/master/vipps-ecom-api.md#error-codes-for-deeplinking
    private ClubDetailedViewModel mViewModel;
    private Member member;
    private RequestQueue queue;
    private ImageButton vippsBtn;
    private ImageView paymentStatusImg;
    private TextView paymentStatusText;
    private TextView paymentDueDate;
    private SimpleDateFormat dateFormat = new SimpleDateFormat("dd MMMM yyyy");


    public static ClubDetailedMemberFragment newInstance(Member member) {
        Bundle args = new Bundle();
        args.putSerializable(MEMBER_STRING, member);
        ClubDetailedMemberFragment newInstance = new ClubDetailedMemberFragment();
        newInstance.setArguments(args);

        return newInstance;
    }

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.content_club_detailed_member, container, false);

        this.member = (Member) getArguments().getSerializable(MEMBER_STRING);
        vippsBtn = view.findViewById(R.id.club_detailed_pay_with_vipps);
        paymentStatusImg = view.findViewById(R.id.club_detailed_paid_status_img);
//        paymentStatusText = view.findViewById(R.id.club_detailed_payment_status_description); TODO Real due date
//        paymentDueDate = view.findViewById(R.id.club_detailed_due_date); TODO Real due date
        queue = Volley.newRequestQueue(getActivity());
        return view;
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        mViewModel = ViewModelProviders.of(getActivity()).get(ClubDetailedViewModel.class);

        TextView memberSince = getView().findViewById(R.id.club_detailed_member_since);
        if (member.getCreated() != null) {

        }
        if (!Objects.isNull(member.getCreated())) {
            memberSince.setText(dateFormat.format(member.getCreated()));
        } else {
            memberSince.setText(dateFormat.format(new Date(0,0,0)));
        }
        mViewModel.getCurrentClub().observe(this, response -> {
            if (response.getStatus() == Status.SUCCESS) {
                if (member.isHasPaid() || response.getData().getPriceOfMembership() == null) {
                    hideVipps();
                } else if (response.getData().getPriceOfMembership() != null && response.getData().getPriceOfMembership().equals(BigDecimal.ZERO)) {
                    hideVipps();
                } else {
                    showVipps();
                }
            }
        });
    }

    private void hideVipps() {
        vippsBtn.setVisibility(View.GONE);

//        paymentStatusText.setText(getString(R.string.payment_true));
//        paymentDueDate.setText("");

        paymentStatusImg.setImageResource(R.drawable.ic_check_black_24dp);
    }

    private void showVipps() {
        vippsBtn.setVisibility(View.VISIBLE);
//        paymentDueDate.setText(dateFormat.format(new Date(1995, 1, 1))); // todo real get a date
        paymentStatusImg.setImageResource(R.drawable.ic_sentiment_very_dissatisfied_black_24dp);
        vippsBtn.setOnClickListener(v -> {
            mViewModel.getUser().observe(this, user -> {
                mViewModel.getDeeplink(user).observe(this, deeplink -> {
                    if (deeplink.getStatus() == Status.SUCCESS) {
                        openVipps(deeplink.getData());
                    } else if (deeplink.getStatus() == Status.ERROR) {
                        Toast.makeText(getContext(), R.string.userfeedback_deeplink_error, Toast.LENGTH_SHORT).show();
                        Log.e(TAG, "Couldn't get deeplink, please contact your personal sysadmin");
                    }
                });
            });
        });
    }

    private void openVipps(String deepLink) {
        try {
            PackageManager pm = getActivity().getPackageManager();
            PackageInfo info = pm.getPackageInfo(NO_DNB_VIPPS_PACKAGE, PackageManager.GET_ACTIVITIES);

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
