package no.ntnu.klubbhuset.data.model;

import com.google.gson.Gson;
import com.google.gson.JsonObject;

import org.json.JSONException;
import org.json.JSONObject;

import no.ntnu.klubbhuset.data.CommunicationConfig;

public class VippsPaymentDetails {
    String mobileNummer;
    OrderId orderId;
    String merchantSerialNumber;
    String shippingDetails;
    double amount; // in NOK øre
    String vippsAuthToken;
    String transactionText;
    JsonObject details;



    public VippsPaymentDetails(String mobileNummer, OrderId orderId, double amount, String transactionText) {
        this.mobileNummer = mobileNummer;
        this.orderId = orderId;
        this.amount = amount;
        this.transactionText = transactionText;

        JsonObject customerInfo = new JsonObject();
        customerInfo.addProperty("mobileNumber", mobileNummer);

        JsonObject merchantInfo = new JsonObject();

        merchantInfo.addProperty("callbackPrefix", CommunicationConfig.API_URL); //fixme
        merchantInfo.addProperty("fallbackPrefix", "no.ntnu.klubbhuset."); //fixme
        merchantInfo.addProperty("isApp", true);
        merchantInfo.addProperty("merchantSerialNumber", merchantSerialNumber);

        JsonObject transaction = new JsonObject();
        transaction.addProperty("amount", amount);
        transaction.addProperty("transactionText", transactionText);

        details = new JsonObject();
        details.add("customerInfo", customerInfo);
        details.add("merchantInfo", merchantInfo);
        details.add("transaction", transaction);
    }

    private String createShippingDetails() {
        String address = "Digital";
        int shippingCost = 0;
        String shippingMethod = "Digital";
        String shippingMethodId = "DIGITAL";

        JsonObject result = new JsonObject();
        result.addProperty("address", address);
        result.addProperty("shippingCost", shippingCost);
        result.addProperty("shippingMethod", shippingMethod);
        result.addProperty("shippingMethodId", shippingMethodId);

        return new Gson().toJson(result);
    }

    public JSONObject getBody() throws JSONException {
        return new JSONObject(details.toString());
    }
}
