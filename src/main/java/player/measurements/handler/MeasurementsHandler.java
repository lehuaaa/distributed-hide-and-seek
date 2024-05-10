package player.measurements.handler;

import player.measurements.buffer.Buffer;
import player.measurements.buffer.implementation.SenderBuffer;
import player.measurements.model.Measurement;

import java.util.ArrayList;
import java.util.List;

public class MeasurementsHandler extends Thread{

    private final SenderBuffer senderBuffer;
    private final Buffer producerBuffer;

    public MeasurementsHandler(Buffer producerBuffer, SenderBuffer senderBuffer) {
        this.producerBuffer = producerBuffer;
        this.senderBuffer = senderBuffer;
    }

    @Override
    public void run() {
        while (true) {
              Double average = getAverage(producerBuffer.readAllAndClean());
              senderBuffer.addAverage(average);
        }
    }

    /* Compute the average */
    private Double getAverage(List<Measurement> measurements) {
        return measurements.stream()
                .mapToDouble(Measurement::getValue)
                .average()
                .orElse(0);
   }
}
