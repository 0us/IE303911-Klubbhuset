package no.ntnu.klubbhuset.data.model;

import org.json.JSONObject;

import java.io.Serializable;
import java.util.Date;

import lombok.Data;

@Data
public class Member implements Serializable {

    private boolean hasPaid;
    private boolean needsToPay;
    private Date created;
    private Group group;

    public Member(boolean hasPaid, boolean needsToPay, Date created) {
        this.hasPaid = hasPaid;
        this.needsToPay = needsToPay;
        this.created = created;
    }

}
