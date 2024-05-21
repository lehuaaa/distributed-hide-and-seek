package player.measurements.domain;

public class PlayerMeasurement {

    private final String playerId;
    private final double hrValue;
    private final long timestamp;

    public PlayerMeasurement(String playerId, double hrValue, long timestamp) {
        this.playerId = playerId;
        this.hrValue = hrValue;
        this.timestamp = timestamp;
    }

    public String getPlayerId() {
        return playerId;
    }

    public double getHrValue() { return hrValue; }

    public long getTimestamp() {
        return timestamp;
    }

    @Override
    public String toString() {
        return "measurement of player with id " + playerId + " with an heart rate value of " + hrValue + " and a timestamp of " + timestamp;
    }
}