package administration.server.repositories;

import administration.server.domain.Coordinate;
import administration.server.domain.MatchInfo;
import administration.server.domain.Client;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlRootElement;
import java.util.ArrayList;
import java.util.List;

@XmlRootElement
@XmlAccessorType(XmlAccessType.FIELD)
public class PlayersRepository {

    private List<Client> players;

    private static PlayersRepository instance;

    private PlayersRepository() { players = new ArrayList<>(); }

    public synchronized static PlayersRepository getInstance() {
        if(instance == null) {
            instance = new PlayersRepository();
        }
        return instance;
    }

    public synchronized List<Client> getPlayers() {
        if (players == null) {
            return new ArrayList<>();
        }
        return new ArrayList<>(players);
    }

    public synchronized void setPlayers(List<Client> players) {
        if (players == null) {
            this.players = new ArrayList<>();
        } else {
            this.players = new ArrayList<>(players);
        }
    }

    /* Add the new player to the list */
    public synchronized MatchInfo addPlayer(Client player) {
        if(containsPlayer(player.getId())) {
            System.out.println("The player with id " + player.getId() + " already exists.");
            return null;
        }

        Coordinate coordinate = CoordinatesRepository.getInstance().getPerimeterPosition();

        if (coordinate == null) {
            System.out.println("The player with id " + player.getId() + " cannot register to the match because it's already full.");
            return null;
        }

        List<Client> playersCopy = getPlayers();
        players.add(player);
        System.out.println("The player with id " + player.getId() + " obtained the position: " + coordinate + ".");
        return new MatchInfo(coordinate, playersCopy);
    }

    /* Check if player's ID already exists */
    public synchronized boolean containsPlayer(String playerId) {
        for (Client p : players)
            if (p.getId().equals(playerId))
                return true;
        return false;
    }
}