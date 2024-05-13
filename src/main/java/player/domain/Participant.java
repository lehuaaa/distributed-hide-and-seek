package player.domain;

import administration.server.beans.Coordinate;
import administration.server.beans.Node;

public class Participant extends Node {

    protected Coordinate coordinate;

    public Participant() { }

    public Participant(Node node) {
        super(node.getId(), node.getAddress(), node.getPort());
    }

    public Participant(String id, String address, int port, int x, int y) {
        super(id, address, port);
        coordinate = new Coordinate(x, y);
    }

    public Coordinate getCoordinate() {
        return new Coordinate(coordinate.getX(), coordinate.getY());
    }

    public void setCoordinate(Coordinate coordinate) {
        this.coordinate = new Coordinate(coordinate.getX(), coordinate.getY());
    }
}