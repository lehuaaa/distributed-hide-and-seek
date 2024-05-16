package player.mqtt;

import org.eclipse.paho.client.mqttv3.*;
import player.domain.Player;
import player.grpc.handlers.ElectionHandler;

public class MqttHandler {

    private final String mqttBrokerAddress;
    private final MqttClient mqttClient;

    public MqttHandler(String mqttBrokerAddress) {
        this.mqttBrokerAddress = mqttBrokerAddress;
        this.mqttClient = createMqttClient();
    }

    public void start() {
        if (mqttClient != null) {
            mqttClient.setCallback(new MqttCallback() {

                @Override
                public void connectionLost(Throwable throwable) {
                    throw new RuntimeException(throwable);
                }

                @Override
                public void messageArrived(String topic, MqttMessage message) {
                    if (topic.equals("game/start")) {
                        if (!Player.getInstance().isInGame) {
                            Player.getInstance().isInGame = true;
                            System.out.println("The election phase has begun!");
                            ElectionHandler.getInstance().forwardMessage("ELECTION", Player.getInstance().getId(), Player.getInstance().getCoordinate().getDistanceFromBase());
                        }
                    } else {
                        System.out.println("Game manager: " + message.toString());
                    }
                }

                @Override
                public void deliveryComplete(IMqttDeliveryToken iMqttDeliveryToken) {}

            });
        }
    }

    private MqttClient createMqttClient() {
        try {
            MqttClient mqttClient = new MqttClient(mqttBrokerAddress, MqttClient.generateClientId());
            MqttConnectOptions connectOptions = new MqttConnectOptions();
            connectOptions.setCleanSession(true);
            mqttClient.connect(connectOptions);
            mqttClient.subscribe("game/#");
            return mqttClient;
        } catch (MqttException e) {
            System.out.println("Unfortunately the player failed to connect to mqtt broker.");
            throw new RuntimeException(e);
        }
    }
}