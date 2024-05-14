package player.domain;

import administration.server.beans.Node;
import administration.server.beans.Coordinate;

import java.util.ArrayList;
import java.util.List;

public class Player extends Participant {

    private static Player instance;

    private String serverAddress;

    private List<Participant> participants;

    private Node nextNode;

    public boolean hasParticipatedToElection;

    public boolean isSeeker;

    public boolean isInGame;


    public static Player getInstance() {
        if(instance == null) {
            instance = new Player();
        }
        return instance;
    }

    public void init(Node node, String serverAddress, Coordinate coordinate, List<Node> participants) {
        this.id = node.getId();
        this.address = node.getAddress();
        this.port = node.getPort();
        this.serverAddress = serverAddress;
        this.coordinate = new Coordinate(coordinate.getX(), coordinate.getY());
        setParticipants(participants);
    }

    public String getServerAddress() {
        return serverAddress;
    }

    public synchronized Node getNextNode() {
        return nextNode;
    }

    public synchronized List<Participant> getParticipants() {
        return new ArrayList<>(participants);
    }

    public synchronized void setNextNode(Node nextNode) {
        this.nextNode = new Node(nextNode.getId(), nextNode.getAddress(), nextNode.getPort());
    }

    private void setParticipants(List<Node> participants) {
        if (participants == null) {
            this.participants = new ArrayList<>();
        } else {
            this.participants = new ArrayList<>();
            for (Node n : participants) {
                this.participants.add(new Participant(n));
            }
        }
    }

    public synchronized void storeNewParticipant(String id, String address, int port, int x, int y) {
        participants.add(new Participant(id, address, port, x, y));
    }

    public synchronized void setParticipantCoordinate(int index, Coordinate coordinate) {
        participants.get(index).setCoordinate(coordinate);
    }
}