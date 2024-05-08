package player.measurements.consumer;

import player.measurements.buffer.Buffer;
import player.measurements.model.Measurement;

import java.util.List;

public class MeasurementsHandler extends Thread{

    private final Buffer buffer;

    public MeasurementsHandler(Buffer buffer) {
        this.buffer = buffer;
    }

    @Override
    public void run() {
        while (true) {
            List<Measurement> measurementList = buffer.readAllAndClean();
            /* Compute sliding window approach */
        }
    }

}
