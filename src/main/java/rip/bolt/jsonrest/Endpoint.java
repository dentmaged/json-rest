package rip.bolt.jsonrest;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.io.UnsupportedEncodingException;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLEncoder;

import rip.bolt.jsonrest.json.ObjectToJSON;

/**
 * Represents an REST API endpoint
 * 
 * @author dentmaged
 */
public class Endpoint {

    private Client client;
    private String url;

    protected Endpoint(Client client, String url) {
        this.client = client;
        this.url = url;
    }

    /**
     * Replaces all instance of {key} in the URL with the value
     *   
     * @param key The template to resolve
     * @param value The value to replace the template wiht
     * @return an endpoint with this template resolved
     */
    public Endpoint resolve(String key, Object value) {
        return new Endpoint(client, url.replaceAll("\\{" + key + "\\}", value.toString()));
    }

    /**
     * Only supported for GET requests.
     * 
     * @param key the query parameter key
     * @param value the query parameter value
     * @return an endpoint with this added parameter
     */
    public Endpoint parameter(String key, Object value) {
        String query;
        try {
            query = key + "=" + URLEncoder.encode(String.valueOf(value), "UTF-8");
        } catch (UnsupportedEncodingException e) {
            query = key + "=" + value;

            e.printStackTrace();
        }

        if (url.indexOf("?") < 0)
            return new Endpoint(client, url + "?" + query);

        return new Endpoint(client, url + "&" + query);
    }

    /**
     * Submit a GET request to this endpoint
     * 
     * @return the server's response
     */
    public Response get() {
        try {
            HttpURLConnection connection = client.constructRequest(this);
            connection.setRequestMethod("GET");

            return generateResponse(connection);
        } catch (Exception e) {
            e.printStackTrace();
        }

        return null;
    }

    /**
     * Submit a POST request to this endpoint
     * 
     * @param object the object that should be sent as part of this request
     * @return the server's response
     */
    public <T> Response post(T object) {
        try {
            HttpURLConnection connection = client.constructRequest(this);
            connection.setRequestMethod("POST");
            connection.setRequestProperty("Content-Type", "application/json; charset=UTF-8");
            connection.setRequestProperty("Accept", "application/json");

            OutputStream output = connection.getOutputStream();
            output.write(ObjectToJSON.perform(object).getBytes("UTF-8"));
            output.flush();
            output.close();

            return generateResponse(connection);
        } catch (Exception e) {
            e.printStackTrace();
        }

        return null;
    }

    private Response generateResponse(HttpURLConnection connection) throws IOException {
        InputStream in = connection.getInputStream();
        ByteArrayOutputStream baos = new ByteArrayOutputStream();

        byte[] bytes = new byte[256];
        int length;

        while ((length = in.read(bytes)) != -1)
            baos.write(bytes, 0, length);
        bytes = baos.toByteArray();

        return new Response(connection.getResponseCode(), new String(bytes, "UTF-8"));
    }

    /**
     * Converts this endpoint to a URL
     * 
     * @return the url representation of this endpoint
     */
    public URL toURL() {
        try {
            return new URL(url);
        } catch (MalformedURLException e) {
            e.printStackTrace();
        }

        return null;
    }

}
