package administration.client;

import administration.server.beans.Average;
import administration.server.beans.Node;
import administration.server.repositories.PlayersRepository;
import com.sun.jersey.api.client.ClientResponse;
import org.eclipse.paho.client.mqttv3.*;
import utils.factories.MqttFactory;
import utils.remotes.MeasurementsRemote;
import utils.remotes.PlayersRemote;

import javax.ws.rs.core.Response;
import java.text.DecimalFormat;
import java.util.InputMismatchException;
import java.util.List;
import java.util.Scanner;
import java.util.regex.Pattern;

public class AdministrationClient {

    private static final Pattern pattern  = Pattern.compile("[-._~:/?*#\\[\\]\"@!$&'()+,;=\\s^]");
    private final static String administrationServerAddress = "http://localhost:8080";
    private static MqttClient mqttClient;

    public static void main(String[] args) {

        System.out.println("Welcome to the game manager console!");

        mqttClient = MqttFactory.generateClient("");
        Scanner scanner = new Scanner(System.in);
        ClientResponse response;
        String choice;

        do {
            System.out.println();
            System.out.println();
            System.out.println("Select an option:");
            System.out.println("1 - Start the watchOut match");
            System.out.println("2 - Send a message to all the player in the match");
            System.out.println("3 - Show all the player currently in the game");
            System.out.println("4 - Get the average of the last N heart rate values by a given player");
            System.out.println("5 - Get the average of the heart rate values that occurred between timestamp t1 and timestamp t2");
            System.out.println("6 - Exit");

            choice = scanner.nextLine();

            System.out.println();
            System.out.println();

            switch (choice) {


                /* Start the watchOut match */
                case "1":
                    sendMqttMessage("game/start", "start", 2, true, "Match is starting, the players are now identifying the seeker!");
                    break;


                /* Send a message to all the player in the match */
                case "2":
                    System.out.println("Enter a message:");
                    sendMqttMessage("game/messages", scanner.nextLine(), 1, false, "Message successfully sent!");
                    break;


                /* Show all the player currently in the game */
                case "3":
                    response = PlayersRemote.getInstance().requestGetPlayers(administrationServerAddress);
                    if (response != null) {
                        List<Node> players = response.getEntity(PlayersRepository.class).getPlayers();
                        if(!players.isEmpty()) {
                            System.out.println("There are " + players.size() + " players in the match:");
                            for (int i = 0; i <  players.size(); i++) {
                                System.out.println((i + 1) + ". " + players.get(i));
                            }
                        } else {
                            System.out.println("There are no players in the match.");
                        }
                    }
                    break;


                /* Get the average of the last N heart rate values by a given player */
                case "4":
                    System.out.println("Enter player Id:");
                    String playerId = scanner.nextLine();
                    while (playerId.isEmpty() || pattern.matcher(playerId).find()) {
                        System.out.println("The entered Id is not valid, please try with another one.");
                        playerId = scanner.nextLine();
                    }

                    System.out.println("Enter number of measurements:");
                    int n = 0;
                    while (n < 1) {
                        try {
                            n = scanner.nextInt();
                            if (n < 1) {
                                System.out.println("The number must be grater than 0, please try with another one.");
                            }
                        } catch (InputMismatchException e) {
                            System.out.println("The entered number is not valid, please try with another one.");
                        }
                        scanner.nextLine();
                    }

                    response = MeasurementsRemote.getInstance().requestGetPlayerAverage(administrationServerAddress, playerId, n);
                    if (response != null) {
                        if (response.getStatus() == Response.Status.OK.getStatusCode()) {
                            System.out.println("The average of the last " + n + " measurements of player " + playerId + " is " + new DecimalFormat("0.00").format(response.getEntity(Average.class).getResult()));
                        } else {
                            System.out.println("The player with Id " + playerId + " was not found.");
                        }
                    }
                    break;


                /* Get the average of the heart rate values that occurred between timestamp t1 and timestamp t2 */
                case "5":
                    System.out.println("Enter first timestamp:");
                    long t1 = -1;
                    while (t1 < 0) {
                        try {
                            t1 = scanner.nextInt();
                            if (t1 < 0) {
                                System.out.println("The timestamp must be grater equal than 0, please try with another one.");
                            }
                        } catch (InputMismatchException e) {
                            System.out.println("The entered timestamp is not valid, please try with another one.");
                        }
                        scanner.nextLine();
                    }

                    System.out.println("Enter second timestamp:");
                    long t2 = -1;
                    while (t2 < 0 || t1 > t2) {
                        try {
                            t2 = scanner.nextLong();
                            if (t2 < 0) {
                                System.out.println("The timestamp must be grater equal than 0, please try with another one.");
                            } else if (t1 > t2) {
                                System.out.println("The second timestamp must be grater than the first one, please try with another one.");
                            }
                        } catch (InputMismatchException e) {
                            System.out.println("The entered timestamp is not valid, please try with another one.");
                        }
                        scanner.nextLine();
                    }

                    response = MeasurementsRemote.getInstance().requestGetIntervalAverage(administrationServerAddress, t1, t2);
                    if (response != null) {
                        System.out.println("The average of the measurements that occurred between " + t1 + " and " + t2 + " is " + new DecimalFormat("0.00").format(response.getEntity(Average.class).getResult()));
                    }
                    break;


                /* Exit */
                case "6":
                    System.out.println("The console is shutting down.");
                    break;


                /* Invalid choice */
                default:
                    System.out.println("Invalid choice, please try again.");
                    break;
            }
        } while (!choice.equals("6"));

        closeMqttConnection();
    }

    private static void sendMqttMessage(String topic, String message, int qos, boolean isRetained, String messageToShow) {
        MqttMessage mqttMessage = new MqttMessage(message.getBytes());
        mqttMessage.setQos(qos);
        mqttMessage.setRetained(isRetained);
        try {
            mqttClient.publish(topic, mqttMessage);
            System.out.println(messageToShow);
        } catch (MqttException e) {
            System.out.println("The mqtt broker is not available.");
            throw new RuntimeException(e);
        }
    }

    private static void closeMqttConnection() {
        try {
            mqttClient.disconnect();
            mqttClient.close();
        } catch (MqttException e) {
            throw new RuntimeException(e);
        }
    }
}