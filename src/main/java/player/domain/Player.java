package player.domain;

import administration.server.beans.Node;
import administration.server.beans.Coordinate;
import administration.server.repositories.PlayersRepository;

import java.util.ArrayList;
import java.util.List;

public class Player extends Node {

    private static Player instance;
    private String serverAddress;
    private Coordinate coordinate;
    private List<OtherPlayer> otherPlayers;

    public Player() {
        super();
    }

    public static Player getInstance() {
        if(instance == null) {
            instance = new Player();
        }
        return instance;
    }

    public void init(Node node, String serverAddress, Coordinate coordinate, List<Node> otherPlayers) {
        this.id = node.getId();
        this.address = node.getAddress();
        this.port = node.getPort();
        this.serverAddress = serverAddress;
        this.coordinate = new Coordinate(coordinate.getX(), coordinate.getY());
        setOtherPlayerFromNodes(otherPlayers);
    }

    public String getServerAddress() {
        return serverAddress;
    }

    public Coordinate getCoordinate() {
        return coordinate;
    }

    private void setOtherPlayerFromNodes(List<Node> otherNodes) {
        if (otherNodes == null) {
            this.otherPlayers = new ArrayList<>();
        } else {
            this.otherPlayers = new ArrayList<>();
            for (Node n : otherNodes) {
                this.otherPlayers.add(new OtherPlayer(n));
            }
        }
    }

    public List<Node> getOtherPlayers() {
        return new ArrayList<>(otherPlayers);
    }
}