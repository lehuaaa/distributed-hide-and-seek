package server.beans;

import javax.xml.bind.annotation.XmlRootElement;
import java.util.List;

@XmlRootElement
public class InitialInfo {

    private Coordinate coordinate;
    private List<PlayerInfo> players;

    public InitialInfo() {}

    public InitialInfo(Coordinate coordinate, List<PlayerInfo> players) {
        this.coordinate = coordinate;
        this.players = players;
    }

    public Coordinate getCoordinate() {
        return coordinate;
    }

    public void setCoordinate(Coordinate coordinate) {
        this.coordinate = coordinate;
    }

    public List<PlayerInfo> getPlayers() {
        return players;
    }

    public void setPlayers(List<PlayerInfo> players) {
        this.players = players;
    }
}
