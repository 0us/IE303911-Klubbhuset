package no.ntnu.klubbhuset.data.model;

import com.android.volley.toolbox.JsonObjectRequest;

import org.json.JSONObject;

import lombok.Data;

@Data
public class User {
    private String firstName;
    private String lastName;
    private String email;
    private String phone;

    public User(JSONObject json) {
        throw new UnsupportedOperationException("Not implemented");
    }
}
