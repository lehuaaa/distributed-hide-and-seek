package player;

import administration.server.beans.Node;
import administration.server.beans.MatchInfo;
import com.sun.jersey.api.client.ClientResponse;
import player.domain.OtherPlayer;
import player.smartwatch.buffers.Buffer;
import player.smartwatch.buffers.implementations.ProductionBuffer;
import player.smartwatch.buffers.implementations.SendBuffer;
import player.smartwatch.handlers.consumer.MeasurementsConsumer;
import player.smartwatch.handlers.producer.HRSimulator;
import player.smartwatch.handlers.sender.MeasurementsSender;
import player.domain.Player;
import player.mqtt.MqttHandler;
import util.checker.StringChecker;
import util.remote.PlayersRemote;

import javax.ws.rs.core.Response;
import java.util.Random;
import java.util.Scanner;

public class PlayerMain {

    private static final String address = "localhost";
    private static final String serverAddress = "http://localhost:8080";
    private static final String mqttServerAddress = "tcp://localhost:1883";

    public static void main(String[] args) {
        Scanner scanner = new Scanner(System.in);


        /* Get player's id*/
        System.out.println("Enter your player Id:");
        String playerId = scanner.nextLine();
        while (playerId.isEmpty() || StringChecker.containsIllegalsCharacters(playerId)) {
            System.out.println("The entered id is not valid, please try with another one.");
            playerId = scanner.nextLine();
        }


        /* Set player's listening port */
        int listeningPort = 8081 + new Random().nextInt(200);


        /* Node registration */
        Node node = new Node(playerId, address, listeningPort);
        ClientResponse response = PlayersRemote.getInstance().requestAddPlayer(serverAddress, node);

        while (response == null || response.getStatus() == Response.Status.BAD_REQUEST.getStatusCode()) {
            if (response != null)
                System.out.println("Unfortunately id" + playerId + " has already been taken, please try with another one.");

            playerId = scanner.nextLine();
            while (playerId.isEmpty() || StringChecker.containsIllegalsCharacters(playerId)) {
                System.out.println("The entered id is not valid, please try with another one.");
                playerId = scanner.nextLine();
            }
            response = PlayersRemote.getInstance().requestAddPlayer(serverAddress, node);
        }


        /* Player initialization */
        MatchInfo info = response.getEntity(MatchInfo.class);
        Player player = Player.getInstance();
        player.init(node, serverAddress, info.getCoordinate(), info.getOtherPlayers());

        System.out.println("You joined the game, your position is " + player.getCoordinate().toString() );


        /* Buffer used to store the HRvalues produced by the hrSimulator */
        Buffer productionBuffer = new ProductionBuffer(4);


        /* Buffer used to store the measurements produced by the MeasurementsConsumer */
        SendBuffer sendBuffer = new SendBuffer();


        /* Threads that produce, consume and send the list of measurements */
        HRSimulator hrSimulator = new HRSimulator(productionBuffer);
        MeasurementsConsumer measurementsConsumer = new MeasurementsConsumer(productionBuffer, sendBuffer);
        MeasurementsSender measurementsSender = new MeasurementsSender(playerId, serverAddress, sendBuffer);

        hrSimulator.start();
        measurementsConsumer.start();
        measurementsSender.start();


        /* Initialize MqttClient */
        MqttHandler mqttHandler = new MqttHandler(mqttServerAddress);
    }
}