package utils.factories;

import org.eclipse.paho.client.mqttv3.MqttClient;
import org.eclipse.paho.client.mqttv3.MqttConnectOptions;
import org.eclipse.paho.client.mqttv3.MqttException;

public class MqttFactory {

    private static final String mqttBrokerAddress = "tcp://localhost:1883";

    public static MqttClient generateClient(String topics) {

        try {
            MqttClient mqttClient = new MqttClient(mqttBrokerAddress, MqttClient.generateClientId());
            MqttConnectOptions connectOptions = new MqttConnectOptions();
            connectOptions.setCleanSession(true);
            mqttClient.connect(connectOptions);

            if (!topics.isEmpty()) {
                mqttClient.subscribe(topics, 2);
            }

            return mqttClient;

        } catch (MqttException e) {
            System.out.println("Mqtt broker is not available");
            throw new RuntimeException(e);
        }
    }
}
