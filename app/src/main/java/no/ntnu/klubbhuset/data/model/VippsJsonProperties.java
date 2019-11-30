package no.ntnu.klubbhuset.data.model;

/**
 * Strings used in application
 */
public class VippsJsonProperties {

    /**
     * Used for accessing Vipps API
     */
    public static final String CLIENT_ID_STRING = "client_id";
    public static final String CLIENT_SECRET_STRING = "client_secret";
    public static final String OCP_APIM_SUBSCRIPTION_KEY_STRING = "Ocp-Apim-Subscription-Key";

    /**
     * Used for mapping response body from Vipps API
     */
    public static final String TOKEN_TYPE = "token_type";
    public static final String EXPIRES_IN = "expires_in";
    public static final String EXT_EXPIRES_IN = "ext_expires_in";
    public static final String EXT_EXPIRES_ON = "expires_on";
    public static final String NOT_BEFORE = "not_before";
    public static final String RESOURCE = "resource";
    public static final String ACCESS_TOKEN = "access_token";

    public static final String VIPPS_API_URL = "https://apitest.vipps.no";
    public static final String AUTHORIZATION = "Authorization";
    public static final String APPLICATION_JSON = "application/json";
    public static final String CONTENT_TYPE = "Content-Type";
    public static final String VIPPS_STRING = "vipps";
    public static final String merchantSerialNumber = "";
    // TEMPORARY VALUES SO I CAN COMPILE
    public static final String callbackPrefix = ""; // todo
    public static final String fallBack = ""; // todo
    public static final String MERCHANT_SERIAL_NUMBER_STRING = "merchantSerialNumber";
    public static final String TRANSACTION_TEXT_STRING = "transactionText";
    public static final String SKIP_LANDING_PAGE_STRING = "skipLandingPage";
    public static final String MERCHANT_INFO_STRING = "merchantInfo";
    public static final String FALL_BACK_STRING = "fallBack";
    public static final String AMOUNT_STRING = "amount";
    public static final String CALLBACK_PREFIX_STRING = "callbackPrefix";
    public static final String AUTH_TOKEN = "authToken";
    public static final String IS_APP = "isApp";
    public static final String MOBILE_NUMBER_STRING = "mobileNumber";
    public static final String CUSTOMER_INFO_STRING = "customerInfo";
    public static final String TRANSACTION_STRING = "transaction";
    public static final String ECOMM_V_2_PAYMENTS = "/ecomm/v2/payments/";
    public static final String CAPTURE = "/capture";
    public static final String DETAILS = "/details";
    public static final String IS_DEFAULT = "isDefault";
    public static final String ORDER_ID_STRING = "orderId";
    public static final String DIGITAL = "Digital";
    public static final String ADDRESS = "address";
    public static final String SHIPPING_COST = "shippingCost";
    public static final String SHIPPING_METHOD = "shippingMethod";
    public static final String SHIPPING_METHOD_ID = "shippingMethodId";
    public static final String CLUB_DETAILED_ACTIVITY_PACKAGE = "no.ntnu.klubbhuset.ui.userviews.club.detailed.ClubDetailedActivity";
    //    public static final String MERCHANT_SERIAL_NUMBER = "merchantSerialNumber";
    public static final String VIPPS_URL = "https://apitest.vipps.no";
    public static final String MINIMUM_REQUIRED_VIPPS_VERSION = "1.8.0";
    public static final String UTF_8 = "UTF-8";
    public static final String NO_DNB_VIPPS_PACKAGE = "no.dnb.vipps";
    public static final String BEARER = "Bearer ";
}
