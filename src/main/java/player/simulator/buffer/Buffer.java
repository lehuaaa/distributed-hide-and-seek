package player.simulator.buffer;

import player.simulator.models.Measurement;

import java.util.List;

public interface Buffer {

    void addMeasurement(Measurement m);

    List<Measurement> readAllAndClean();

}
