package no.ntnu.klubbhuset.ui.userviews.club;

import android.content.Intent;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import no.ntnu.klubbhuset.R;
import no.ntnu.klubbhuset.util.MemberHelper;


public class ClubDetailedFragment extends Fragment {

    private static final int SUCCESS = 100; // success code from vipps app

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        super.onCreateView(inflater, container, savedInstanceState);
        View view = inflater.inflate(R.layout.content_club_detailed, container, false);

        Button join = view.findViewById(R.id.club_detailed_joinbtn);
        Button pay = view.findViewById(R.id.club_detailed_pay_membership_button);

        long organizationId = 0; // todo get this id
        if (MemberHelper.isMember(organizationId)) {
            join.setVisibility(View.GONE);
            join.setEnabled(false);
        }

        if (MemberHelper.hasPaid(organizationId)) {
            pay.setVisibility(View.VISIBLE);
            pay.setEnabled(true);
            pay.setOnClickListener(v -> {
                try {
                    PackageManager pm = getActivity().getPackageManager();
                    PackageInfo info = pm.getPackageInfo(, PackageManager.GET_ACTIVITIES);
                    if (versionCompare(info.versionName, ) >= 0) { // fixme What is this doing?
                        String uri = deeplinkURL; // todo get deep link from vipps some how
                        Intent intent = new Intent(Intent.ACTION_VIEW);
                        intent.setData(Uri.parse(uri));

                        if (pm.resolveActivity(intent, PackageManager.MATCH_DEFAULT_ONLY) != null) { // Guard. If there is no activity to fulfill this intent the app would crash if guard was not in place
                            startActivityForResult(intent, SUCCESS);
                        }
                    } else {
                        Toast.makeText(getActivity(), "You need to download the latest Vipps app", Toast.LENGTH_SHORT).show();
                    }
                } catch (PackageManager.NameNotFoundException e) {
                    String url = "https://play.google.com/store/apps/details?id=no.dnb.vipps";
                    Intent storeIntent = new Intent(Intent.ACTION_VIEW);
                    storeIntent.setData(Uri.parse(url));
                    startActivity(storeIntent);
                }
            });
        }

        return view;
    }


}
