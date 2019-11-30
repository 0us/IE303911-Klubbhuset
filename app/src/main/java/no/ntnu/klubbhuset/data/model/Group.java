package no.ntnu.klubbhuset.data.model;

import java.io.Serializable;

public class Group implements Serializable {

    public transient static final String USER = "user";
    public transient static final String ADMIN = "admin";

    private String name;

    public String getName() {
        return name;
    }
}
