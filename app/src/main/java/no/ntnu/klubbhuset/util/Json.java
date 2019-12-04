package no.ntnu.klubbhuset.util;

import lombok.experimental.UtilityClass;

@UtilityClass
public class Json {
    private final com.google.gson.Gson gson = new com.google.gson.Gson();

    public <T> String toJson(T item) {
        return gson.toJson(item);
    }
    public <T> T fromJson(String json, Class<T> type) {
        return gson.fromJson(json.toString(), type);
    }
}
