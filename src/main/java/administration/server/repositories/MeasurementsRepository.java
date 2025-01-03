package administration.server.repositories;

import administration.server.beans.Average;
import player.measurements.domain.PlayerMeasurement;
import player.measurements.domain.PlayerMeasurements;

import java.text.DecimalFormat;
import java.util.*;

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

    /* Add player's measurements inside the list */
    public boolean addMeasurement(PlayerMeasurements playerMeasurements) {
        if (!PlayersRepository.getInstance().containsPlayer(playerMeasurements.getPlayerId())){
            System.out.println("Player " + playerMeasurements.getPlayerId() + " does not exist.");
            return false;
        }

        for (Double hrValue : playerMeasurements.getHrValues()) {
            PlayerMeasurement measurement = new PlayerMeasurement(playerMeasurements.getPlayerId(), hrValue, playerMeasurements.getTimestamp());

            synchronized (this) {
                measurements.add(measurement);
            }

            System.out.println("The " + measurement + " has been added successfully.");
        }

        return true;
    }

    /* Get the average of the last N measurements of a specific player */
    public Average getPlayerAverage(String playerId, int n) {
        if (!PlayersRepository.getInstance().containsPlayer(playerId)){
            System.out.println("Player " + playerId + " does not exist.");
            return new Average(-1);
        }

        double sum = 0; int count = 0;

        synchronized (this) {
            for (int i = measurements.size() - 1; i > -1; i--) {
                if (n == 0) break;

                if (measurements.get(i).getPlayerId().equals(playerId)){
                    sum += measurements.get(i).getHrValue();
                    count++;
                    n--;
                }
            }
        }

        if (count == 0) return new Average(0);

        double result = sum / count;
        System.out.println("The average of the last " + n + " measurements of player " + playerId + " has a result of: " + new DecimalFormat("0.00").format(result));
        return new Average(result);
    }

    /* Get the average of the measurements occurred between timestamp t1 and timestamp t2 */
    public Average getIntervalAverage(long t1, long t2) {
        double result = 0;

        synchronized (this) {
            result = measurements.stream()
                    .filter(m -> m.getTimestamp() >= t1 && m.getTimestamp() <= t2)
                    .mapToDouble(PlayerMeasurement::getHrValue)
                    .average()
                    .orElse(0);
        }

        System.out.println("The average of the measurements with timestamp between " + t1 + " and " + t2 + " has a result of: " + new DecimalFormat("0.00").format(result));
        return new Average(result);
    }
}