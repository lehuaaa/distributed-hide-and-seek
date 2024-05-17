package player.domain;

import administration.server.beans.Node;
import administration.server.beans.Coordinate;
import player.domain.enums.Role;
import player.domain.enums.State;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class Player extends Participant {

    private final Map<String, Participant> participants = new HashMap<>();

    private String serverAddress;

    private State state;

    private Role role;

    private static Player instance;

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
        this.coordinate = coordinate;
        setParticipants(participants);
    }

    public State getState() { return state; }

    public Role getRole() { return role; }

    public synchronized List<Participant> getParticipants() {
        return new ArrayList<>(participants.values());
    }

    public synchronized int getParticipantsCount() {
        return participants.size();
    }

    public void setState(State state) { this.state = state; }

    public void setRole(Role role) { this.role = role; }

    private synchronized void setParticipants(List<Node> participants) {
        if (participants != null) {
            for (Node n : participants) {
                this.participants.put(n.getId(), new Participant(n));
            }
        }
    }

    public synchronized void storeNewParticipant(Participant participant) {
        participants.put(participant.getId(), participant);
    }

    public synchronized void setParticipantCoordinate(String participantId, Coordinate coordinate) {
        participants.get(participantId).setCoordinate(coordinate);
    }

    public synchronized Node getParticipant(String participantId) {
        return participants.get(participantId);
    }
}