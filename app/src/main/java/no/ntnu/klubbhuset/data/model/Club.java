package no.ntnu.klubbhuset.data.model;

import android.util.Base64;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.Serializable;
import java.math.BigDecimal;

import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class Club implements Serializable {
    private String description;
    private BigDecimal priceOfMembership;
    private String emailContact;
    private String url;
    private long oid;
    private String name;
    private Image[] orgImages;

    public Club(String description, BigDecimal priceOfMembership, String emailContact, String url, String name) {
        this.description = description;
        this.priceOfMembership = priceOfMembership;
        this.emailContact = emailContact;
        this.url = url;
        this.name = name;
    }

    public Club(JSONObject json) {
        try {
            this.oid = json.getLong("oid");
            this.name = json.getString("name");
            if (json.has("url")) {
                this.url = json.getString("url");
            }
            if (json.has("emailContact")) {
                this.emailContact = json.getString("emailContact");
            }
            if (json.getString("priceOfMembership").equals("null")) {
                this.priceOfMembership = BigDecimal.valueOf(0);
            } else {
                this.priceOfMembership = BigDecimal.valueOf(json.getDouble("priceOfMembership"));
            }
            this.description = json.getString("description");
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }

    public JSONObject toJson(byte[] imageInByte) {
        JSONObject json = new JSONObject();
      
        String base64String = null;
        if (imageInByte != null) {
            base64String = Base64.encodeToString(imageInByte, Base64.DEFAULT);
        }

        try {
            json.put("name", name);
            json.put("url", url);
            json.put("emailContact", emailContact);
            json.put("priceOfMembership", priceOfMembership);
            json.put("description", description);
            json.put("image", base64String);
        } catch (JSONException e) {
            e.printStackTrace();
        }
        return json;
    }

    public JSONObject toJson() {
        JSONObject json = new JSONObject();

        try {
            json.put("name", name);
            json.put("url", url);
            json.put("emailContact", emailContact);
            json.put("priceOfMembership", priceOfMembership);
            json.put("description", description);
        } catch (JSONException e) {
            e.printStackTrace();
        }
        return json;
    }

}
