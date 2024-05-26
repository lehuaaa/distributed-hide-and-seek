package player.game.domain.singletons;

import player.game.domain.enums.GameState;
import player.game.handlers.BaseAccessHandler;
import player.game.handlers.InformationHandler;

import java.text.DecimalFormat;
import java.util.HashSet;
import java.util.LinkedList;
import java.util.Queue;
import java.util.Set;

public class Hider extends Thread {

    private long timestampBaseRequest = Long.MAX_VALUE;

    private final Queue<String> waitingHiders;

    private final Set<String> confirmations;

    private double timePassedToReachBase;

    private final Set<String> finishedHidersCount;

    private static Hider instance;

    private Hider() {
        waitingHiders = new LinkedList<>();
        confirmations = new HashSet<>();
        finishedHidersCount = new HashSet<>();
    }

    public synchronized static Hider getInstance() {
        if (instance == null) {
            instance = new Hider();
        }
        return instance;
    }

    public void generateBaseRequest() { timestampBaseRequest = System.currentTimeMillis(); }

    public long getTimestampBaseRequest() { return timestampBaseRequest; }

    public synchronized boolean waitingHidersIsEmpty() { return waitingHiders.isEmpty(); }

    public synchronized String getFirstWaitingHider() { return waitingHiders.poll(); }

    public synchronized void storeWaitingHider(String message) { waitingHiders.add(message); }

    public synchronized int getConfirmationsCount() { return confirmations.size(); }

    public synchronized void addConfirmation(String hiderId) { confirmations.add(hiderId); }

    public synchronized int getFinishedHidersCount() { return finishedHidersCount.size(); }

    public synchronized void addFinishedHiders(String playerId) { finishedHidersCount.add(playerId); }

    public double getTimePassedToReachBase() { return timePassedToReachBase; }

    public void setTimePassed(double timePassed) { this.timePassedToReachBase = Math.max(this.timePassedToReachBase, timePassed); }

    @Override
    public void run() {
        reachBaseAndWait10Seconds();
    }

    private void reachBaseAndWait10Seconds() {

        double timeWaitedToGetBaseAccess = timePassedToReachBase;

        Double timeToReachBase = (Player.getInstance().getCoordinate().getDistanceFromBase() / 2) + 10;
        timePassedToReachBase += timeToReachBase;
        System.out.println("You obtain the access to the base after " + new DecimalFormat("0.00").format(timeWaitedToGetBaseAccess) + " seconds");

        try {
            timeToReachBase *= 1000;
            Thread.sleep(timeToReachBase.intValue());
        } catch (InterruptedException e) {
            throw new RuntimeException(e);
        }

        Player.getInstance().setState(GameState.FINISHED);
        InformationHandler.getInstance().informOtherPlayersObtainedAccess(timeWaitedToGetBaseAccess);
        BaseAccessHandler.getInstance().sendBackConfirmationsToStoredHiders();
    }

}