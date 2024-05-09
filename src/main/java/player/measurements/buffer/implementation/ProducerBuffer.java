package player.measurements.buffer.implementation;

import player.measurements.buffer.Buffer;
import player.measurements.model.Measurement;

import java.util.ArrayList;
import java.util.List;

public class ProducerBuffer implements Buffer {

    private final List<Measurement> list;
    private final int maxSize = 8;

    public ProducerBuffer() {
        list = new ArrayList<>();
    }

    @Override
    public synchronized void addMeasurement(Measurement m) {
        while (list.size() == maxSize) {
            try {
                wait();
            } catch (InterruptedException e) {
                throw new RuntimeException(e);
            }
        }

        list.add(m);

        if (list.size() == maxSize)
            notify();
    }

    @Override
    public synchronized List<Measurement> readAllAndClean() {
        while (list.size() < maxSize) {
            try {
                wait();
            } catch (InterruptedException e) {
                throw new RuntimeException(e);
            }
        }

        List<Measurement> measurements = new ArrayList<>(list);
        list.clear();
        notifyAll();
        return measurements;
    }
}
