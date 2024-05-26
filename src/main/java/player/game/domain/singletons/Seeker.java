package player.game.domain.singletons;

import administration.server.beans.Coordinate;
import administration.server.beans.Participant;

import java.text.DecimalFormat;
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
            return -1;
        }
        return taggingTimeHiders.get(hiderId);
    }

    public synchronized boolean isHidersEmpty() {
        return hiders.isEmpty();
    }

    public synchronized double getNearestHiderDistance() {

        double minDistance = Double.MAX_VALUE;
        String hiderId = "";
        Coordinate hiderCoordinate = new Coordinate();

        for (Participant p : hiders.values()) {
            double distance = Player.getInstance().getCoordinate().getDistanceFromSecondPoint(p.getCoordinate());
            if (distance < minDistance) {
                minDistance = distance;
                hiderId = p.getId();
                hiderCoordinate = p.getCoordinate();
            }
        }

        minDistance /= 2;
        timePassed += minDistance;
        taggingTimeHiders.put(hiderId, timePassed);
        Player.getInstance().setCoordinate(hiderCoordinate);
        hiders.remove(hiderId);
        return minDistance;
    }

    public void ShowTaggingSummary() {
        System.out.println();
        System.out.println("Tagging summary:");

        for (String hiderId : taggingTimeHiders.keySet()) {
            System.out.println("Player " + hiderId + ": " + new DecimalFormat("0.00").format(taggingTimeHiders.get(hiderId)) + " seconds");
        }
    }
}
