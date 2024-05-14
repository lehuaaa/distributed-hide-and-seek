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
        double minDistanceFromBaseTop =  Math.min(
                    Math.sqrt((Math.pow(x - 4, 2) + Math.pow(y - 4, 2))),
                    Math.sqrt((Math.pow(x - 4, 2) + Math.pow(y - 5, 2))));

        double minDistanceFromBaseBottom =  Math.min(
                Math.sqrt((Math.pow(x - 5, 2) + Math.pow(y - 4, 2))),
                Math.sqrt((Math.pow(x - 5, 2) + Math.pow(y - 5, 2))));

        return Math.min(minDistanceFromBaseTop, minDistanceFromBaseBottom);
    }

    @Override
    public String toString() {
        return "(" + x + ", " + y + ").";
    }
}