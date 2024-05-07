package server.handlers;

import server.beans.InitialPlayerInfo;
import server.beans.Player;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlRootElement;
import java.util.ArrayList;
import java.util.List;

@XmlRootElement
@XmlAccessorType(XmlAccessType.FIELD)
public class PlayersHandler {

    private List<Player> players;

    private static PlayersHandler instance;

    private PlayersHandler() { players = new ArrayList<>(); }

    public synchronized static PlayersHandler getInstance() {
        if(instance == null) {
            instance = new PlayersHandler();
        }
        return instance;
    }

    public synchronized List<Player> getPlayers() {
        return new ArrayList<>(players);
    }

    public synchronized void setPlayers(List<Player> players) {
        this.players = players;
    }

    /* Check if player's ID already exists, If not the player will be added to the list of players */
    public synchronized InitialPlayerInfo addPlayer(Player player) {
        List<Player> playersCopy = getPlayers();
        for (Player p : playersCopy)
            if (p.getId().equals(player.getId())){
                System.out.println("PlayerId " + player.getId() + " already exists");
                return null;
            }
        players.add(player);
        MeasurementsHandler.getInstance().addPlayerToMeasurementsList(player.getId());
        System.out.println("Player: " + player.toString() + " successfully added to the list");
        return new InitialPlayerInfo(CoordinatesHandler.getInstance().getRandomPosition(), playersCopy);
    }
}