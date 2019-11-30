package no.ntnu.klubbhuset.data.model;

import com.android.volley.toolbox.JsonObjectRequest;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.InvalidObjectException;

import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class User {
    public transient static final String PHONENUMBER = "phonenumber";
    public transient static final String EMAIL = "email";
    public transient static final String LASTNAME = "lastName";
    public transient static final String FIRSTNAME = "firstName";
    private String firstName;
    private String lastName;
    private String email;
    private String phone;
    private String password;
}
