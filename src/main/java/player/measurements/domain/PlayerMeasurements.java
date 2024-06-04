package player.measurements.domain;

import java.util.ArrayList;
import java.util.List;

public class PlayerMeasurements implements Comparable<PlayerMeasurements> {

    private String playerId;
    private List<Double> hrValues;
    private long timestamp;

    public PlayerMeasurements() {}

    public PlayerMeasurements(String playerId, List<Double> hrValues, long timestamp) {
        this.playerId = playerId;
        setHrValues(hrValues);
        this.timestamp = timestamp;
    }

    public String getPlayerId() {
        return playerId;
    }

    public List<Double> getHrValues() {
        return new ArrayList<>(hrValues);
    }

    public long getTimestamp() {
        return timestamp;
    }

    public void setPlayerId(String playerId) {
        this.playerId = playerId;
    }

    public void setHrValues(List<Double> hrValues) {
        if (hrValues == null) {
            this.hrValues = new ArrayList<>();
        } else {
            this.hrValues = new ArrayList<>(hrValues);
        }
    }

    @Override
    public int compareTo(PlayerMeasurements p) {
        Long thisTimestamp = timestamp;
        Long otherTimestamp = p.getTimestamp();
        return thisTimestamp.compareTo(otherTimestamp);
    }
}