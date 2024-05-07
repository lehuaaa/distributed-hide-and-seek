package server.handlers;

import server.beans.PlayerMeasurement;

import java.util.*;
import java.util.stream.Collectors;

public class MeasurementsHandler {

    private final HashMap<String, List<PlayerMeasurement>> measurementsById;
    private final List<PlayerMeasurement> measurements;

    private static MeasurementsHandler instance;

    private MeasurementsHandler() {
        measurementsById = new HashMap<>();
        measurements = new ArrayList<>();
    }

    public synchronized static MeasurementsHandler getInstance() {
        if(instance == null) {
            instance = new MeasurementsHandler();
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
    public synchronized double getPlayerAverage(String playerId, int n) {
        if (measurementsById.containsKey(playerId)) {
            List<PlayerMeasurement> measurements = measurementsById.get(playerId);
            return measurements.size() >= n
                    ? computeAverage(measurements.subList(measurements.size() - n, measurements.size()))
                    : computeAverage(measurements);
        }
        return -1;
    }

    /* Get the average of the measurements occurred between timestamp t1 and timestamp t2 */
    public synchronized double getIntervalAverage(long t1, long t2) {
        return computeAverage(
                measurements.stream()
                        .filter(m -> m.getTimestamp() >= t1 && m.getTimestamp() <= t2)
                        .collect(Collectors.toList())
        );
    }

    /* Get the average from a list of measurements */
    private double computeAverage(List<PlayerMeasurement> m) {
        return m.stream()
                .mapToDouble(PlayerMeasurement::getHrValue)
                .average()
                .orElse(0);
    }
}