package no.ntnu.klubbhuset.data.model;

import android.content.Context;

import com.google.gson.Gson;
import com.google.gson.JsonObject;

import org.json.JSONException;
import org.json.JSONObject;

import no.ntnu.klubbhuset.data.CommunicationConfig;
import no.ntnu.klubbhuset.util.PreferenceUtils;

public class VippsPaymentDetails {
    String mobileNummer;
    OrderId orderId;
    String merchantSerialNumber;
    String shippingDetails;
    double amount; // in NOK øre
    String vippsAuthToken;
    String transactionText;
    JsonObject details;

    public VippsPaymentDetails(String mobileNummer, OrderId orderId, double amount, String transactionText, Context context) {
        this.mobileNummer = mobileNummer;
        this.orderId = orderId;
        this.amount = amount;
        this.transactionText = transactionText;
        merchantSerialNumber = CommunicationConfig.getInstance().getMerchantSerialNumber();
        vippsAuthToken = PreferenceUtils.getVippsAccessToken(context);


        JsonObject customerInfo = new JsonObject();
        customerInfo.addProperty("mobileNumber", mobileNummer);

        JsonObject staticShippingDetails = new JsonObject();
        staticShippingDetails.addProperty("isDefault", "Y");
        staticShippingDetails.addProperty("shippingCost", 0);
        staticShippingDetails.addProperty("shippingMethod", "digital");
        staticShippingDetails.addProperty("shippingMethodId", "DIG");

        JsonObject merchantInfo = new JsonObject();
        merchantInfo.addProperty("callbackPrefix", CommunicationConfig.API_URL); //fixme
        merchantInfo.addProperty("fallBack", "no.ntnu.klubbhuset.ui.userviews.club.detailed.ClubDetailedActivity"); //fixme
        merchantInfo.addProperty("isApp", true);
        merchantInfo.addProperty("merchantSerialNumber", merchantSerialNumber);
//        merchantInfo.add("staticShippingDetails", staticShippingDetails); // uncomment if shipping info is required

        JsonObject transaction = new JsonObject();
        transaction.addProperty("orderId", orderId.toString());
        transaction.addProperty("amount", Integer.valueOf((int) (amount * 100))); // The Vipps API does only accept amount as int, representing øre.
        transaction.addProperty("transactionText", transactionText);
        transaction.addProperty("skipLandingPage", false);

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
