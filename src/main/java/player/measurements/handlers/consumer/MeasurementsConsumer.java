package player.measurements.handlers.consumer;

import player.measurements.buffers.Buffer;
import player.measurements.buffers.implementations.SendBuffer;
import player.domain.Measurement;

import java.util.List;

public class MeasurementsConsumer extends Thread{

    private final SendBuffer sendBuffer;
    private final Buffer productionBuffer;

    public MeasurementsConsumer(Buffer productionBuffer, SendBuffer sendBuffer) {
        this.productionBuffer = productionBuffer;
        this.sendBuffer = sendBuffer;
    }

    @Override
    public void run() {
        while (true) {
              sendBuffer.add(getAverage(productionBuffer.readAllAndClean()));
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