package player.game.handlers;

import player.game.domain.singletons.Seeker;

public class SeekerHandler extends Thread {

    private static SeekerHandler instance;

    public static SeekerHandler getInstance() {
        if (instance == null){
            instance = new SeekerHandler();
        }
        return instance;
    }

    @Override
    public void run() {
        while (!Seeker.getInstance().isHidersEmpty()) {
            Double timeToReachHider = Seeker.getInstance().getNearestHiderDistance() * 1000;

            try {
                System.out.println("I'm waiting " + timeToReachHider.intValue() +" milliseconds");
                Thread.sleep(timeToReachHider.intValue());
            } catch (InterruptedException e) {
                throw new RuntimeException(e);
            }
        }
    }
}
