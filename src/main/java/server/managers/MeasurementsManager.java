package server.managers;

import server.beans.PlayerMeasurement;

import java.util.*;

public class MeasurementsManager {

    private final HashMap<String, List<PlayerMeasurement>> playersMeasurements;
    private final TreeSet<PlayerMeasurement> measurements;

    private static MeasurementsManager instance;

    private MeasurementsManager() {
        playersMeasurements = new HashMap<>();
        measurements = new TreeSet<>();
    }

    public synchronized static MeasurementsManager getInstance() {
        if(instance == null) {
            instance = new MeasurementsManager();
        }
        return instance;
    }

    /* Add player's measurement inside his list */
    public synchronized void addMeasurement(PlayerMeasurement measurement) {
        measurements.add(measurement);
        playersMeasurements.computeIfAbsent(measurement.getPlayerId(), k -> new ArrayList<>());
        List<PlayerMeasurement> playerMeasurements = playersMeasurements.get(measurement.getPlayerId());
        playerMeasurements.add(measurement);
    }

    /* Get the average of the last N measurements of a specific player */
    public synchronized double getAverageOfLastNMeasurementsById(String playerId, int n) {
        if (playersMeasurements.containsKey(playerId)) {
            List<PlayerMeasurement> measurements = playersMeasurements.get(playerId);
            return measurements.size() >= n
                    ? getAverageFromListOfMeasurement(measurements.subList(measurements.size() - n, measurements.size()))
                    : getAverageFromListOfMeasurement(measurements);
        }
        return -1;
    }

    /* Get the average of the measurements occurred between timestamp t1 and timestamp t2 */
    public synchronized double getAverageOfMeasurementsBetweenT1AndT2(long t1, long t2) {
        // TODO
        return 0;
    }

    /* Get the average from a list of measurements */
    private double getAverageFromListOfMeasurement(List<PlayerMeasurement> measurements) {
        return measurements.stream()
                .mapToDouble(PlayerMeasurement::getHrValue)
                .average()
                .orElse(0);
    }
}
