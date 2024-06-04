package player.game.handlers;

import player.game.domain.enums.GameState;
import player.game.domain.singletons.Player;
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
        while (Player.getInstance().getState() != GameState.GAME_OVER) {
            try {
                Thread.sleep((int) (Seeker.getInstance().getDistanceNearestHider() * 1000));
            } catch (InterruptedException e) {
                throw new RuntimeException(e);
            }
        }
    }
}