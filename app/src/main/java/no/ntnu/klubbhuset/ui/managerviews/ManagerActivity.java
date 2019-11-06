package no.ntnu.klubbhuset.ui.managerviews;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;

import no.ntnu.klubbhuset.R;
import no.ntnu.klubbhuset.ui.managerviews.ui.manager.ManagerFragment;

public class
ManagerActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.manager_activity);
        if (savedInstanceState == null) {
            getSupportFragmentManager().beginTransaction()
                    .replace(R.id.container, ManagerFragment.newInstance())
                    .commitNow();
        }
    }
}
