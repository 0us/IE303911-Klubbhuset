package no.ntnu.klubbhuset.ui.managerviews;

import android.content.Intent;
import android.os.Bundle;
import android.widget.Button;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import no.ntnu.klubbhuset.R;
import no.ntnu.klubbhuset.adapter.MembersAdapter;
import no.ntnu.klubbhuset.data.Status;
import no.ntnu.klubbhuset.data.model.Club;
import no.ntnu.klubbhuset.ui.managerviews.barcode.BarcodeScannerActivity;
import no.ntnu.klubbhuset.viewmodels.ManagerViewModel;

public class ClubAdminActivity extends AppCompatActivity {

    private Button QRBtn;
    private RecyclerView recyclerView;
    private RecyclerView.Adapter adapter;
    private RecyclerView.LayoutManager layoutManager;
    private ManagerViewModel viewModel;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_club_admin);
        Club club = (Club) getIntent().getExtras().get("club");

        recyclerView = findViewById(R.id.membership_recycle_view);
        recyclerView.setHasFixedSize(true);
        viewModel = ViewModelProviders.of(this).get(ManagerViewModel.class);

        layoutManager = new LinearLayoutManager(this);
        recyclerView.setLayoutManager(layoutManager);

        viewModel.getAllMembersOfClub(club).observe(this, members -> {
            if(members.getStatus() == Status.SUCCESS) {
                adapter = new MembersAdapter(members.getData());
                recyclerView.setAdapter(adapter);
            }
        });

        SwipeRefreshLayout swipeRefreshLayout = findViewById(R.id.swipe_refresh_layout);
        swipeRefreshLayout.setOnRefreshListener(() -> {
            viewModel.refreshMembers(club).observe(this, resource -> {
                if (resource.getStatus() == Status.SUCCESS) {
                    swipeRefreshLayout.setRefreshing(false);
                } else if (resource.getStatus() == Status.ERROR) {
                    Toast.makeText(
                            this,
                            R.string.generic_error_response,
                            Toast.LENGTH_LONG).
                            show();
                    swipeRefreshLayout.setRefreshing(false);
                }
            });
        });

        Toolbar toolbar = findViewById(R.id.toolbar);
        toolbar.setTitle(club.getName());

        QRBtn = findViewById(R.id.club_admin_Qr_button);
        QRBtn.setOnClickListener(l -> {
            startBarcodeScanner(club);
        });
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
    }

    private void startBarcodeScanner(Club club) {
        Intent intent = new Intent(this, BarcodeScannerActivity.class);
        intent.putExtra("club", club);
        startActivity(intent);
    }

}
