package rip.bolt.jsonrest;

import java.io.IOException;
import java.net.HttpURLConnection;

public class Client {

    private String authorisationHeader;

    /**
     * Sets the authorisation header used for all requests from this client.
     * 
     * @param key the API key
     */
    public void setAuthorisationKey(String key) {
        if (key == null)
            this.authorisationHeader = null;
        else
            this.authorisationHeader = "Bearer " + key;
    }

    /**
     * Creates an endpoint with the specified url
     * 
     * @param url the url for the REST endpoint
     * @return the endpoint
     */
    public Endpoint endpoint(String url) {
        return new Endpoint(this, url);
    }

    protected HttpURLConnection constructRequest(Endpoint endpoint) {
        try {
            HttpURLConnection connection = (HttpURLConnection) endpoint.toURL().openConnection();
            if (authorisationHeader != null)
                connection.addRequestProperty("Authorization", authorisationHeader);

            connection.setUseCaches(false);
            connection.setDoInput(true);
            connection.setDoOutput(true);

            return connection;
        } catch (IOException e) {
            e.printStackTrace();
        }

        return null;
    }

}
