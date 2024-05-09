package player;

import administration.server.entities.Player;
import com.sun.jersey.api.client.ClientResponse;
import player.measurements.buffer.Buffer;
import player.measurements.buffer.implementation.ProducerBuffer;
import player.measurements.buffer.implementation.SenderBuffer;
import player.measurements.handler.MeasurementsHandler;
import player.measurements.simulator.HRSimulator;
import player.measurements.sender.SmartWatch;
import util.remote.PlayersRemote;

import javax.ws.rs.core.Response;
import java.util.InputMismatchException;
import java.util.Scanner;

public class Main {

    private static final String address = "localhost";
    private static final String serverAddress = "http://localhost:8080";

    public static void main(String[] args) {
        Scanner scanner = new Scanner(System.in);


        /* Get player's id*/
        System.out.println("Insert your player Id:");
        String playerId = scanner.nextLine();
        while (playerId.isEmpty()) {
            System.out.println("The entered id is not valid, try with another one:");
            playerId = scanner.nextLine();
        }


        /* Get player's listening port */
        System.out.println("Insert your listening port:");
        int listeningPort = -1;
        while (listeningPort == -1) {
            try {
                listeningPort = scanner.nextInt();
            } catch (InputMismatchException e) {
                System.out.println("The entered listening port is not valid, try with another one:");
                scanner.nextLine();
            }
        }


        /* Player initialization */
        Player player = new Player(playerId, address, listeningPort);


        /* Player registration request */
        ClientResponse response = PlayersRemote.getInstance().requestAddPlayer(serverAddress, player);

        if (response == null) {
            System.out.println("Server not available");
            System.exit(0);
        }

        while (response.getStatus() == Response.Status.BAD_REQUEST.getStatusCode()) {
            System.out.println("Unfortunately id" + playerId + " has already been taken, try with another one:");
            player.setId(scanner.nextLine());
            response = PlayersRemote.getInstance().requestAddPlayer(serverAddress, player);
            if (response == null) {
                System.out.println("Server not available");
                System.exit(0);
            }
        }

        System.out.println("You have registered successfully!");


        /* Buffer used to store the HRvalues product by the simulator */
        Buffer productionBuffer = new ProducerBuffer();


        /* Buffer used to store the meas product by the simulator */
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