package player.game.domain.singletons;

import administration.server.beans.Node;
import administration.server.beans.Coordinate;
import administration.server.beans.Participant;
import player.game.domain.enums.Role;
import player.game.domain.enums.GameState;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class Player extends Participant {

    private final Map<String, Participant> participants = new HashMap<>();

    private String serverAddress;

    private GameState gameState = GameState.INIT;

    private Role role = Role.HIDER;

    private static Player instance;

    public synchronized static Player getInstance() {
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
        this.role = Role.HIDER;
        setCoordinate(coordinate);
        setParticipants(participants);
    }

    public GameState getState() { return gameState; }

    public Role getRole() { return role; }

    public String getServerAddress() { return serverAddress; }

    public synchronized List<Participant> getParticipants() {
        return new ArrayList<>(participants.values());
    }

    public synchronized int getParticipantsCount() {
        return participants.size();
    }

    public synchronized void setState(GameState gameState) { this.gameState = gameState; }

    public synchronized void setRole(Role role) { this.role = role; }

    private synchronized void setParticipants(List<Node> participants) {
        if (participants != null) {
            for (Node n : participants) {
                this.participants.put(n.getId(), new Participant(n.getId(), n.getAddress(), n.getPort(), new Coordinate()));
            }
        }
    }

    public synchronized void storeNewParticipant(Participant participant) {
        participants.put(participant.getId(), participant);
    }

    public synchronized void setParticipantCoordinate(String participantId, Coordinate coordinate) {
        participants.get(participantId).setCoordinate(coordinate);
    }

    public synchronized Participant getParticipant(String participantId) {
        return participants.get(participantId);
    }

    public synchronized boolean doesParticipantExist(String participantId) {
        return participants.containsKey(participantId);
    }
}