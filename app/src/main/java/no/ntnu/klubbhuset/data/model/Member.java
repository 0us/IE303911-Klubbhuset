package no.ntnu.klubbhuset.data.model;

import org.json.JSONObject;

import java.util.Date;

public class Member {

    private boolean hasPaid;
    private boolean needsToPay;
    private Date created;
    private Group group;

    public Member(boolean hasPaid, boolean needsToPay, Date created) {
        this.hasPaid = hasPaid;
        this.needsToPay = needsToPay;
        this.created = created;
    }

    public Member(JSONObject json) {
    }

    public boolean isHasPaid() {
        return hasPaid;
    }

    public boolean isNeedsToPay() {
        return needsToPay;
    }

    public Date getCreated() {
        return created;
    }

    public Group getGroup() {
        return group;
    }
}
