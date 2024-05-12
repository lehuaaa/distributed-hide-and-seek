package player.domain;

import java.util.ArrayList;
import java.util.List;

public class PlayerMeasurements {

    private final String playerId;
    private List<Double> hrValues;
    private final long timestamp;

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

    public void setHrValues(List<Double> hrValues) {
        if (hrValues == null) {
            this.hrValues = new ArrayList<>();
        } else {
            this.hrValues = new ArrayList<>(hrValues);
        }
    }

    public long getTimestamp() {
        return timestamp;
    }
}