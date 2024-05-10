package administration.server.repositories;

import administration.server.entities.Coordinate;
import administration.server.entities.MatchInfo;
import administration.server.entities.Client;

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
        if (!players.isEmpty()) {
            return new ArrayList<>(players);
        } else {
            return new ArrayList<>();
        }
    }

    public synchronized void setPlayers(List<Client> players) {
        if (players.isEmpty()) {
            this.players = new ArrayList<>(players);
        } else {
            this.players = new ArrayList<>();
        }
    }

    /* Add the new player to the list */
    public synchronized MatchInfo addPlayer(Client player) {
        if(!checkPlayerId(player.getId())) {
            System.out.println("Player with id: " + player.getId() + " already exists");
            return null;
        }

        Coordinate coordinate = CoordinatesRepository.getInstance().getPerimeterPosition();

        if (coordinate == null) {
            System.out.println("Player with Id: " + player.getId() + " cannot register to the match because it's already full");
            return null;
        }

        List<Client> playersCopy = getPlayers();
        players.add(player);
        System.out.println("Player: " + player + " successfully added to the list and obtained the position: " + coordinate);
        return new MatchInfo(coordinate, playersCopy);
    }

    /* Check if player's ID already exists */
    private boolean checkPlayerId(String playerId) {
        for (Client p : players)
            if (p.getId().equals(playerId))
                return false;
        return true;
    }
}