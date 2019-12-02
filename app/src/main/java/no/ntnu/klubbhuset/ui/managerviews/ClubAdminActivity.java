package no.ntnu.klubbhuset.ui.managerviews;

import android.content.Intent;
import android.os.Bundle;
import android.widget.Button;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import no.ntnu.klubbhuset.R;
import no.ntnu.klubbhuset.adapter.MembersAdapter;
import no.ntnu.klubbhuset.data.model.Club;
import no.ntnu.klubbhuset.ui.managerviews.barcode.BarcodeScannerActivity;

public class ClubAdminActivity extends AppCompatActivity {

    private Button QRBtn;
    private RecyclerView recyclerView;
    private RecyclerView.Adapter adapter;
    private RecyclerView.LayoutManager layoutManager;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_club_admin);
        recyclerView = findViewById(R.id.membership_recycle_view);
        recyclerView.setHasFixedSize(true);

        layoutManager = new LinearLayoutManager(this);
        recyclerView.setLayoutManager(layoutManager);

        adapter = new MembersAdapter();
        recyclerView.setAdapter(adapter);


        Club club = (Club) getIntent().getExtras().get("club");
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
