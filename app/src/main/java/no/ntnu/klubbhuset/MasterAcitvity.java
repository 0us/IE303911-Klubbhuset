package no.ntnu.klubbhuset;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.util.Log;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import no.ntnu.klubbhuset.util.CommunicationConfig;
import no.ntnu.klubbhuset.ui.login.LoginActivity;

import static no.ntnu.klubbhuset.ui.main.MainActivity.FINISHED;
import static no.ntnu.klubbhuset.ui.main.MainActivity.LOGOUT;

public class MasterAcitvity extends AppCompatActivity {

    private static final String TAG = "MasterActivity";
    SharedPreferences pref;

    /**
     * Activity Result Codes
     */
    private final int LOGIN = 1;
    private final int HOME = 2;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        CommunicationConfig.getInstance(this.getApplicationContext());
        this.pref = getApplication().getSharedPreferences("login", Context.MODE_PRIVATE);
        boolean loggedin = pref.getBoolean("loggedin", false);

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

        // Who's activity finished
        switch (requestCode) {

            case HOME:

                if (resultCode == LOGOUT) {
                    SharedPreferences pref = getApplication().getSharedPreferences("login", Context.MODE_PRIVATE);
                    SharedPreferences.Editor editor = pref.edit();
                    editor.putBoolean("loggedin", false);
                    editor.putString("token", "");
                    editor.apply();
                    showLogin();

                } else if (resultCode == FINISHED) {
                    finish();
                }
                break;

            case LOGIN:

                if (resultCode == Activity.RESULT_OK) {
                    showHome();
                } else {
                    Log.e(TAG, "Something went wrong with login activity result");
                }
                break;
        }
    }
}
