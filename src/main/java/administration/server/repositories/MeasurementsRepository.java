package administration.server.repositories;

import administration.server.entities.Average;
import administration.server.entities.PlayerMeasurement;
import player.measurements.model.PlayerMeasurements;

import java.util.*;
import java.util.stream.Collectors;

public class MeasurementsRepository {

    private final List<PlayerMeasurement> measurements;

    private static MeasurementsRepository instance;

    private MeasurementsRepository() {
        measurements = new ArrayList<>();
    }

    public synchronized static MeasurementsRepository getInstance() {
        if(instance == null) {
            instance = new MeasurementsRepository();
        }
        return instance;
    }

    public synchronized List<PlayerMeasurement> getMeasurements() {
        if (!measurements.isEmpty()) {
            return new ArrayList<>(measurements);
        } else {
            return new ArrayList<>();
        }
    }

    /* Add player's measurements inside the list */
    public synchronized boolean addMeasurement(PlayerMeasurements playerMeasurements) {
        for (Double hrValue : playerMeasurements.getHrValues()) {
            PlayerMeasurement measurement = new PlayerMeasurement(playerMeasurements.getPlayerId(), hrValue, playerMeasurements.getTimestamp());
            measurements.add(measurement);
            System.out.println("Measurement : " + measurement + " successfully added to the general and player's list");
        }
        return true;
    }

    /* Get the average of the last N measurements of a specific player */
    public Average getPlayerAverage(String playerId, int n) {
        List<PlayerMeasurement> measurementsCopy = getMeasurements();
        double sum = 0;
        int count = 0;

        for (int i = measurementsCopy.size() - 1; i > -1; i--) {
                if (measurementsCopy.get(i).getPlayerId().equals(playerId)){
                    sum += measurementsCopy.get(i).getHrValue();
                    count++;
                    n--;
                }
                if (n == 0) break;
        }

        if (count == 0)
            return new Average(-1);

        double result = sum / count;
        System.out.println("Average of the last " + n + " measurements of player " + playerId + " has a result of: " + result);
        return new Average(result);
    }

    /* Get the average of the measurements occurred between timestamp t1 and timestamp t2 */
    public Average getIntervalAverage(long t1, long t2) {
        List<PlayerMeasurement> measurementsCopy = getMeasurements();

        double result = measurementsCopy.stream()
                        .filter(m -> m.getTimestamp() >= t1 && m.getTimestamp() <= t2)
                        .mapToDouble(PlayerMeasurement::getHrValue)
                        .average()
                        .orElse(0);

        System.out.println("Average of the measurements with timestamp between " + t1 + " and " + t2 + " has a result of: " + result);
        return new Average(result);
    }
}