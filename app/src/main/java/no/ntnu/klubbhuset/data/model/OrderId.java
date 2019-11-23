package no.ntnu.klubbhuset.data.model;

import java.text.SimpleDateFormat;
import java.util.Date;

public class OrderId {
    String organizationId;
    String userId;
    String now;

    SimpleDateFormat formatter = new SimpleDateFormat("dd-MM-yyyy HH:mm:ss");

    public OrderId(String organizationId, String userId) {
        this.organizationId = organizationId;
        this.userId = userId;
        this.now = formatter.format(new Date());
    }

    @Override
    public String toString() {
        return userId + "::" + organizationId + "::" + now;
    }
}
