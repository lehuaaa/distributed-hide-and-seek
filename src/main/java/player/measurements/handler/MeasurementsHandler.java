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
              List<Double> averages = getListOfAverages(producerBuffer.readAllAndClean());
              senderBuffer.addAll(averages);
        }
    }

    /* Compute the average using sliding window approach. overlap factor: 50% */
    private List<Double> getListOfAverages(List<Measurement> measurements) {
        List<Double> averages = new ArrayList<>();
        double windowSum = 0;
        double overlapFactor = 0.5;
        int windowSize = 4;
        int stride = (int) (windowSize * overlapFactor);

        for (int i = 0; i < windowSize; i++) {
            windowSum += measurements.get(i).getValue();
        }

        averages.add(windowSum / windowSize);

        for (int i = stride; i < measurements.size() - windowSize + 1; i += stride) {
            windowSum -= measurements.get(i - 1).getValue();
            windowSum -= measurements.get(i - 2).getValue();
            windowSum += measurements.get(i + windowSize - 1).getValue();
            windowSum += measurements.get(i + windowSize - 2).getValue();
            averages.add(windowSum / windowSize);
        }

        return averages;
   }
}
