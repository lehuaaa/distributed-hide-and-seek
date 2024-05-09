package player;

import administration.server.entities.Client;
import administration.server.entities.MatchInfo;
import com.sun.jersey.api.client.ClientResponse;
import player.measurements.buffer.Buffer;
import player.measurements.buffer.implementation.ProducerBuffer;
import player.measurements.buffer.implementation.SenderBuffer;
import player.measurements.handler.MeasurementsHandler;
import player.measurements.simulator.HRSimulator;
import player.measurements.sender.SmartWatch;
import player.models.Player;
import util.checker.StringChecker;
import util.remote.PlayersRemote;

import javax.ws.rs.core.Response;
import java.util.Random;
import java.util.Scanner;

public class StartPlayer {

    private static final String address = "localhost";
    private static final String serverAddress = "http://localhost:8080";

    public static void main(String[] args) {
        Scanner scanner = new Scanner(System.in);

        /* Get player's id*/
        System.out.println("Insert your player Id:");
        String playerId = scanner.nextLine();
        while (playerId.isEmpty() || StringChecker.containsIllegalsCharacters(playerId)) {
            System.out.println("The entered id is not valid, try with another one:");
            playerId = scanner.nextLine();
        }

        /* Set player's listening port */
        int listeningPort = 8081 + new Random().nextInt(200);

        /* Client registration */
        Client client = new Client(playerId, address, listeningPort);
        ClientResponse response = PlayersRemote.getInstance().requestAddPlayer(serverAddress, client);

        if (response == null) {
            System.exit(0);
        }

        while (response.getStatus() == Response.Status.BAD_REQUEST.getStatusCode()) {
            System.out.println("Unfortunately id" + playerId + " has already been taken, try with another one:");
            playerId = scanner.nextLine();
            while (playerId.isEmpty() || StringChecker.containsIllegalsCharacters(playerId)) {
                System.out.println("The entered id is not valid, try with another one:");
                playerId = scanner.nextLine();
            }
            response = PlayersRemote.getInstance().requestAddPlayer(serverAddress, client);
            if (response == null) {
                System.exit(0);
            }
        }

        /* Player initialization */
        MatchInfo info = response.getEntity(MatchInfo.class);
        Player player = new Player(client, serverAddress, info.getCoordinate(), info.getOtherPlayers());

        System.out.println("You have registered successfully!");
        System.out.println("Your position on the map is: " + player.getCoordinate().toString());

        /* Buffer used to store the HRvalues produced by the simulator */
        Buffer productionBuffer = new ProducerBuffer();

        /* Buffer used to store the measurements produced by the measurementsHandler */
        SenderBuffer senderBuffer = new SenderBuffer();

        /* Threads that produce, compute and send the list of measurements */
        HRSimulator hrSimulator = new HRSimulator(productionBuffer);
        MeasurementsHandler measurementsHandler = new MeasurementsHandler(productionBuffer, senderBuffer);
        SmartWatch smartWatch = new SmartWatch(playerId, serverAddress, senderBuffer);

        hrSimulator.start();
        measurementsHandler.start();
        smartWatch.start();
    }
}