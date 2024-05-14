package player.mqtt;

import org.eclipse.paho.client.mqttv3.*;

public class MqttMessagesHandler {

    private final String mqttBrokerAddress;
    private final MqttClient mqttClient;

    public MqttMessagesHandler(String mqttBrokerAddress) {
        this.mqttBrokerAddress = mqttBrokerAddress;
        this.mqttClient = createMqttClient();
    }

    public void start() {
        if (mqttClient != null) {
            mqttClient.setCallback(new MqttCallback() {
                @Override
                public void connectionLost(Throwable throwable) {
                    System.out.println("You cannot longer receiver messages from the gamer manager");
                    throw new RuntimeException(throwable);
                }

                @Override
                public void messageArrived(String topic, MqttMessage message) {
                    if (topic.equals("game/start")) {
                        System.out.println("The game manager started the game!");
                    } else {
                        System.out.println("Game manager: " + message.toString());
                    }
                }

                @Override
                public void deliveryComplete(IMqttDeliveryToken iMqttDeliveryToken) {
                    System.out.println("Message successfully sent!");
                }
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