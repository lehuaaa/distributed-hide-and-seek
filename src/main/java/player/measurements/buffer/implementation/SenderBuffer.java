package player.measurements.buffer.implementation;

import java.util.ArrayList;
import java.util.List;

public class SenderBuffer {
    private final List<Double> queue;

    public SenderBuffer() {
        queue = new ArrayList<>();
    }

    public synchronized void add(Double average) {
        queue.add(average);
    }

    public synchronized List<Double> readAllAndClean() {
        if (queue.isEmpty()) {
            return new ArrayList<>();
        }

        List<Double> measurements = new ArrayList<>(queue);
        queue.clear();
        return measurements;
    }

    public synchronized void addAll(List<Double> averages) {
        queue.addAll(averages);
    }
}