package player.simulator.buffer.implementation;

import player.simulator.buffer.Buffer;
import player.simulator.models.Measurement;

import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;
import java.util.Queue;

public class BufferImpl implements Buffer {

    private final Queue<Measurement> queue;
    private final int maxSize = 8;

    public BufferImpl() {
        queue = new LinkedList<>();
    }

    @Override
    public void addMeasurement(Measurement m) {
        while (queue.size() == maxSize) {
            try {
                wait();
            } catch (InterruptedException e) {
                throw new RuntimeException(e);
            }
        }

        queue.add(m);

        if (queue.size() == maxSize)
            notifyAll();
    }

    @Override
    public List<Measurement> readAllAndClean() {
        while (queue.size() < maxSize) {
            try {
                wait();
            } catch (InterruptedException e) {
                throw new RuntimeException(e);
            }
        }

        List<Measurement> measurements = new ArrayList<>(queue);
        queue.clear();
        notifyAll();
        return measurements;
    }
}
