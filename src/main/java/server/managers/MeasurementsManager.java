package server.managers;

import server.beans.PlayerMeasurement;

import java.util.*;
import java.util.stream.Collectors;

public class MeasurementsManager {

    private final HashMap<String, List<PlayerMeasurement>> measurementsById;
    private final List<PlayerMeasurement> measurements;

    private static MeasurementsManager instance;

    private MeasurementsManager() {
        measurementsById = new HashMap<>();
        measurements = new ArrayList<>();
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
        measurementsById.computeIfAbsent(measurement.getPlayerId(), k -> new ArrayList<>());
        List<PlayerMeasurement> playerMeasurements = measurementsById.get(measurement.getPlayerId());
        playerMeasurements.add(measurement);
    }

    /* Get the average of the last N measurements of a specific player */
    public synchronized double getAverageOfLastNMeasurementsById(String playerId, int n) {
        if (measurementsById.containsKey(playerId)) {
            List<PlayerMeasurement> measurements = measurementsById.get(playerId);
            return measurements.size() >= n
                    ? getAverageFromListOfMeasurement(measurements.subList(measurements.size() - n, measurements.size()))
                    : getAverageFromListOfMeasurement(measurements);
        }
        return -1;
    }

    /* Get the average of the measurements occurred between timestamp t1 and timestamp t2 */
    public synchronized double getAverageOfMeasurementsBetweenT1AndT2(long t1, long t2) {
        return getAverageFromListOfMeasurement(
                measurements.stream()
                        .filter(m -> m.getTimestamp() >= t1 && m.getTimestamp() <= t2)
                        .collect(Collectors.toList())
        );
    }

    /* Get the average from a list of measurements */
    private double getAverageFromListOfMeasurement(List<PlayerMeasurement> m) {
        return m.stream()
                .mapToDouble(PlayerMeasurement::getHrValue)
                .average()
                .orElse(0);
    }
}