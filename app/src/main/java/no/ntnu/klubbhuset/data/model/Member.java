package no.ntnu.klubbhuset.data.model;

import java.io.Serializable;
import java.util.Date;

import lombok.Data;

@Data
public class Member implements Serializable {

    private final User user;
    private boolean hasPaid;
    private boolean needsToPay;
    private Date created;
    private Group group;

    public Member(boolean hasPaid, boolean needsToPay, Date created, User user) {
        this.hasPaid = hasPaid;
        this.needsToPay = needsToPay;
        this.created = created;
        this.user = user;
    }

}
