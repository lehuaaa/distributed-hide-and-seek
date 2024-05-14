package player;

import administration.server.beans.Node;
import administration.server.beans.MatchInfo;
import com.sun.jersey.api.client.ClientResponse;
import player.domain.Player;
import player.grpc.GrpcServer;
import player.smartwatch.buffers.Buffer;
import player.smartwatch.buffers.implementations.ProductionBuffer;
import player.smartwatch.buffers.implementations.SendBuffer;
import player.smartwatch.handlers.consumer.MeasurementsConsumer;
import player.smartwatch.handlers.producer.HRSimulator;
import player.smartwatch.handlers.sender.MeasurementsSender;
import player.mqtt.MqttMessagesHandler;
import util.checker.StringChecker;
import util.remote.PlayersRemote;

import javax.ws.rs.core.Response;
import java.util.Random;
import java.util.Scanner;

public class Main {

    private static final String address = "localhost";
    private static final String serverAddress = "http://localhost:8080";
    private static final String mqttServerAddress = "tcp://localhost:1883";
    private static final Scanner scanner = new Scanner(System.in);

    public static void main(String[] args) {

        /* Get player's id*/
        System.out.println("Enter your player id:");
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
            node.setId(playerId);
            response = PlayersRemote.getInstance().requestAddPlayer(serverAddress, node);
        }

        /* Player initialization */
        MatchInfo info = response.getEntity(MatchInfo.class);
        Player player = Player.getInstance();
        player.init(node, serverAddress, info.getCoordinate(), info.getOtherPlayers());
        System.out.println("You joined the game at position " + info.getCoordinate());

        if (info.getOtherPlayers().isEmpty()) {
            System.out.println("You are the only one in the game.");
        } else if (info.getOtherPlayers().size() == 1) {
            System.out.println("There is 1 other player in the game.");
        } else {
            System.out.println("There are " + info.getOtherPlayers().size() + " other players in the game.");
        }

        /* Buffers that store produced measurements and measurements to be sent to the server */
        Buffer productionBuffer = new ProductionBuffer(4);
        SendBuffer sendBuffer = new SendBuffer();

        /* Start threads that produce, consume and send the list of measurements */
        HRSimulator hrSimulator = new HRSimulator(productionBuffer);
        MeasurementsConsumer measurementsConsumer = new MeasurementsConsumer(productionBuffer, sendBuffer);
        MeasurementsSender measurementsSender = new MeasurementsSender(playerId, serverAddress, sendBuffer);
        hrSimulator.start();
        measurementsConsumer.start();
        measurementsSender.start();

        /* Present itself to the otherPlayers */
        GrpcServer.getInstance().start(player.getPort());

        /* Start mqttMessagesHandler */
        MqttMessagesHandler mqttMessagesHandler = new MqttMessagesHandler(mqttServerAddress);
        mqttMessagesHandler.start();
    }
}