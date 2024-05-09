package administration.server.repositories;

import administration.server.entities.PlayerMeasurement;
import player.measurements.model.PlayerMeasurements;

import java.util.*;
import java.util.stream.Collectors;

public class MeasurementsRepository {

    private final HashMap<String, List<PlayerMeasurement>> measurementsMap;
    private final List<PlayerMeasurement> measurementsList;

    private static MeasurementsRepository instance;

    private MeasurementsRepository() {
        measurementsMap = new HashMap<>();
        measurementsList = new ArrayList<>();
    }

    public synchronized static MeasurementsRepository getInstance() {
        if(instance == null) {
            instance = new MeasurementsRepository();
        }
        return instance;
    }

    /* Add playerId to the HashMap */
    public synchronized void addPlayerToMeasurementsList(String playerId) {
        measurementsMap.put(playerId, new ArrayList<>());
    }

    /* Add player's measurements inside his list and the general one */
    public synchronized boolean addMeasurement(PlayerMeasurements measurements) {
        if (measurementsMap.containsKey(measurements.getPlayerId())) {
            List<PlayerMeasurement> playerMeasurements = measurementsMap.get(measurements.getPlayerId());
            for (Double hrValue : measurements.getHrValues()) {
                PlayerMeasurement measurement = new PlayerMeasurement(measurements.getPlayerId(), hrValue, measurements.getTimestamp());
                playerMeasurements.add(measurement);
                measurementsList.add(measurement);
                System.out.println("Measurement : " + measurement + " successfully added to the general and player's list");
            }
            return true;
        }
        System.out.println("Measurements not added to the list because player with Id: " + measurements.getPlayerId() + " doesn't exist");
        return false;
    }

    /* Get the average of the last N measurements of a specific player */
    public synchronized double getPlayerAverage(String playerId, int n) {
        if (measurementsMap.containsKey(playerId)) {
            List<PlayerMeasurement> measurements = measurementsMap.get(playerId);
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
                measurementsList.stream()
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