package no.ntnu.klubbhuset.util;

import android.content.SharedPreferences;

import java.util.logging.Handler;

import no.ntnu.klubbhuset.ui.main.MainActivity;

public class MemberHelper {
    static SharedPreferences preferences = MainActivity.this.getSharedPreferences("login");
    static SharedPreferences.Editor editor = preferences.edit();


    public static boolean isMember(long organizationId){
        String token = preferences.getString("token", null);
        if (token == null) {
            return false;
        }
        // todo send token to server to verify that user is member

        return true;
    }

    // todo implement this
    public static boolean hasPaid(long organizationId) {
        return false;
    }
}
