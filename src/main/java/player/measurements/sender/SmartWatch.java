package player.measurements.sender;

import player.measurements.buffer.implementation.SenderBuffer;
import player.measurements.model.PlayerMeasurements;
import util.remote.MeasurementsRemote;

import java.util.List;

public class SmartWatch extends Thread {

    private final String playerId;
    private final String serverAddress;
    private final SenderBuffer senderBuffer;

    public SmartWatch(String playerId, String serverAddress, SenderBuffer senderBuffer) {
        this.playerId = playerId;
        this.serverAddress = serverAddress;
        this.senderBuffer = senderBuffer;
    }

    @Override
    public void run() {
        while (true) {
            List<Double> averages = senderBuffer.readAllAndClean();
            if (!averages.isEmpty())
                sendMeasurements(new PlayerMeasurements(playerId, averages, System.currentTimeMillis()));
            try {
                Thread.sleep(10000);
            } catch (InterruptedException e) {
                throw new RuntimeException(e);
            }
        }
    }

    /* Call remote's method in order to perform the http request */
    private void sendMeasurements(PlayerMeasurements measurements) {
        MeasurementsRemote.getInstance().requestAddMeasurements(serverAddress, measurements);
    }
}