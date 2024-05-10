package player.measurements.buffer.implementation;

import java.util.ArrayList;
import java.util.List;

public class SenderBuffer {
    private final List<Double> list;

    public SenderBuffer() {
        list = new ArrayList<>();
    }

    public synchronized void addAverage(Double average) {
        list.add(average);
    }

    public synchronized List<Double> readAllAndClean() {
        List<Double> measurements = new ArrayList<>(list);
        list.clear();
        return measurements;
    }
}