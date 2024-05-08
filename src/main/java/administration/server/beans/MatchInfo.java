package administration.server.beans;

import javax.xml.bind.annotation.XmlRootElement;
import java.util.List;

@XmlRootElement
public class MatchInfo {

    private Coordinate coordinate;
    private List<Player> players;

    public MatchInfo() {}

    public MatchInfo(Coordinate coordinate, List<Player> players) {
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
