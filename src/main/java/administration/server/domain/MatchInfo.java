package administration.server.domain;

import javax.xml.bind.annotation.XmlRootElement;
import java.util.ArrayList;
import java.util.List;

@XmlRootElement
public class MatchInfo {

    private Coordinate coordinate;
    private List<Client> otherPlayers;

    public MatchInfo() {}

    public MatchInfo(Coordinate coordinate, List<Client> otherPlayers) {
        this.coordinate = new Coordinate(coordinate.getX(), coordinate.getY());
        setOtherPlayers(otherPlayers);
    }

    public Coordinate getCoordinate() {
        return new Coordinate(coordinate.getX(), coordinate.getY());
    }

    public void setCoordinate(Coordinate coordinate) {
        this.coordinate = new Coordinate(coordinate.getX(), coordinate.getY());
    }

    public List<Client> getOtherPlayers() {
        if (otherPlayers == null) {
            return new ArrayList<>();
        }
        return new ArrayList<>(otherPlayers);
    }

    public void setOtherPlayers(List<Client> otherPlayers) {
        if (otherPlayers == null) {
            this.otherPlayers = new ArrayList<>();
        } else {
            this.otherPlayers = new ArrayList<>(otherPlayers);
        }
    }
}