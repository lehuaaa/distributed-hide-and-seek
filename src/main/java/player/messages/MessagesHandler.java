package player.messages;

import org.eclipse.paho.client.mqttv3.*;
import player.domain.Player;
import player.domain.enums.GameState;
import player.game.handlers.ElectionHandler;
import util.factories.MqttFactory;

public class MessagesHandler extends Thread {

    private final MqttClient mqttClient;

    public MessagesHandler() {
        this.mqttClient = MqttFactory.generateMqttClient("game/#");
    }

    @Override
    public void run() {
        mqttClient.setCallback(new MqttCallback() {

            @Override
            public void connectionLost(Throwable throwable) {
                throw new RuntimeException(throwable);
            }

            @Override
            public void messageArrived(String topic, MqttMessage message) {
                if (topic.equals("game/start")) {
                    if (Player.getInstance().getState() == GameState.INIT) {
                        System.out.println();
                        System.out.println("0. Election phase!");

                        Player.getInstance().setState(GameState.ELECTION);
                        ElectionHandler.getInstance().start();
                    }
                } else {
                    System.out.println("Game manager: " + message.toString());
                }
            }

            @Override
            public void deliveryComplete(IMqttDeliveryToken iMqttDeliveryToken) { }

        });
    }
}