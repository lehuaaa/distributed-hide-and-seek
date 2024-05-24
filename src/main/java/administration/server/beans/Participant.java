package administration.server.beans;

public class Participant extends Node {

    private Coordinate coordinate;

    public Participant() { }

    public Participant(String id, String address, int port, Coordinate coordinate) {
        super(id, address, port);
        this.coordinate = coordinate;
    }

    public Coordinate getCoordinate() {
        return new Coordinate(coordinate.getX(), coordinate.getY());
    }

    public void setCoordinate(Coordinate coordinate) {
        this.coordinate = new Coordinate(coordinate.getX(), coordinate.getY());
    }
}