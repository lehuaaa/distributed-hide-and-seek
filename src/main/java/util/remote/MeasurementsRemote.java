package util.remote;

import com.sun.jersey.api.client.ClientHandlerException;
import player.measurements.model.PlayerMeasurements;
import com.google.gson.Gson;
import com.sun.jersey.api.client.Client;
import com.sun.jersey.api.client.ClientResponse;
import com.sun.jersey.api.client.WebResource;

public class MeasurementsRemote {

    private static MeasurementsRemote instance;
    private final Client client;
    private final Gson gson;

    private MeasurementsRemote() {
        client = Client.create();
        gson = new Gson();
    }

    public static MeasurementsRemote getInstance() {
        if (instance == null)
            instance = new MeasurementsRemote();
        return instance;
    }

    public ClientResponse requestAddMeasurements(String serverAddress, PlayerMeasurements measurements) {
        WebResource webResource = client.resource(serverAddress + "/measurements");
        String jsonMeasurements = gson.toJson(measurements);
        try {
            return webResource.type("application/json").post(ClientResponse.class, jsonMeasurements);
        } catch (ClientHandlerException e) {
         return null;
        }
    }
}
