package administration.server.repositories;

import administration.server.beans.Coordinate;
import administration.server.beans.GameInfo;
import administration.server.beans.Node;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlRootElement;
import java.util.ArrayList;
import java.util.List;

@XmlRootElement
@XmlAccessorType(XmlAccessType.FIELD)
public class PlayersRepository {

    private List<Node> players;

    private static PlayersRepository instance;

    private PlayersRepository() { players = new ArrayList<>(); }

    public synchronized static PlayersRepository getInstance() {
        if(instance == null) {
            instance = new PlayersRepository();
        }
        return instance;
    }

    public synchronized List<Node> getPlayers() {
        if (players == null) {
            return new ArrayList<>();
        }
        return new ArrayList<>(players);
    }

    public synchronized void setPlayers(List<Node> players) {
        if (players == null) {
            this.players = new ArrayList<>();
        } else {
            this.players = new ArrayList<>(players);
        }
    }

    /* Add the new player to the list */
    public synchronized GameInfo addPlayer(Node player) {
        if (containsPlayer(player.getId())) {
            System.out.println("The id " + player.getId() + " already exists.");
            return null;
        }

        Coordinate coordinate = CoordinatesRepository.getInstance().getRandomPerimeterCoordinate();

        if (coordinate == null) {
            System.out.println("The player " + player.getId() + " cannot register to the match because it's already full.");
            return null;
        }

        List<Node> playersCopy = getPlayers();
        players.add(player);
        System.out.println("The player " + player.getId() + " obtained the position: " + coordinate);
        return new GameInfo(coordinate, playersCopy);
    }

    /* Check if player's ID already exists */
    public synchronized boolean containsPlayer(String playerId) {
        for (Node p : players)
            if (p.getId().equals(playerId))
                return true;
        return false;
    }
}