package no.ntnu.klubbhuset.ui.managerviews;

import android.content.Intent;
import android.os.Bundle;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import android.widget.Button;

import no.ntnu.klubbhuset.R;
import no.ntnu.klubbhuset.data.model.Club;
import no.ntnu.klubbhuset.ui.managerviews.barcode.BarcodeScannerActivity;

public class ClubAdminActivity extends AppCompatActivity {

    private Button QRBtn;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_club_admin);
        Club club = (Club) getIntent().getExtras().get("club");
        Toolbar toolbar = findViewById(R.id.toolbar);
        toolbar.setTitle(club.getName());


        QRBtn = findViewById(R.id.club_admin_Qr_button);
        QRBtn.setOnClickListener(l -> {
            Intent intent = new Intent(this, BarcodeScannerActivity.class);
            intent.putExtra("club", club);
            startActivity(intent);
        });
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
    }

}
