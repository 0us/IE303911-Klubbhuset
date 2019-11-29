package no.ntnu.klubbhuset.data.model;

import android.content.Context;

import com.google.gson.Gson;
import com.google.gson.JsonObject;

import org.json.JSONException;
import org.json.JSONObject;

import no.ntnu.klubbhuset.data.CommunicationConfig;
import no.ntnu.klubbhuset.util.PreferenceUtils;

import static no.ntnu.klubbhuset.data.model.VippsJsonProperties.*;

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
        customerInfo.addProperty(MOBILE_NUMBER_STRING, mobileNummer);

        JsonObject staticShippingDetails = new JsonObject();
        staticShippingDetails.addProperty(IS_DEFAULT, "Y");
        staticShippingDetails.addProperty(SHIPPING_COST, 0);
        staticShippingDetails.addProperty(SHIPPING_METHOD, DIGITAL);
        staticShippingDetails.addProperty(SHIPPING_METHOD_ID, DIGITAL);

        JsonObject merchantInfo = new JsonObject();
        merchantInfo.addProperty(CALLBACK_PREFIX_STRING, CommunicationConfig.API_URL); //fixme
        merchantInfo.addProperty(FALL_BACK_STRING, CLUB_DETAILED_ACTIVITY_PACKAGE); //fixme
        merchantInfo.addProperty(IS_APP, true);
        merchantInfo.addProperty(MERCHANT_SERIAL_NUMBER_STRING, merchantSerialNumber);
//        merchantInfo.add("staticShippingDetails", staticShippingDetails); // uncomment if shipping info is required

        JsonObject transaction = new JsonObject();
        transaction.addProperty(ORDER_ID_STRING, orderId.toString());
        transaction.addProperty(AMOUNT_STRING, Integer.valueOf((int) (amount * 100))); // The Vipps API does only accept amount as int, representing øre.
        transaction.addProperty(TRANSACTION_TEXT_STRING, transactionText);
        transaction.addProperty(SKIP_LANDING_PAGE_STRING, false);

        details = new JsonObject();
        details.add(CUSTOMER_INFO_STRING, customerInfo);
        details.add(MERCHANT_INFO_STRING, merchantInfo);
        details.add(TRANSACTION_STRING, transaction);
    }

    // todo use this
    private String createShippingDetails() {
        String address = DIGITAL;
        int shippingCost = 0;
        String shippingMethod = DIGITAL;
        String shippingMethodId = DIGITAL;

        JsonObject result = new JsonObject();
        result.addProperty(ADDRESS, address);
        result.addProperty(SHIPPING_COST, shippingCost);
        result.addProperty(SHIPPING_METHOD, shippingMethod);
        result.addProperty(SHIPPING_METHOD_ID, shippingMethodId);

        return new Gson().toJson(result);
    }

    public JSONObject getBody() throws JSONException {
        return new JSONObject(details.toString());
    }
}
