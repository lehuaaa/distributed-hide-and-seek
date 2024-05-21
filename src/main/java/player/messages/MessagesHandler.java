package player.messages;

import org.eclipse.paho.client.mqttv3.*;
import player.game.domain.singletons.Player;
import player.game.domain.enums.GameState;
import player.game.handlers.ElectionHandler;
import utils.factories.MqttFactory;

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
                        Player.getInstance().setState(GameState.ELECTION);

                        System.out.println();
                        System.out.println("0. Election phase!");

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