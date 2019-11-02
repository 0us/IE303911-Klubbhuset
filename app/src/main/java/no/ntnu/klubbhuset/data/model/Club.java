package no.ntnu.klubbhuset.data.model;

import org.json.JSONException;
import org.json.JSONObject;

import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class Club {
    private String description;
    private long priceOfMembership;
    private String emailContact;
    private String url;
    private long oid;
    private String name;

    public Club(JSONObject json) {
        try {
            this.oid = json.getLong("oid");
            this.name = json.getString("name");
            this.url = json.getString("url");
            this.emailContact = json.getString("emailContact");
            this.priceOfMembership = json.getLong("priceOfMembership");
            this.description = json.getString("description");
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }
}
