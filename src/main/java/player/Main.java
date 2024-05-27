package player;

import administration.server.beans.Node;
import administration.server.beans.GameInfo;
import com.sun.jersey.api.client.ClientResponse;
import player.game.domain.singletons.Player;
import player.game.GrpcServer;
import player.game.handlers.InformationHandler;
import player.measurements.buffers.Buffer;
import player.measurements.buffers.implementations.ProductionBuffer;
import player.measurements.buffers.implementations.SendBuffer;
import player.measurements.handlers.MeasurementsConsumer;
import player.measurements.handlers.HRSimulator;
import player.measurements.handlers.MeasurementsSender;
import player.messages.MessagesHandler;
import utils.remotes.PlayersRemote;

import javax.ws.rs.core.Response;
import java.util.Random;
import java.util.Scanner;
import java.util.regex.Pattern;

public class Main {

    private static final Pattern pattern  = Pattern.compile("[-._~:/?*#\\[\\]\"@!$&'()+,;=\\s^]");
    private static final String serverAddress = "http://localhost:8080";
    private static final String address = "localhost";
    private static final Scanner scanner = new Scanner(System.in);

    public static void main(String[] args) {

        /* Get player's id*/
        System.out.println("Enter your player id:");
        String playerId = scanner.nextLine();
        while (playerId.isEmpty() || pattern.matcher(playerId).find()) {
            System.out.println("The entered id is not valid, please try with another one.");
            playerId = scanner.nextLine();
        }


        /* Set player's listening port */
        int listeningPort = 8081 + new Random().nextInt(1000);


        /* Node registration */
        Node node = new Node(playerId, address, listeningPort);
        ClientResponse response = PlayersRemote.getInstance().requestAddPlayer(serverAddress, node);

        while (response == null || response.getStatus() == Response.Status.BAD_REQUEST.getStatusCode()) {
            if (response != null)
                System.out.println("Unfortunately id" + playerId + " has already been taken, please try with another one.");

            playerId = scanner.nextLine();
            while (playerId.isEmpty() || pattern.matcher(playerId).find()) {
                System.out.println("The entered id is not valid, please try with another one.");
                playerId = scanner.nextLine();
            }
            node.setId(playerId);
            response = PlayersRemote.getInstance().requestAddPlayer(serverAddress, node);
        }


        /* Player initialization */
        GameInfo info = response.getEntity(GameInfo.class);
        Player player = Player.getInstance();

        /* Insert a new node with the minimum distance from the base and the highest ID
        if (playerId.equals("9")) {
            player.init(node, serverAddress, new Coordinate(4, 9), info.getOtherPlayers());
            System.out.println("You joined the game at position " + new Coordinate(4, 9));
        } else {
            INTEGRATE THE 2 LINES OF CODE BELOW INSIDE THIS ELSE
        } */

        player.init(node, serverAddress, info.getCoordinate(), info.getOtherPlayers());
        System.out.println("You joined the game at position " + info.getCoordinate());

        if (info.getOtherPlayers().isEmpty()) {
            System.out.println("You are the only one in the game.");
        } else {
            System.out.println(info.getOtherPlayers().size() == 1
                            ? "There is 1 other player in the game."
                            : "There are " + info.getOtherPlayers().size() + " other players in the game.");
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


        /* Start Grpc sever */
        GrpcServer.getInstance().start(player.getPort());
        InformationHandler.getInstance().start();


        /* Start MqttMessagesHandler */
        new MessagesHandler().start();
    }
}