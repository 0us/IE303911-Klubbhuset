package no.ntnu.klubbhuset;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import no.ntnu.klubbhuset.data.CommunicationConfig;
import no.ntnu.klubbhuset.ui.login.LoginActivity;

public class MasterAcitvity extends AppCompatActivity {

    SharedPreferences pref;
    private final int LOGIN = 1;
    private final int HOME = 2;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        CommunicationConfig.getInstance(this.getApplicationContext());
        this.pref = getApplication().getSharedPreferences("login", Context.MODE_PRIVATE);
        boolean loggedin = pref.getBoolean("loggedin", false);

        Intent intent;
        if (loggedin) {
            showHome();
        } else {
            showLogin();
        }
    }

    private void showHome() {
        Intent intent = new Intent(this, MainActivity.class);
        startActivityForResult(intent, HOME);
    }

    private void showLogin() {
        Intent intent = new Intent(this, LoginActivity.class);
        startActivityForResult(intent, LOGIN);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        // Check which request we're responding to
        if (requestCode == LOGIN) {
            // Make sure the request was successful
            if (resultCode == LoginActivity.RESULT_OK) {
                showHome();
            }
        }

        if (requestCode == HOME) {
            if (data != null && data.getAction().equals("logout")) {
                SharedPreferences pref = getApplication().getSharedPreferences("login", Context.MODE_PRIVATE);
                SharedPreferences.Editor editor = pref.edit();
                editor.putBoolean("loggedin", false);
                editor.putString("token", "");
                editor.apply();
                showLogin();
            }
        }
    }
}
