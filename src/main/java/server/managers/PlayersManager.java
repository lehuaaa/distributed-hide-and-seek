package server.managers;

import server.beans.InitialInfo;
import server.beans.PlayerInfo;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlRootElement;
import java.util.ArrayList;
import java.util.List;

@XmlRootElement
@XmlAccessorType(XmlAccessType.FIELD)
public class PlayersManager {

    private List<PlayerInfo> players;

    private static PlayersManager instance;

    private PlayersManager() { players = new ArrayList<>(); }

    public synchronized static PlayersManager getInstance() {
        if(instance == null) {
            instance = new PlayersManager();
        }
        return instance;
    }

    public synchronized List<PlayerInfo> getPlayers() {
        return new ArrayList<>(players);
    }

    public synchronized void setPlayers(List<PlayerInfo> players) {
        this.players = players;
    }

    /* Check if player's ID already exists, If not the player will be added to the list of players */
    public synchronized InitialInfo addPlayer(PlayerInfo player) {
        List<PlayerInfo> playersCopy = getPlayers();
        for (PlayerInfo p : playersCopy)
            if (p.getId().equals(player.getId()))
                return null;
        players.add(player);
        return new InitialInfo(CoordinatesManager.getInstance().getFreePosition(), playersCopy);
    }
}