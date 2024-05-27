package player.game.domain.singletons;

import java.util.HashSet;
import java.util.Set;

public class Election {

    private static Election instance;

    private final Set<String> votes;

    private Election() {
        votes = new HashSet<>();
    }

    public synchronized static Election getInstance() {
        if (instance == null) {
            instance = new Election();
        }
        return instance;
    }

    public synchronized int getVotesCount() {
        return votes.size();
    }

    public synchronized void addVote(String playerId) {
        votes.add(playerId);
    }
}
