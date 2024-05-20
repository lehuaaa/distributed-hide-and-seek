package administration.server.beans;

import javax.xml.bind.annotation.XmlRootElement;

@XmlRootElement
public class Coordinate {

    private int x;
    private int y;

    public Coordinate() {}

    public Coordinate(int x, int y) {
        this.x = x;
        this.y = y;
    }

    public int getX() {
        return x;
    }

    public void setX(int x) {
        this.x = x;
    }

    public int getY() {
        return y;
    }

    public void setY(int y) {
        this.y = y;
    }

    public double getDistanceFromBase() {
        double minimumDistanceFromBaseTop = Math.min(getDistanceFromSecondPoint(new Coordinate(4, 4)),
                                                     getDistanceFromSecondPoint(new Coordinate(4, 5)));

        double minimumDistanceFromBaseBottom = Math.min(getDistanceFromSecondPoint(new Coordinate(5, 4)),
                                                        getDistanceFromSecondPoint(new Coordinate(5, 5)));

        return Math.min(minimumDistanceFromBaseTop, minimumDistanceFromBaseBottom);
    }

    public double getDistanceFromSecondPoint(Coordinate coordinate) {
        return Math.sqrt((Math.pow(x - coordinate.x, 2) + Math.pow(y - coordinate.y, 2)));
    }

    @Override
    public String toString() {
        return "(" + x + ", " + y + ").";
    }
}