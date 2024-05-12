package player.domain;

import administration.server.domain.Client;
import administration.server.domain.Coordinate;

import java.util.ArrayList;
import java.util.List;

public class Player extends Client {

    private final String serverAddress;
    private final Coordinate coordinate;
    private List<Client> otherPlayers;

    public Player(Client client, String serverAddress, Coordinate coordinate, List<Client> otherPlayers) {
        super(client.getId(), client.getAddress(), client.getPort());
        this.serverAddress = serverAddress;
        this.coordinate = new Coordinate(coordinate.getX(), coordinate.getY());
        setOtherPlayers(otherPlayers);
    }

    public String getServerAddress() {
        return serverAddress;
    }

    public Coordinate getCoordinate() {
        return coordinate;
    }

    public void setOtherPlayers(List<Client> otherPlayers) {
        if (otherPlayers == null) {
            this.otherPlayers = new ArrayList<>();
        } else {
            this.otherPlayers = new ArrayList<>(otherPlayers);
        }
    }

    public List<Client> getOtherPlayers() {
        return new ArrayList<>(otherPlayers);
    }
}