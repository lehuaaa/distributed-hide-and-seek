package player;

import administration.server.beans.MatchInfo;
import administration.server.beans.Player;
import player.simulator.HRSimulator;
import player.simulator.buffer.Buffer;
import player.simulator.buffer.implementation.BufferImpl;

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