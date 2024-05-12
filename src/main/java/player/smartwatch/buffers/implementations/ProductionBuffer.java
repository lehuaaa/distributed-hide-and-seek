package player.smartwatch.buffers.implementations;

import player.smartwatch.buffers.Buffer;
import player.domain.Measurement;

import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;
import java.util.Queue;

public class ProductionBuffer implements Buffer {

    private final Queue<Measurement> queue;
    private final int maxSize = 8;
    private final int slideFactor;

    public ProductionBuffer(int slideFactor) {
        this.slideFactor = slideFactor;
        queue = new LinkedList<>();
    }

    @Override
    public synchronized void addMeasurement(Measurement m) {
        while (queue.size() == maxSize) {
            try {
                wait();
            } catch (InterruptedException e) {
                throw new RuntimeException(e);
            }
        }

        queue.add(m);

        if (queue.size() == maxSize)
            notify();
    }

    @Override
    public synchronized List<Measurement> readAllAndClean() {
        while (queue.size() < maxSize) {
            try {
                wait();
            } catch (InterruptedException e) {
                throw new RuntimeException(e);
            }
        }

        List<Measurement> measurements = new ArrayList<>(queue);

        while (queue.size() > slideFactor) {
            queue.poll();
        }

        notify();
        return measurements;
    }
}