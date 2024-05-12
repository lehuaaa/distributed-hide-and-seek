package player.domain;

import administration.server.beans.Node;
import administration.server.beans.Coordinate;

import java.util.ArrayList;
import java.util.List;

public class Player extends Node {

    private final String serverAddress;
    private final Coordinate coordinate;
    private List<Node> otherPlayers;

    public Player(Node node, String serverAddress, Coordinate coordinate, List<Node> otherPlayers) {
        super(node.getId(), node.getAddress(), node.getPort());
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

    public void setOtherPlayers(List<Node> otherPlayers) {
        if (otherPlayers == null) {
            this.otherPlayers = new ArrayList<>();
        } else {
            this.otherPlayers = new ArrayList<>(otherPlayers);
        }
    }

    public List<Node> getOtherPlayers() {
        return new ArrayList<>(otherPlayers);
    }
}