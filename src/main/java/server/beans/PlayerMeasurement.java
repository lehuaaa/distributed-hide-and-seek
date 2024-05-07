package server.beans;

import javax.xml.bind.annotation.XmlRootElement;

@XmlRootElement
public class PlayerMeasurement implements Comparable<PlayerMeasurement> {

    private String playerId;
    private double hrValue;
    private long timestamp;

    public PlayerMeasurement() {}

    public PlayerMeasurement(String playerId, double hrValue, long timestamp) {
        this.playerId = playerId;
        this.hrValue = hrValue;
        this.timestamp = timestamp;
    }

    public String getPlayerId() {
        return playerId;
    }

    public void setPlayerId(String playerId) {
        this.playerId = playerId;
    }

    public double getHrValue() {
        return hrValue;
    }

    public void setHrValue(double hrValue) {
        this.hrValue = hrValue;
    }

    public long getTimestamp() {
        return timestamp;
    }

    public void setTimestamp(long timestamp) {
        this.timestamp = timestamp;
    }

    @Override
    public int compareTo(PlayerMeasurement m) {
        Long thisTimestamp = timestamp;
        Long otherTimestamp = m.getTimestamp();
        return thisTimestamp.compareTo(otherTimestamp);
    }

    @Override
    public String toString() {
        return "{ PlayerId: " + playerId + ", HeartRateValue: " + hrValue + ", TimeStamp: " + timestamp + "}";
    }
}
