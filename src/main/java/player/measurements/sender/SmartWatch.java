package player.measurements.sender;

import com.sun.jersey.api.client.ClientResponse;
import player.measurements.buffer.Buffer;
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
            if (!senderBuffer.isEmpty()) {
                List<Double> averages = senderBuffer.readAllAndClean();
                ClientResponse response = sendMeasurements(new PlayerMeasurements(playerId, averages, System.currentTimeMillis()));
                if(response == null) {
                    System.out.println("Server not available");
                } else {
                    System.out.println("Measurements successfully sent");
                }
            }
            try {
                Thread.sleep(10000);
            } catch (InterruptedException e) {
                throw new RuntimeException(e);
            }
        }
    }

    /* Call remote's method in order to perform the http request */
    private ClientResponse sendMeasurements(PlayerMeasurements measurements) {
        return MeasurementsRemote.getInstance().requestAddMeasurements(serverAddress, measurements);
    }
}
