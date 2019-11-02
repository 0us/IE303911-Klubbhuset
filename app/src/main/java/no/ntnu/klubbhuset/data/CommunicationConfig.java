package no.ntnu.klubbhuset.data;

public class CommunicationConfig {

    public static final String API_URL = "http://10.22.193.222:8080/api/";
    public static final String GET_CLUBS_URL = "organization";
    public static final String ORGANIZATION = "organization/";
    public static final String JOIN = "join/";

    public static String joinClub(int id) {
        return API_URL + ORGANIZATION + id + "/" + JOIN;
    }

}
