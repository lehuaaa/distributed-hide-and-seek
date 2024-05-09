package player.measurements.buffer.implementation;

import java.util.ArrayList;
import java.util.List;

public class SenderBuffer {
    private final List<Double> list;

    public SenderBuffer() {
        list = new ArrayList<>();
    }

    public void addAll(List<Double> averages) {
        list.addAll(averages);
    }

    public List<Double> readAllAndClean() {
        List<Double> measurements = new ArrayList<>(list);
        list.clear();
        return measurements;
    }

    public boolean isEmpty() {
        return list.isEmpty();
    }
}