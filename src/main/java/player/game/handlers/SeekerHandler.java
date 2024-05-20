package player.game.handlers;

import player.domain.Participant;
import player.domain.Player;

import java.util.HashMap;
import java.util.Map;
import java.util.stream.Collectors;

public class SeekerHandler {

    private final Map<String, Participant> hiders;

    private final Map<String, Double> taggingTimeHiders;

    private int finishedHidersCount = 0;

    private double timePassed;

    private static SeekerHandler instance;

    private SeekerHandler() {
        hiders = Player.getInstance().getParticipants().stream().collect(Collectors.toMap(Participant::getId, participant -> participant));
        taggingTimeHiders = new HashMap<>();
    }

    public synchronized static SeekerHandler getInstance() {
        if (instance == null) {
            instance = new SeekerHandler();
        }
        return instance;
    }

    public int getFinishedHidersCount() {
        return finishedHidersCount;
    }

    public void incrementFinishedHidersCount() {
        finishedHidersCount++;
    }

    public void storeNewHider(Participant hider) {
        hiders.put(hider.getId(), hider);
    }

    public double checkTaggingTime(String hiderId) {
        if (!taggingTimeHiders.containsKey(hiderId)) {
            tagHiderById(hiderId);
        }
        return taggingTimeHiders.get(hiderId);
    }

    private void tagHiderById(String hiderId) {
        String nearestHiderId = getNearestHider();
        while (!hiderId.equals(nearestHiderId)) {
            nearestHiderId = getNearestHider();
        }
    }

    private String getNearestHider() {
        Participant hider = new Participant();
        double minDistance = Double.MAX_VALUE;

        for (Participant p : hiders.values()) {
            double distance = Player.getInstance().getCoordinate().getDistanceFromSecondPoint(p.getCoordinate());
            if (distance < minDistance) {
                minDistance = distance;
                hider = p;
            }
        }

        timePassed += minDistance;
        taggingTimeHiders.put(hider.getId(), timePassed);
        Player.getInstance().setCoordinate(hider.getCoordinate());
        hiders.remove(hider.getId());
        return hider.getId();
    }
}