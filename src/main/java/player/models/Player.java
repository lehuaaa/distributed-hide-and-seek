package player.models;

import administration.server.entities.Client;
import administration.server.entities.Coordinate;

import java.util.ArrayList;
import java.util.List;

public class Player extends Client {

    private String serverAddress;
    private Coordinate coordinate;
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

    public void setCoordinates(Coordinate coordinate) {
        this.coordinate.setX(coordinate.getX());
        this.coordinate.setY(coordinate.getY());
    }

    public Coordinate getCoordinate() {
        return coordinate;
    }

    public void setOtherPlayers(List<Client> otherPlayers) {
        if (otherPlayers != null) {
            this.otherPlayers = new ArrayList<>(otherPlayers);
        } else {
            this.otherPlayers = new ArrayList<>();
        }
    }

    public List<Client> getOtherPlayers() {
        if (otherPlayers != null) {
            return new ArrayList<>(otherPlayers);
        } else {
            return new ArrayList<>();
        }
    }
}