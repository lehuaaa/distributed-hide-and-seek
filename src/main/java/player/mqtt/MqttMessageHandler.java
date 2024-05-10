package player.mqtt;

import org.eclipse.paho.client.mqttv3.*;

public class MqttMessageHandler {

    private final String mqttBrokerAddress;
    private final MqttClient mqttClient;

    public MqttMessageHandler(String mqttBrokerAddress) {
        this.mqttBrokerAddress = mqttBrokerAddress;
        this.mqttClient = generateMqttClient();
        if (mqttClient != null) {
            startListening();
        } else {
            System.out.println("You can't receive messages form the game manager");
        }
    }

    private void startListening() {
        mqttClient.setCallback(new MqttCallback() {
            @Override
            public void connectionLost(Throwable throwable) {
                System.out.println("You cannot longer receiver messages from the gamer manager");
            }

            @Override
            public void messageArrived(String topic, MqttMessage message) throws Exception {
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

    private MqttClient generateMqttClient() {
        try {
            MqttClient mqttClient = new MqttClient(mqttBrokerAddress, MqttClient.generateClientId());
            MqttConnectOptions connectOptions = new MqttConnectOptions();
            connectOptions.setCleanSession(true);
            mqttClient.connect(connectOptions);
            mqttClient.subscribe(new String[]{"game/start", "game/messages"});
            return mqttClient;
        } catch (MqttException e) {
            System.out.println("Unfortunately the player failed to connect to mqtt broker :(");
            return null;
        }
    }
}