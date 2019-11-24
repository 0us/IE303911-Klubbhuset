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
    public static final String PHONENUMBER = "phonenumber";
    public static final String EMAIL = "email";
    public static final String LASTNAME = "lastName";
    public static final String FIRSTNAME = "firstName";
    private String firstName;
    private String lastName;
    private String email;
    private String phone;
    private String password;
}
