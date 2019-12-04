package no.ntnu.klubbhuset.data;

import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class Resource<T> {
    Status status;
    T data;
    String error;

    public static <T> Resource success(T data) {
        return new Resource(Status.SUCCESS, data, null);
    }

    public static <T> Resource error(String message, T data) {
        return new Resource(Status.ERROR, data, message);
    }

    public static <T> Resource loading() {
        return new Resource(Status.LOADING, null, null);
    }
}
