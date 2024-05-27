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

    private long timestampBaseAccessRequest = Long.MAX_VALUE;

    private final Queue<String> waitingHiders;

    private final Set<String> confirmations;

    private final Set<String> finishedHiders;

    private double timeWaitedToObtainBaseAccess;

    private double timePassedAfterReachingBase;

    private static Hider instance;

    private Hider() {
        waitingHiders = new LinkedList<>();
        confirmations = new HashSet<>();
        finishedHiders = new HashSet<>();
    }

    public synchronized static Hider getInstance() {
        if (instance == null) {
            instance = new Hider();
        }
        return instance;
    }

    public void generateTimestampBaseAccessRequest() { timestampBaseAccessRequest = System.currentTimeMillis(); }

    public long getTimestampBaseAccessRequest() { return timestampBaseAccessRequest; }

    public synchronized boolean waitingHidersIsEmpty() { return waitingHiders.isEmpty(); }

    public synchronized String getFirstWaitingHider() { return waitingHiders.poll(); }

    public synchronized void addWaitingHider(String message) { waitingHiders.add(message); }

    public synchronized int getConfirmationsCount() { return confirmations.size(); }

    public synchronized void addConfirmation(String hiderId) { confirmations.add(hiderId); }

    public synchronized int getFinishedHidersCount() { return finishedHiders.size(); }

    public synchronized void addFinishedHiders(String playerId) { finishedHiders.add(playerId); }

    public double getTimeWaitedToObtainBaseAccess() { return timeWaitedToObtainBaseAccess; }

    public synchronized void setTimeWaitedToObtainBaseAccess(double timePassed) { this.timeWaitedToObtainBaseAccess = Math.max(this.timeWaitedToObtainBaseAccess, timePassed); }

    public double getTimePassedAfterReachingBase() { return timePassedAfterReachingBase; }

    @Override
    public void run() { reachBaseAndWait10Seconds(); }

    private void reachBaseAndWait10Seconds() {

        double timeToReachBase = (Player.getInstance().getCoordinate().getDistanceFromBase() / 2) + 10;
        timePassedAfterReachingBase = timeWaitedToObtainBaseAccess + timeToReachBase;
        System.out.println("You obtain the access to the base after " + new DecimalFormat("0.00").format(timeWaitedToObtainBaseAccess) + " seconds");

        try {
            timeToReachBase *= 1000;
            Thread.sleep((int)timeToReachBase);
        } catch (InterruptedException e) {
            throw new RuntimeException(e);
        }

        Player.getInstance().setState(GameState.FINISHED);
        InformationHandler.getInstance().informPlayersOfTheObtainedAccess();
        BaseAccessHandler.getInstance().returnConfirmations();
    }
}