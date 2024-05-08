package administration.server.handlers;

import administration.server.beans.PlayerMeasurement;

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

    /* Add player to the HashMap */
    public synchronized void addPlayerToMeasurementsList(String playerId) {
        measurementsById.put(playerId, new ArrayList<>());
    }

    /* Add player's measurement inside his list and the general one */
    public synchronized boolean addMeasurement(PlayerMeasurement measurement) {
        if (measurementsById.containsKey(measurement.getPlayerId())) {
            measurements.add(measurement);
            measurementsById.get(measurement.getPlayerId()).add(measurement);
            System.out.println("Measurement : " + measurement + " successfully added to the general list and to the player's list");
            return true;
        }
        System.out.println("Measurement: " + measurement + " not added to the list because player with Id: " + measurement.getPlayerId() + " doesn't exist");
        return false;
    }

    /* Get the average of the last N measurements of a specific player */
    public synchronized double getPlayerAverage(String playerId, int n) {
        if (measurementsById.containsKey(playerId)) {
            List<PlayerMeasurement> measurements = measurementsById.get(playerId);
            double average = measurements.size() >= n
                    ? computeAverage(measurements.subList(measurements.size() - n, measurements.size()))
                    : computeAverage(measurements);
            System.out.println("Average of the last " + n + " measurements of player " + playerId + " has a result of: " + average);
            return average;
        }
        System.out.println("Player with Id: " + playerId + " not found");
        return -1;
    }

    /* Get the average of the measurements occurred between timestamp t1 and timestamp t2 */
    public synchronized double getIntervalAverage(long t1, long t2) {
        double average = computeAverage(
                measurements.stream()
                        .filter(m -> m.getTimestamp() >= t1 && m.getTimestamp() <= t2)
                        .collect(Collectors.toList())
        );
        System.out.println("Average of the measurements with timestamp between " + t1 + " and " + t2 + " has a result of: " + average);
        return average;
    }

    /* Get the average from a list of measurements */
    private double computeAverage(List<PlayerMeasurement> m) {
        return m.stream()
                .mapToDouble(PlayerMeasurement::getHrValue)
                .average()
                .orElse(0);
    }
}