package no.ntnu.klubbhuset.data;

public class CommunicationConfig {

    public static final String API_URL = "http://todo.klubbhuset.no/api/";
    public static final String ORGANIZATION = "organization/";
    public static final String JOIN = "join/";

    public static String joinClub(int id) {
        return API_URL + ORGANIZATION + id + "/" + JOIN;
    }
}
