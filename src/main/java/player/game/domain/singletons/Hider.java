package player.game.domain.singletons;

import player.game.domain.enums.GameState;
import player.game.handlers.BaseAccessHandler;
import player.game.handlers.InformationHandler;

import java.util.HashSet;
import java.util.LinkedList;
import java.util.Queue;
import java.util.Set;

public class Hider {

    private long timestampBaseRequest = Long.MAX_VALUE;

    private final Queue<String> waitingHiders;

    private final Set<String> confirmations;

    private double timePassedToReachBase;

    private int finishedHidersCount;

    private static Hider instance;

    private Hider() {
        waitingHiders = new LinkedList<>();
        confirmations = new HashSet<>();
    }

    public synchronized static Hider getInstance() {
        if (instance == null) {
            instance = new Hider();
        }
        return instance;
    }

    public long generateBaseRequest() {
        timestampBaseRequest = System.currentTimeMillis();
        return timestampBaseRequest;
    }

    public long getTimestampBaseRequest() { return timestampBaseRequest; }

    public synchronized boolean waitingHidersIsEmpty() { return waitingHiders.isEmpty(); }

    public synchronized String getFirstWaitingHider() { return waitingHiders.poll(); }

    public synchronized void storeWaitingHider(String message) { waitingHiders.add(message); }

    public synchronized int getConfirmationsCount() { return confirmations.size(); }

    public synchronized void addConfirmation(String hiderId) { confirmations.add(hiderId); }

    public int getFinishedHidersCount() { return finishedHidersCount; }

    public void increaseFinishedHiders() { finishedHidersCount++; }

    public double getTimePassedToReachBase() { return timePassedToReachBase; }

    public void setTimePassed(double timePassed) { this.timePassedToReachBase = Math.max(this.timePassedToReachBase, timePassed); }

    public void increaseTimePassed(double timePassed) { timePassedToReachBase += timePassed; }

    public void moveToTheBase() {

        double timeWaitedToGetBaseAccess = timePassedToReachBase;
        increaseTimePassed(Player.getInstance().getCoordinate().getDistanceFromBase() + 10);

        System.out.println("You obtain the access to the base after " + timeWaitedToGetBaseAccess +
                " seconds. You will be finished after " + Hider.getInstance().getTimePassedToReachBase() + " seconds");

        Player.getInstance().setState(GameState.FINISHED);

        InformationHandler.getInstance().informPlayersOfSaving(timeWaitedToGetBaseAccess);
        BaseAccessHandler.getInstance().sendBackConfirmationsToStoredHiders();
    }

}