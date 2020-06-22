package rip.bolt.jsonrest;

import java.util.List;

import rip.bolt.jsonrest.json.JSONToObject;

public class Response {

    private int statusCode;
    private String data;

    public Response(int statusCode, String data) {
        this.statusCode = statusCode;
        this.data = data;
    }

    public <T> T as(Class<T> type) {
        return JSONToObject.perform(data, type);
    }

    public <T> List<T> asList(Class<T> type) {
        return JSONToObject.performList(data, type);
    }

    public int getStatusCode() {
        return statusCode;
    }

}
