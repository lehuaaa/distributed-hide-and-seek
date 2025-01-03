package player.measurements.handlers;

import com.sun.jersey.api.client.ClientResponse;
import player.measurements.buffers.implementations.SendBuffer;
import player.measurements.domain.PlayerMeasurements;
import utils.remotes.MeasurementsRemote;

import java.util.List;

public class MeasurementsSender extends Thread {

    private final String playerId;
    private final String serverAddress;
    private final SendBuffer sendBuffer;

    public MeasurementsSender(String playerId, String serverAddress, SendBuffer sendBuffer) {
        this.playerId = playerId;
        this.serverAddress = serverAddress;
        this.sendBuffer = sendBuffer;
    }

    @Override
    public void run() {
        while (true) {
            List<Double> averages = sendBuffer.readAllAndClean();

            if (!averages.isEmpty()) {
                sendMeasurements(new PlayerMeasurements(playerId, averages, System.currentTimeMillis()));
            }

            try { Thread.sleep(10000); } catch (InterruptedException e) { throw new RuntimeException(e); }
        }
    }

    /* Call remote's method in order to perform the http request */
    private ClientResponse sendMeasurements(PlayerMeasurements measurements) {
        return MeasurementsRemote.getInstance().requestAddMeasurements(serverAddress, measurements);
    }
}