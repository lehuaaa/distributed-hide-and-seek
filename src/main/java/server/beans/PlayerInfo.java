package server.beans;

import javax.xml.bind.annotation.XmlRootElement;
import java.util.List;

@XmlRootElement
public class PlayerInfo {

    private Coordinate coordinate;
    private List<Player> players;

    public PlayerInfo() {}

    public PlayerInfo(Coordinate coordinate, List<Player> players) {
        this.coordinate = coordinate;
        this.players = players;
    }

    public Coordinate getCoordinate() {
        return coordinate;
    }

    public void setCoordinate(Coordinate coordinate) {
        this.coordinate = coordinate;
    }

    public List<Player> getPlayers() {
        return players;
    }

    public void setPlayers(List<Player> players) {
        this.players = players;
    }
}
