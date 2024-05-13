package player.smartwatch.handlers.consumer;

import player.smartwatch.buffers.Buffer;
import player.smartwatch.buffers.implementations.SendBuffer;
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
            Double average = getAverage(productionBuffer.readAllAndClean());
            System.out.println("Your current average heart rate is " + average + ".");
            sendBuffer.add(average);
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