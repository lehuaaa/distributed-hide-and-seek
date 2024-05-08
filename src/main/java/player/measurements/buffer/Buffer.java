package player.measurements.buffer;

import player.measurements.model.Measurement;

import java.util.List;

public interface Buffer {

    void addMeasurement(Measurement m);

    List<Measurement> readAllAndClean();

}
