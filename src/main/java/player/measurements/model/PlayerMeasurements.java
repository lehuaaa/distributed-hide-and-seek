package player.measurements.model;

import java.util.ArrayList;
import java.util.List;

public class PlayerMeasurements {

    private String playerId;
    private List<Double> hrValues;
    private long timestamp;

    public PlayerMeasurements() {}

    public PlayerMeasurements(String playerId, List<Double> hrValues, long timestamp) {
        this.playerId = playerId;
        this.hrValues = new ArrayList<>(hrValues);
        this.timestamp = timestamp;
    }

    public String getPlayerId() {
        return playerId;
    }

    public void setPlayerId(String playerId) {
        this.playerId = playerId;
    }

    public List<Double> getHrValues() {
        return new ArrayList<>(hrValues);
    }

    public void setHrValues(List<Double> hrValues) {
        this.hrValues = new ArrayList<>(hrValues);
    }

    public long getTimestamp() {
        return timestamp;
    }

    public void setTimestamp(long timestamp) {
        this.timestamp = timestamp;
    }
}
