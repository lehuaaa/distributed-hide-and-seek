package util.remote;

import administration.server.entities.Player;
import com.google.gson.Gson;
import com.sun.jersey.api.client.Client;
import com.sun.jersey.api.client.ClientHandlerException;
import com.sun.jersey.api.client.ClientResponse;
import com.sun.jersey.api.client.WebResource;

public class PlayersRemote {

    private static PlayersRemote instance;
    private final Client client;
    private final Gson gson;

    private PlayersRemote() {
        client = Client.create();
        gson = new Gson();
    }

    public static PlayersRemote getInstance() {
        if (instance == null)
            instance = new PlayersRemote();
        return instance;
    }

    public ClientResponse requestAddPlayer(String serverAddress, Player player) {
        WebResource webResource = client.resource(serverAddress + "/players");
        String jsonPlayer = gson.toJson(player);
        try {
            return webResource.type("application/json").post(ClientResponse.class, jsonPlayer);
        } catch (ClientHandlerException e) {
            System.out.println("Server not available");
            return null;
        }
    }

    public ClientResponse requestGetPlayers(String serverAddress) {
        WebResource webResource = client.resource(serverAddress + "/players");
        try {
            return webResource.get(ClientResponse.class);
        } catch (ClientHandlerException e) {
            System.out.println("Server not available");
            return null;
        }
    }
}
