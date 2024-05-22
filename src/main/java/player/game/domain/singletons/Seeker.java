package player.game.domain.singletons;

import administration.server.beans.Participant;

import java.util.HashMap;
import java.util.Map;
import java.util.stream.Collectors;

public class Seeker {

    private final Map<String, Participant> hiders;

    private final Map<String, Double> taggingTimeHiders;

    private int finishedHidersCount = 0;

    private double timePassed;

    private static Seeker instance;

    private Seeker() {
        hiders = Player.getInstance().getParticipants().stream().collect(Collectors.toMap(Participant::getId, participant -> participant));
        taggingTimeHiders = new HashMap<>();
    }

    public synchronized static Seeker getInstance() {
        if (instance == null) {
            instance = new Seeker();
        }
        return instance;
    }

    public int getFinishedHidersCount() {
        return finishedHidersCount;
    }

    public void incrementFinishedHidersCount() {
        finishedHidersCount++;
    }

    public synchronized void storeNewHider(Participant hider) {
        hiders.put(hider.getId(), hider);
    }

    public synchronized double checkTaggingTime(String hiderId) {
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

        /* Player move 2 meters per second */
        timePassed += minDistance / 2;
        taggingTimeHiders.put(hider.getId(), timePassed);
        Player.getInstance().setCoordinate(hider.getCoordinate());
        hiders.remove(hider.getId());
        return hider.getId();
    }
}
