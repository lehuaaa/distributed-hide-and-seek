package administration.server.repositories;

import administration.server.entities.Coordinate;
import administration.server.entities.MatchInfo;
import administration.server.entities.Player;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlRootElement;
import java.util.ArrayList;
import java.util.List;

@XmlRootElement
@XmlAccessorType(XmlAccessType.FIELD)
public class PlayersRepository {

    private List<Player> players;

    private static PlayersRepository instance;

    private PlayersRepository() { players = new ArrayList<>(); }

    public synchronized static PlayersRepository getInstance() {
        if(instance == null) {
            instance = new PlayersRepository();
        }
        return instance;
    }

    public synchronized List<Player> getPlayers() {
        return new ArrayList<>(players);
    }

    public synchronized void setPlayers(List<Player> players) {
        this.players = players;
    }

    /* Add the new player to the list */
    public synchronized MatchInfo addPlayer(Player player) {
        List<Player> playersCopy = getPlayers();

        if(!checkPlayerId(playersCopy, player.getId())) {
            System.out.println("PlayerId " + player.getId() + " already exists");
            return null;
        }

        Coordinate coordinate = CoordinatesRepository.getInstance().getPerimeterPosition();

        if (coordinate == null) {
            System.out.println("Player with Id: " + player.getId() + " cannot register to the match because it's already full");
            return null;
        }

        players.add(player);
        MeasurementsRepository.getInstance().addPlayerToMeasurementsList(player.getId());
        System.out.println("Player: " + player + " successfully added to the list and obtained the position: " + coordinate);
        return new MatchInfo(coordinate, playersCopy);
    }

    /* Check if player's ID already exists */
    private boolean checkPlayerId(List<Player> players, String playerId) {
        for (Player p : players)
            if (p.getId().equals(playerId))
                return false;
        return true;
    }
}