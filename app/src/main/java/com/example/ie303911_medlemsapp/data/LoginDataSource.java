package com.example.ie303911_medlemsapp.data;

import com.example.ie303911_medlemsapp.data.model.LoggedInUser;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;

/**
 * Class that handles authentication w/ login credentials and retrieves user information.
 */
public class LoginDataSource {

    public Result<LoggedInUser> login(String username, String password) {
        HttpURLConnection c = null;
        try {
            // TODO: get a real server, maaaan
            URL url = new URL("http://158.38.101.86:8080/api/auth/login?uid=" + username + "&pwd=" + password);
            c = (HttpURLConnection) url.openConnection();
            c.setUseCaches(true);
            c.setRequestMethod("GET");

            if(c.getResponseCode() == HttpURLConnection.HTTP_OK) {
                BufferedReader br = new BufferedReader(new InputStreamReader(c.getInputStream(),"UTF-8"));
                String token = br.readLine();
                System.out.println(token);
                LoggedInUser user = new LoggedInUser(username,token);
                c.getInputStream().close(); // Why?
                return new Result.Success<>(user);
            }
            // TODO: handle loggedInUser authentication
            return new Result.Error(new IOException("Error logging in " + c.getResponseMessage()));
        } catch (Exception e) {
            System.err.println("Failed to call " + e);
            return new Result.Error(new IOException("Error logging in", e));
        } finally {
            if(c != null) c.disconnect();
        }
    }

    public void logout() {
        // TODO: revoke authentication
    }
}
