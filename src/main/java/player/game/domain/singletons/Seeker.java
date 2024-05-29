package player.game.domain.singletons;

import administration.server.beans.Coordinate;
import administration.server.beans.Participant;

import java.text.DecimalFormat;
import java.util.HashMap;
import java.util.Map;
import java.util.stream.Collectors;

public class Seeker {

    private final Map<String, Participant> hiders;

    private final Map<String, Double> hidersTaggingTime;

    private int finishedHiders = 0;

    private boolean isSeeking = true;

    private double timePassed;

    private static Seeker instance;

    private Seeker() {
        hiders = Player.getInstance().getParticipants().stream().collect(Collectors.toMap(Participant::getId, participant -> participant));
        hidersTaggingTime = new HashMap<>();
    }

    public synchronized static Seeker getInstance() {
        if (instance == null) {
            instance = new Seeker();
        }
        return instance;
    }

    public int getFinishedHiders() {
        return finishedHiders;
    }

    public synchronized void incrementFinishedHiders() {
        finishedHiders++;
    }

    public boolean isSeeking() { return isSeeking; }

    public synchronized void setSeekingState(boolean state) { isSeeking = state; }

    public synchronized boolean isHidersEmpty() { return hiders.isEmpty(); }

    public synchronized void storeNewHider(Participant hider) {
        hiders.put(hider.getId(), hider);
    }

    public synchronized double getHiderTaggingTime(String hiderId) {
        if (!hidersTaggingTime.containsKey(hiderId))
            return -1;
        return hidersTaggingTime.get(hiderId);
    }

    public synchronized double getDistanceNearestHider() {

        double minDistance = Double.MAX_VALUE;
        Coordinate hiderCoordinate = new Coordinate();
        String hiderId = "";

        for (Participant p : hiders.values()) {
            double distance = Player.getInstance().getCoordinate().getDistanceFromPoint(p.getCoordinate());
            if (distance < minDistance) {
                minDistance = distance;
                hiderId = p.getId();
                hiderCoordinate = p.getCoordinate();
            }
        }

        double timeToReachHider = minDistance / 2;
        timePassed += timeToReachHider;
        hidersTaggingTime.put(hiderId, timePassed);

        System.out.println("You tag hider " + hiderId + " in " + new DecimalFormat("0.00").format(timePassed) + " seconds");

        Player.getInstance().setCoordinate(hiderCoordinate);
        hiders.remove(hiderId);
        return timeToReachHider;
    }
}