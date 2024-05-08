package player;

import administration.server.entities.MatchInfo;
import administration.server.entities.Player;
import player.measurements.producer.HRSimulator;
import player.measurements.buffer.Buffer;
import player.measurements.buffer.implementation.BufferImpl;

public class WatchOutPlayer extends Player {

    private static Buffer measurements;
    private static HRSimulator hrSimulator;
    private MatchInfo matchInfo;

    public WatchOutPlayer(String id, String address, int port) {
        super(id, address, port);
        Buffer buffer = new BufferImpl();
        hrSimulator = new HRSimulator(buffer);
        /* MeasurementConsumer */
    }
}