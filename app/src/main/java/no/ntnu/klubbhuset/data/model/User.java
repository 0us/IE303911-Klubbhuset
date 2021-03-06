package no.ntnu.klubbhuset.data.model;

import java.io.Serializable;

import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class User implements Serializable {
    public transient static final String PHONENUMBER = "phonenumber";
    public transient static final String EMAIL = "email";
    public transient static final String LASTNAME = "lastName";
    public transient static final String FIRSTNAME = "firstName";
    private String firstName;
    private String lastName;
    private String email;
    private String phonenumber;
    private String password;
}
