package player.measurements.buffers;

import player.measurements.domain.Measurement;

import java.util.List;

public interface Buffer {

    void addMeasurement(Measurement m);

    List<Measurement> readAllAndClean();

}