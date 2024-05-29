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
            try {
                Thread.sleep((int) (Seeker.getInstance().getDistanceNearestHider() * 1000));
            } catch (InterruptedException e) {
                throw new RuntimeException(e);
            }
        }
        Seeker.getInstance().setSeekingState(false);
    }
}