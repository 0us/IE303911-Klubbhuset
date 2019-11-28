package no.ntnu.klubbhuset.data.model;

import com.android.volley.toolbox.JsonObjectRequest;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.InvalidObjectException;

import lombok.Data;

@Data
public class User {
    public static final String PHONENUMBER = "phonenumber";
    public static final String EMAIL = "email";
    public static final String LASTNAME = "lastName";
    public static final String FIRSTNAME = "firstName";
    private String firstName;
    private String lastName;
    private String email;
    private String phone;

    public User(JSONObject json) throws InvalidObjectException {
        if (!json.has(FIRSTNAME) ||
                !json.has(LASTNAME) ||
                !json.has(EMAIL) ||
                !json.has(PHONENUMBER)) { throw new InvalidObjectException("Lacking information");
        }

        try {
            firstName = json.get(FIRSTNAME).toString();
            lastName = json.get(LASTNAME).toString();
            email = json.get(EMAIL).toString();
            phone = json.get(PHONENUMBER).toString();
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }
}
