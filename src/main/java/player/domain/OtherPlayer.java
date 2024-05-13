package player.domain;

import administration.server.beans.Coordinate;
import administration.server.beans.Node;

public class OtherPlayer extends Node {

    private Coordinate coordinate;

    public OtherPlayer(Node node) {
        super(node.getId(), node.getAddress(), node.getPort());
    }

    public Coordinate getCoordinate() {
        return new Coordinate(coordinate.getX(), coordinate.getY());
    }

    public void setCoordinate(Coordinate coordinate) {
        this.coordinate = new Coordinate(coordinate.getX(), coordinate.getY());
    }
}