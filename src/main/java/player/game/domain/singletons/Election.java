package player.game.domain.singletons;

import java.util.HashSet;
import java.util.Set;

public class Election {

    private static Election instance;

    private final Set<String> positiveVote;

    private Election() {
        positiveVote = new HashSet<>();
    }

    public synchronized static Election getInstance() {
        if (instance == null) {
            instance = new Election();
        }
        return instance;
    }

    public synchronized int getPositiveVoteCount() {
        return positiveVote.size();
    }

    public synchronized void addNewPositiveVote(String playerId) {
        positiveVote.add(playerId);
    }
}
